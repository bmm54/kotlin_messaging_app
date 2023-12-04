package com.example.messaging_app.ui.home.new_messages

import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.messaging_app.R
import com.example.messaging_app.ui.home.chat.ChatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.xwray.groupie.Item
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.parcelize.Parcelize

class NewMessageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)
        fetchUsers()
    }

//    companion object {
//        val USER_KEY = "USER_KEY"
//    }
    private fun fetchUsers() {
        val usersAdapter = GroupAdapter<GroupieViewHolder>()

        val recyclerView = findViewById<RecyclerView>(R.id.users_list_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = usersAdapter

        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val user = document.toObject(User::class.java)
                    usersAdapter.add(UserItem(user))
                }
            }
            .addOnFailureListener { exception ->
                Log.w("NewMessageActivity", "Error getting users", exception)
            }
        usersAdapter.setOnItemClickListener() { item, view ->
            val userItem = item as UserItem
            //Map user data to parcelable
            val userData = Bundle()
            userData.putString("uid", userItem.user.uid)
            userData.putString("name", userItem.user.name)
            userData.putString("image_url", userItem.user.image_url)
            userData.putString("email", userItem.user.email)
            val intent = Intent(view.context, ChatActivity::class.java)
            intent.putExtra("user", userData)
            setResult(RESULT_OK, intent)
            startActivity(intent)
            finish()
        }
    }
}

class UserItem(val user: User): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.user_name_text).text = user.name
        //set image using glide
        val imageView:ImageView = viewHolder.itemView.findViewById(R.id.user_image_view)
        Glide.with(imageView.context).load(user.image_url).into(imageView)
        //Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.imageview_new_message)
    }

    override fun getLayout(): Int {
        return R.layout.recycler_view_item
    }
}


@Parcelize
class User(val uid: String, val name: String, val image_url: String,val email:String): Parcelable {
    constructor() : this("", "", "", "")
}