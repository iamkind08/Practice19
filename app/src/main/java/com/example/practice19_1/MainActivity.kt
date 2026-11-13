package com.example.practice19_1

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.FrameLayout
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var userPanel: FrameLayout
    private lateinit var nameTextView: TextView
    private lateinit var stateTextView: TextView
    private lateinit var ageTextView: TextView
    private lateinit var lastMessageTextView: TextView // для панели

    private lateinit var userListAdapter: UserListAdapter
    private var positionActiveUser = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
        loadUsersFromFirebase()
    }

    override fun onResume() {
        super.onResume()
        if (::userListAdapter.isInitialized) userListAdapter.notifyDataSetChanged()
        if (userPanel.visibility == View.VISIBLE && positionActiveUser < UserStaticInfo.users.size) {
            initPanel(UserStaticInfo.users[positionActiveUser])
        }
    }

    private fun init() {
        userPanel = findViewById(R.id.userPanel)
        nameTextView = findViewById(R.id.NameTextView)
        stateTextView = findViewById(R.id.StateTextView)
        ageTextView = findViewById(R.id.AgeTextView)
        lastMessageTextView = findViewById(R.id.LastMessageTextView)
        listView = findViewById(R.id.listView)

        userListAdapter = UserListAdapter()
        listView.adapter = userListAdapter
    }

    private fun loadUsersFromFirebase() {
        val db = FirebaseDatabase.getInstance()
        db.getReference("UsersProfileInfo")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    UserStaticInfo.users.clear()
                    for (child in snapshot.children) {
                        val profileId = child.key ?: continue
                        val name = child.child("name").getValue(String::class.java) ?: ""
                        val state = child.child("state").getValue(String::class.java) ?: ""
                        val age = child.child("age").getValue(Int::class.java) ?: 0
                        val savedSignal = child.child("stateSignal").getValue(Int::class.java)
                        val signal = savedSignal ?: when {
                            state.contains("online", true) -> 1
                            state.contains("away", true) || state.contains("departed", true) -> 2
                            else -> 0
                        }
                        val lastMessage = child.child("lastMessage").getValue(String::class.java) ?: ""
                        val lastMessageType = child.child("lastMessageType").getValue(String::class.java) ?: ""

                        UserStaticInfo.users.add(
                            User(profileId, name, state, age, signal, lastMessage, lastMessageType)
                        )
                    }
                    userListAdapter.notifyDataSetChanged()
                    if (userPanel.visibility == View.VISIBLE && positionActiveUser < UserStaticInfo.users.size) {
                        initPanel(UserStaticInfo.users[positionActiveUser])
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    android.util.Log.e("FIREBASE", "Ошибка загрузки: ${error.message}")
                }
            })
    }

    fun backToList(view: View) {
        userPanel.visibility = View.GONE
    }

    fun editUser(view: View) {
        val intent = Intent(this, UserActivity::class.java)
        intent.putExtra(UserStaticInfo.POSITION, positionActiveUser)
        startActivity(intent)
    }

    fun logout(view: View) {
        val sp = getSharedPreferences(Transform.APP_PREFERENCES, MODE_PRIVATE)
        sp.edit().clear().apply()
        val intent = Intent(this, SignActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun initPanel(item: User) {
        nameTextView.text = item.name
        stateTextView.text = item.state
        ageTextView.text = item.age.toString()
        // Превью последнего сообщения на панели
        lastMessageTextView.text = item.lastMessage
        userPanel.visibility = View.VISIBLE
    }

    private inner class UserListAdapter : BaseAdapter() {
        private val inflater = LayoutInflater.from(this@MainActivity)

        override fun getCount(): Int = UserStaticInfo.users.size
        override fun getItem(position: Int): User = UserStaticInfo.users[position]
        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = convertView ?: inflater.inflate(R.layout.item_user, parent, false)
            val currentUser = getItem(position)

            val nameView = view.findViewById<TextView>(R.id.NameTextView)
            val stateView = view.findViewById<TextView>(R.id.StateTextView)
            val lastMessageView = view.findViewById<TextView>(R.id.LastMessageTextView)
            val stateRound = view.findViewById<FrameLayout>(R.id.StateRound)

            nameView.text = currentUser.name
            stateView.text = currentUser.state
            lastMessageView.text = currentUser.lastMessage

            when (currentUser.stateSignal) {
                0 -> stateRound.setBackgroundColor(0xFFCFCFCF.toInt())
                1 -> stateRound.setBackgroundColor(0xFF00FF00.toInt())
                2 -> stateRound.setBackgroundColor(0xFFFF0000.toInt())
            }

            view.setOnClickListener {
                positionActiveUser = position
                initPanel(currentUser)
            }

            return view
        }
    }
}