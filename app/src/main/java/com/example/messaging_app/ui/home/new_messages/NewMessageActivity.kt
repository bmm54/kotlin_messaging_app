package com.example.messaging_app.ui.home.new_messages

import UsersAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.messaging_app.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
class NewMessageActivity : AppCompatActivity() {
    private lateinit var users: List<UsersData>
    private lateinit var adapter: UsersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        val rvUsers = findViewById<RecyclerView>(R.id.users_list_view)
        adapter = UsersAdapter(emptyList())
        rvUsers.adapter = adapter
        rvUsers.layoutManager = LinearLayoutManager(this)

        // Start listening for user updates
        UsersData.startListeningForUsers { updatedUsers ->
            users = updatedUsers
            adapter.updateUsersList(users)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Stop listening for users when the activity is destroyed
        UsersData.stopListeningForUsers()
    }
}
