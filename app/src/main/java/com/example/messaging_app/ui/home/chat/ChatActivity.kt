package com.example.messaging_app.ui.home.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.messaging_app.R

class ChatActivity : AppCompatActivity() {
    // Real-time chat app activity
    private lateinit var messageAdapter: ChatRecyclerAdapter
    private lateinit var messageList: RecyclerView
    private lateinit var messageInput: EditText
    private lateinit var sendButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        messageList = findViewById(R.id.rv_messages)
        messageInput = findViewById(R.id.et_message)
        sendButton = findViewById(R.id.btn_send)

        // Initialize the RecyclerView adapter and set the layout manager
        messageAdapter = ChatRecyclerAdapter()
        //messageAdapter.setCurrentUserId("1")

        messageList.adapter = messageAdapter
        messageList.layoutManager = LinearLayoutManager(this) // Add this line

        sendButton.setOnClickListener {
            val message = messageInput.text.toString()
            if (message.isNotEmpty()) {
                val newMessage = MessageModel(message, "1", null)
                messageAdapter.insertMessage(newMessage)
                messageInput.text.clear()
            }
        }
    }
}
