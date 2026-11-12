package com.example.practice19_1

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

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

        // Заполняем поля
        nameTextView.setText(activeUser.name)
        stateTextView.setText(activeUser.state)
        ageTextView.setText(activeUser.age.toString())
    }

    fun back(view: View) {
        finish()
    }

    fun save(view: View) {
        activeUser.name = nameTextView.text.toString()
        activeUser.state = stateTextView.text.toString()
        activeUser.age = Transform.parseIntOrDefault(ageTextView.text.toString(), activeUser.age)

        finish()
    }
}