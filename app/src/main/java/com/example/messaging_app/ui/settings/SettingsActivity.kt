package com.example.messaging_app.ui.settings

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.messaging_app.R
import com.example.messaging_app.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        var auth= FirebaseAuth.getInstance()


        //logoutOut button

        val text_name: TextView =findViewById(R.id.text_name)
        val text_email: TextView =findViewById(R.id.text_email)

        var user=auth.currentUser
        if (user!=null){
            var name:String= user.displayName!!
            var email:String= user.email!!

            text_name.text = name
            text_email.text=email
        }
        val logoutBtn:Button=findViewById(R.id.logout_btn)
        logoutBtn.setOnClickListener {
            try
            {
                auth.signOut()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
            catch (e:Exception){
                Toast.makeText(this,"Error logging out", Toast.LENGTH_LONG).show()
            }
        }
    }
}