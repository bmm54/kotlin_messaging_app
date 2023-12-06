package com.example.messaging_app

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.messaging_app.ui.home.chat.ChatActivity
import com.example.messaging_app.ui.home.new_messages.NewMessageActivity
import com.example.messaging_app.ui.login.LoginActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LatestMessages : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    val adapter = GroupAdapter<GroupieViewHolder>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        // Check if the user is already logged in
        if (auth.currentUser != null) {
            // User is already logged in, load the latest messages
            setupLatestMessages()
        } else {
            // User is not logged in, navigate to LoginActivity
            val loginIntent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
            startActivity(loginIntent)
            finish() // Close MainActivity to prevent user from coming back with back button
        }

    }

    fun setupLatestMessages() {
        setContentView(R.layout.activity_latest_messages)

        val newMsgBtn = findViewById<Button>(R.id.new_msg_btn)
        findViewById<TextView>(R.id.lt_username).text =
            FirebaseAuth.getInstance().currentUser?.email
        newMsgBtn.setOnClickListener {
            val intent = Intent(this, NewMessageActivity::class.java)
            startActivity(intent)
        }
        val auth = FirebaseAuth.getInstance()
        val logoutBtn: Button = findViewById(R.id.logOut)
        logoutBtn.setOnClickListener {
            try {
                auth.signOut()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "Error logging out", Toast.LENGTH_LONG).show()
            }
        }
        val rv_latest_messages: RecyclerView =
            findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rv_latest_messages)
        rv_latest_messages.adapter = adapter
        rv_latest_messages.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        adapter.setOnItemClickListener { item, view ->
            if (item is MessageItem) {
                val user = Bundle()
                //user.putString("uid", item.userUid)// the problem is here its giving the same uid for both sender and receiver
                user.putString("uid", item.otherUid)
                user.putString("name", item.userName)
                user.putString("image_url", item.userImageUrl)
                val intent = Intent(view.context, ChatActivity::class.java)
                intent.putExtra("user_lm", user)
                startActivity(intent)
            }
        }
        loadLatestMessages()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadLatestMessages() {
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
        val db = FirebaseFirestore.getInstance()
        val messagesCollection = db.collection("messages")
        messagesCollection
            .addSnapshotListener { querySnapshot, _ ->
                val chats =
                    mutableListOf<MessageItem>()  // Move the initialization outside the loop
                querySnapshot?.documents?.forEach { document ->
                    if (document.data!!["senderUid"] == currentUserUid || document.data!!["receiverUid"] == currentUserUid) {
                        val chatroomId = document.id
                        val message = document.getString("message")!!
                        val senderUid = document.getString("senderUid")!!
                        val receiverUid = document.getString("receiverUid")!!
                        val time = document.getTimestamp("time")!!
                        if (senderUid == currentUserUid) {
                            chats.add(
                                MessageItem(
                                    chatroomId,
                                    "You: " + message,
                                    senderUid,
                                    receiverUid,
                                    time,
                                    document.getString("senderName")!!,
                                    document.getString("senderImageUrl") ?: ""
                                )
                            )
                        } else {
                            chats.add(
                                MessageItem(
                                    chatroomId,
                                    message,
                                    receiverUid,
                                    senderUid,
                                    time,
                                    document.getString("receiverName")!!,
                                    document.getString("receiverImageUrl") ?: ""
                                )
                            )
                        }
                    } else {
                        // Do nothing
                    }
                }
                // Sort the chats by time acecnding
                chats.sortBy { it.time }
                chats.reverse()
                adapter.update(chats)
                adapter.notifyDataSetChanged()
            }
    }
}


class ChatRoom(var senderUid:String,var receiverUid:String,var message:String,var timestamp:Long)

class MessageItem(var chatroomId:String,var message:String, var userUid:String,var otherUid:String, var time:Timestamp, var userName:String, var userImageUrl:String): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val latest_message = viewHolder.itemView.findViewById<TextView>(R.id.lt_user_msg_text)
        val latest_message_name= viewHolder.itemView.findViewById<TextView>(R.id.lt_usr_name_text)
        val latest_message_time = viewHolder.itemView.findViewById<TextView>(R.id.lt_user_time_text)
        val imageView=viewHolder.itemView.findViewById<ImageView>(R.id.lt_user_image)
        Glide.with(viewHolder.itemView.context).load(userImageUrl).into(imageView)
        latest_message.text = message
        latest_message_name.text = userName
        latest_message_time.text = formatTime(time)
    }

    private fun formatTime(timestamp: Timestamp): String {
        val currentTime = Timestamp.now().toDate()
        val messageTime = timestamp.toDate()

        val diff = currentTime.time - messageTime.time
        val hours = diff / (60 * 60 * 1000)
        val minutes = (diff / (60 * 1000) % 60).toInt()

        return if (hours >= 24) {
            // More than 24 hours ago, display the day
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            dateFormat.format(messageTime)
        } else {
            // Less than 24 hours ago, display the time
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            timeFormat.format(messageTime)
        }
    }
    override fun getLayout(): Int {
        return R.layout.list_tile_message
    }
}