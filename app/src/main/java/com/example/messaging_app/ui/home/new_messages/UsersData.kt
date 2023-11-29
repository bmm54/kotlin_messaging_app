package com.example.messaging_app.ui.home.new_messages

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UsersData(
    val uid: String,
    val name: String,
    val email: String,
    val imageUrl: String?
) {
    companion object {
        private var listenerRegistration: ListenerRegistration? = null

        fun startListeningForUsers(callback: (List<UsersData>) -> Unit) {
            // Stop previous listener if exists
            stopListeningForUsers()

            // Start a new listener
            listenerRegistration = FirebaseFirestore.getInstance().collection("users")
                .addSnapshotListener { querySnapshot, _ ->
                    val users = ArrayList<UsersData>()
                    querySnapshot?.documents?.forEach { document ->
                        val user = UsersData(
                            document.getString("uid") ?: "",
                            document.getString("name") ?: "",
                            document.getString("email") ?: "",
                            document.getString("image_url")
                        )
                        users.add(user)
                    }
                    callback(users)
                }
        }

        fun stopListeningForUsers() {
            listenerRegistration?.remove()
            listenerRegistration = null
        }
    }
}
