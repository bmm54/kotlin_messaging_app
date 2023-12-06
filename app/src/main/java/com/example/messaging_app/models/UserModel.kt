package com.example.messaging_app.models

//("uid", item.userUid)
//                user.putString("name", item.userName)
//                user.putString("image_url", item.userImageUrl)
//                user.putString("email", item.userName)
class UserModel(val uid: String, val name: String, val image_url: String) {
    constructor() : this("", "", "")
}