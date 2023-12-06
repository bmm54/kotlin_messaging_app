package com.example.messaging_app.ui.home.chat

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.messaging_app.R
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.type.DateTime
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item

class ChatActivity : AppCompatActivity() {
    companion object{
        val TAG="ChatLog"
    }
    val adapter = GroupAdapter<GroupieViewHolder>()

    private lateinit var user: Bundle
    private lateinit var chatroomid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)


        user = intent.extras!!.getBundle("user")?:intent.extras!!.getBundle("user_lm")!!


        setupData()
        val sendButton = findViewById<Button>(R.id.btn_send)
        sendButton.setOnClickListener {
            sendMessage()
        }

        val backButton = findViewById<Button>(R.id.chat_log_back)

        backButton.setOnClickListener {
            finish()
        }
    }
    private fun sendMessage(){

        //latest messsages intent or new message intent
//        val user = intent.extras!!.getBundle("user")
//        val toUid = user!!.getString("uid")

        val username = user.getString("name")
        val toUid = user.getString("uid")
        val image_url = user.getString("image_url")


        val editText = findViewById<EditText>(R.id.et_message)
        val text = editText.text.toString()
        val currentUid = FirebaseAuth.getInstance().currentUser?.uid
        val db = FirebaseFirestore.getInstance()
        val chatRoomId = if (currentUid!! < toUid.toString()) "$currentUid-$toUid" else "$toUid-$currentUid"
        val chatRoomRef=db.collection("messages")
            .document(chatRoomId)

        Log.d(TAG, "Chat room reference: $chatRoomRef")
        // send message to the specified chat room with some feilds in chatroom document along with msglist collection
        chatRoomRef
            .collection("msgList")
            .add(
                hashMapOf(
                    "message" to text,
                    "senderId" to currentUid,
                    "time" to Timestamp.now()
                )
            )
        chatRoomRef.set(
            hashMapOf(
                "chatRoomId" to chatRoomId,
                "senderUid" to currentUid,
                "receiverUid" to toUid,
                "message"  to text,
                "time" to Timestamp.now(),
                "senderName" to username,
                "receiverName" to FirebaseAuth.getInstance().currentUser?.displayName,
                "senderImageUrl" to image_url,
                "receiverImageUrl" to FirebaseAuth.getInstance().currentUser?.photoUrl.toString(),
            )
        )
        editText.text.clear()
        val recyclerView = findViewById<RecyclerView>(R.id.rv_chat_log)
        recyclerView.scrollToPosition(adapter.itemCount - 1)
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun setupData(){
        //sort messages by time

        val username = user.getString("name")
        val email = user.getString("email")
        val toUid = user.getString("uid")
        val image_url = user.getString("image_url")
        val currentUid = FirebaseAuth.getInstance().currentUser?.uid

        val chatLogUserName=findViewById<TextView>(R.id.chat_log_username)
        val chatLogImage = findViewById<ImageView>(R.id.chat_user_image)

        chatLogUserName.text=username
        Glide.with(chatLogImage.context).load(image_url).into(chatLogImage)

        // Initialize Firestore
        val db = FirebaseFirestore.getInstance()
        val chatRoomId = if (currentUid!! < toUid.toString()) "$currentUid-$toUid" else "$toUid-$currentUid"
        val chatRoomRef=db.collection("messages")
            .document(chatRoomId)
        Log.d(TAG, "Chat room reference: $chatRoomRef")
        // Retrieve messages from the specified chat room
        chatRoomRef
            .collection("msgList")
            .addSnapshotListener { querySnapshot, _ ->
                // Clear the adapter
                val messages = mutableListOf<ChatItem>()
                adapter.clear()
                querySnapshot?.documents?.forEach { document ->
                    // Create a ChatItem for each message and add it to the adapter
                    val chatItem = ChatItem(
                        document.getString("message") ?: "",
                        document.getString("senderId") ?: "",
                        document.getTimestamp("time") ?: Timestamp.now()
                    )
                    messages.add(chatItem)
                }
                messages.sortBy { it.time }
                adapter.update(messages)
                // Notify the adapter that the data set has changed
                adapter.notifyDataSetChanged()
            }
        val recyclerView = findViewById<RecyclerView>(R.id.rv_chat_log)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }
}

class ChatItem(val text: String,val uid:String,val time :Timestamp) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val userUid= FirebaseAuth.getInstance().currentUser?.uid
        if (uid==userUid){
            viewHolder.itemView.findViewById<TextView>(R.id.right_chat_text).text = text
            //change visibility of left chat text
            viewHolder.itemView.findViewById<TextView>(R.id.left_chat_text).visibility=TextView.INVISIBLE
        }
        else{
            viewHolder.itemView.findViewById<TextView>(R.id.left_chat_text).text = text
            //change visibility of right chat text
            viewHolder.itemView.findViewById<TextView>(R.id.right_chat_text).visibility=TextView.INVISIBLE
        }
    }

    override fun getLayout(): Int {
        return R.layout.message_item
    }
}