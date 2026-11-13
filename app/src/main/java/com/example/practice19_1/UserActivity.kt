package com.example.practice19_1

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

class UserActivity : AppCompatActivity() {

    private lateinit var nameTextView: EditText
    private lateinit var stateTextView: EditText
    private lateinit var ageTextView: EditText
    private lateinit var lastMessageEditText: EditText
    private lateinit var messageTypeSpinner: Spinner
    private lateinit var activeUser: User

    private val messageTypes = listOf("Текст", "Фото", "Аудиосообщение", "Файл")
    private val typeKeys = listOf("text", "image", "audio", "file")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        val position = intent.getIntExtra(UserStaticInfo.POSITION, 0)
        activeUser = UserStaticInfo.users[position]

        nameTextView = findViewById(R.id.NameTextView)
        stateTextView = findViewById(R.id.StateTextView)
        ageTextView = findViewById(R.id.AgeTextView)
        lastMessageEditText = findViewById(R.id.LastMessageEditText)
        messageTypeSpinner = findViewById(R.id.MessageTypeSpinner)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, messageTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        messageTypeSpinner.adapter = adapter

        nameTextView.setText(activeUser.name)
        stateTextView.setText(activeUser.state)
        ageTextView.setText(activeUser.age.toString())

        val idx = typeKeys.indexOf(activeUser.lastMessageType)
        if (idx >= 0) messageTypeSpinner.setSelection(idx)

        lastMessageEditText.setText(
            when (activeUser.lastMessageType) {
                "image" -> ""
                "audio" -> ""
                "file" -> activeUser.lastMessage.replace("Файл: ", "")
                else -> activeUser.lastMessage
            }
        )
    }

    fun back(view: View) {
        finish()
    }

    fun save(view: View) {
        val newName = nameTextView.text.toString()
        val newState = stateTextView.text.toString()
        val newAge = Transform.parseIntOrDefault(ageTextView.text.toString(), activeUser.age)

        val newSignal = when {
            newState.contains("online", true) -> 1
            newState.contains("away", true) || newState.contains("departed", true) -> 2
            else -> 0
        }

        val selectedTypeIndex = messageTypeSpinner.selectedItemPosition
        val selectedTypeKey = typeKeys[selectedTypeIndex]
        val userInput = lastMessageEditText.text.toString().trim()

        val lastMessage = when (selectedTypeKey) {
            "image" -> "Фото"
            "audio" -> "Аудиосообщение"
            "file" -> if (userInput.isNotEmpty()) "Файл: $userInput" else "Файл"
            else -> userInput.ifEmpty { "Нет сообщений" }
        }

        activeUser.name = newName
        activeUser.state = newState
        activeUser.age = newAge
        activeUser.stateSignal = newSignal
        activeUser.lastMessage = lastMessage
        activeUser.lastMessageType = selectedTypeKey

        val db = FirebaseDatabase.getInstance()
        val profileRef = db.getReference("UsersProfileInfo").child(activeUser.profileId)

        profileRef.child("name").setValue(newName)
        profileRef.child("state").setValue(newState)
        profileRef.child("age").setValue(newAge)
        profileRef.child("stateSignal").setValue(newSignal)
        profileRef.child("lastMessage").setValue(lastMessage)
        profileRef.child("lastMessageType").setValue(selectedTypeKey)
            .addOnSuccessListener {
                android.util.Log.e("FIREBASE", "Данные сохранены")
                finish()
            }
            .addOnFailureListener { e ->
                android.util.Log.e("FIREBASE", "Ошибка: ${e.message}")
                finish()
            }
    }
}