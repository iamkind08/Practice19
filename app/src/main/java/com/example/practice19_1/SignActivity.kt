package com.example.practice19_1

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class SignActivity : AppCompatActivity() {

    private lateinit var db: FirebaseDatabase
    private lateinit var layoutSignIn: LinearLayout
    private lateinit var layoutSignUp: LinearLayout

    private lateinit var loginTextView: EditText
    private lateinit var passwordTextView: EditText

    private lateinit var newLoginTextView: EditText
    private lateinit var newPasswordTextView: EditText
    private lateinit var newNameTextView: EditText
    private lateinit var newAgeTextView: EditText
    private lateinit var newStateTextView: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign)
        db = FirebaseDatabase.getInstance()

        initViews()
        setupTabs()
        // checkSignInInfo() УДАЛЁН — поля всегда пустые
    }

    private fun initViews() {
        layoutSignIn = findViewById(R.id.layoutSignIn)
        layoutSignUp = findViewById(R.id.layoutSignUp)

        loginTextView = findViewById(R.id.LoginTextView)
        passwordTextView = findViewById(R.id.PasswordTextView)

        newLoginTextView = findViewById(R.id.NewLoginTextView)
        newPasswordTextView = findViewById(R.id.NewPasswordTextView)
        newNameTextView = findViewById(R.id.NewNameTextView)
        newAgeTextView = findViewById(R.id.NewAgeTextView)
        newStateTextView = findViewById(R.id.NewStateTextView)
    }

    private fun setupTabs() {
        findViewById<View>(R.id.btnTabSignIn).setOnClickListener {
            layoutSignIn.visibility = View.VISIBLE
            layoutSignUp.visibility = View.GONE
        }
        findViewById<View>(R.id.btnTabSignUp).setOnClickListener {
            layoutSignIn.visibility = View.GONE
            layoutSignUp.visibility = View.VISIBLE
        }
    }

    private fun editTextNoNullWithAnimation(editText: EditText): Boolean {
        if (editText.text.toString().isEmpty()) {
            val animation = AnimationUtils.loadAnimation(this, R.anim.error_edit)
            editText.startAnimation(animation)
            return false
        }
        return true
    }

    fun signIn(view: View) {
        if (!editTextNoNullWithAnimation(loginTextView) or !editTextNoNullWithAnimation(passwordTextView)) return

        val login = loginTextView.text.toString()
        val password = passwordTextView.text.toString()

        val myRef = db.getReference("UsersSignInInfo").child(login)
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val dbPassword = snapshot.child("password").getValue(String::class.java)
                if (dbPassword != null && dbPassword == password) {
                    val profileId = snapshot.child("profileId").getValue(String::class.java) ?: ""
                    goNext(profileId)
                } else {
                    cantSignIn()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SignActivity, "Ошибка: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    fun signUp(view: View) {
        if (!editTextNoNullWithAnimation(newLoginTextView) or
            !editTextNoNullWithAnimation(newPasswordTextView) or
            !editTextNoNullWithAnimation(newNameTextView) or
            !editTextNoNullWithAnimation(newAgeTextView) or
            !editTextNoNullWithAnimation(newStateTextView)) return

        val login = newLoginTextView.text.toString()
        val password = newPasswordTextView.text.toString()
        val name = newNameTextView.text.toString()
        val age = Transform.parseIntOrDefault(newAgeTextView.text.toString(), 18)
        val state = newStateTextView.text.toString()

        val signal = when {
            state.contains("online", ignoreCase = true) -> 1
            state.contains("away", ignoreCase = true) ||
                    state.contains("departed", ignoreCase = true) -> 2
            else -> 0
        }

        val myRef = db.getReference("UsersSignInInfo").child(login)
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(this@SignActivity, "Пользователь существует", Toast.LENGTH_SHORT).show()
                } else {
                    val profileId = db.getReference("UsersProfileInfo").push().key ?: ""

                    val signInRef = db.getReference("UsersSignInInfo").child(login)
                    signInRef.child("password").setValue(password)
                    signInRef.child("profileId").setValue(profileId)

                    val profileRef = db.getReference("UsersProfileInfo").child(profileId)
                    profileRef.child("name").setValue(name)
                    profileRef.child("age").setValue(age)
                    profileRef.child("state").setValue(state)
                    profileRef.child("stateSignal").setValue(signal)

                    goNext(profileId)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SignActivity, "Ошибка: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun goNext(profileId: String) {
        // Больше не сохраняем логин/пароль в SharedPreferences
        val intent = Intent(this, LoadedUserDataActivity::class.java)
        intent.putExtra("PROFILE_ID", profileId)
        startActivity(intent)
        finish()
    }

    private fun cantSignIn() {
        Toast.makeText(this, "Неверный логин или пароль", Toast.LENGTH_SHORT).show()
    }
}