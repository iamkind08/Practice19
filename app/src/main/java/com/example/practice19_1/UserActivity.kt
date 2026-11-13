package com.example.practice19_1

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

class UserActivity : AppCompatActivity() {

    private lateinit var nameTextView: EditText
    private lateinit var stateTextView: EditText
    private lateinit var ageTextView: EditText
    private lateinit var activeUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        val position = intent.getIntExtra(UserStaticInfo.POSITION, 0)
        activeUser = UserStaticInfo.users[position]

        nameTextView = findViewById(R.id.NameTextView)
        stateTextView = findViewById(R.id.StateTextView)
        ageTextView = findViewById(R.id.AgeTextView)

        nameTextView.setText(activeUser.name)
        stateTextView.setText(activeUser.state)
        ageTextView.setText(activeUser.age.toString())
    }

    fun back(view: View) {
        finish()
    }

    fun save(view: View) {
        val newName = nameTextView.text.toString()
        val newState = stateTextView.text.toString()
        val newAge = Transform.parseIntOrDefault(ageTextView.text.toString(), activeUser.age)

        // Определяем сигнал по английским словам
        val newSignal = when {
            newState.contains("online", ignoreCase = true) -> 1   // зелёный
            newState.contains("away", ignoreCase = true) ||
                    newState.contains("departed", ignoreCase = true) -> 2 // красный
            else -> 0                                             // серый (offline и всё остальное)
        }

        // 1. Обновляем локальный объект
        activeUser.name = newName
        activeUser.state = newState
        activeUser.age = newAge
        activeUser.stateSignal = newSignal

        // 2. Сохраняем в Firebase
        val db = FirebaseDatabase.getInstance()
        val profileRef = db.getReference("UsersProfileInfo").child(activeUser.profileId)

        profileRef.child("name").setValue(newName)
        profileRef.child("state").setValue(newState)
        profileRef.child("age").setValue(newAge)
        profileRef.child("stateSignal").setValue(newSignal)
            .addOnSuccessListener {
                android.util.Log.e("FIREBASE", "Данные сохранены в Firebase")
                finish()
            }
            .addOnFailureListener { e ->
                android.util.Log.e("FIREBASE", "Ошибка сохранения: ${e.message}")
                finish()
            }
    }
}