package com.example.messaging_app.ui.home.chat

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.messaging_app.R
import com.google.firebase.Timestamp

class ChatRecyclerAdapter : RecyclerView.Adapter<ChatRecyclerAdapter.ChatViewHolder>() {
    private var chatList: List<MessageModel> = ArrayList(listOf((MessageModel(
        "Hello, Right!",
        "1",
        Timestamp.now()
    )),
        MessageModel(
            "Hello, Left!",
            "2",
            Timestamp.now() )))

    private var currentUserId: String? = "1"


    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageRight: TextView = itemView.findViewById(R.id.right_chat_text)
        private val messageLeft: TextView = itemView.findViewById(R.id.left_chat_text)
        // private val timestamp: TextView = itemView.findViewById(R.id.timestamp)

        fun bind(chatMessageModel: MessageModel) {
            if (chatMessageModel.senderId == currentUserId) {
                messageRight.text = chatMessageModel.message
                messageRight.visibility = View.VISIBLE
                messageLeft.visibility = View.GONE
                // timestamp.gravity = Gravity.END
            } else {
                messageLeft.text = chatMessageModel.message
                messageLeft.visibility = View.VISIBLE
                messageRight.visibility = View.GONE
                // timestamp.gravity = Gravity.START
            }
            // timestamp.text = chatMessageModel.timestamp.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.message_item, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(chatList[position])
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    fun setChatList(chatList: List<MessageModel>) {
        this.chatList = chatList
        notifyDataSetChanged()
    }

    fun setCurrentUserId(currentUserId: String?) {
        this.currentUserId = currentUserId
    }

    fun insertMessage(message: MessageModel) {
        chatList = chatList.plus(message)
        notifyDataSetChanged()
    }


}

class MessageModel {
    var message: String? = null
    var senderId: String? = null
    var timestamp: Timestamp? = null

    constructor() {}

    constructor(message: String?, senderId: String?, timestamp: Timestamp?) {
        this.message = message
        this.senderId = senderId
        this.timestamp = timestamp
    }
}
