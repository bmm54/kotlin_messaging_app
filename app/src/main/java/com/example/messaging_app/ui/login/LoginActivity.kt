package com.example.messaging_app.ui.login

import android.content.Intent
import android.content.pm.ActivityInfo.WindowLayout
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.messaging_app.MainActivity
import com.example.messaging_app.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth //Firebase Authentication
    private lateinit var googleSignInClient: GoogleSignInClient //Google Sign In Client
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Hide the status bar
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
        )
        setContentView(R.layout.activity_login)

        auth= FirebaseAuth.getInstance()
        var gso= GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) //default_web_client_id is the client id of the app
            .requestEmail()
            .build()

        googleSignInClient= GoogleSignIn.getClient(this,gso)

        findViewById<Button>(R.id.g_login_btn).setOnClickListener {
            signInWithGoogle()
        }
    }
    private fun signInWithGoogle(){
        val signInIntent= googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    //create a launcher for the sign in intent
    private val launcher= registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result-> if(result.resultCode== RESULT_OK) {
        val intent = result.data
        val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
        handleResult(task)
    }
    }

    //handle the result of the sign in intent
    private fun handleResult(task: Task<GoogleSignInAccount>){
        if (task.isSuccessful) {
            val account:GoogleSignInAccount? = task.result
            if (account != null) {
                updateUI(account)
            }
        }
        else{
            Toast.makeText(this,"Sign in with Google failed", Toast.LENGTH_LONG).show()
        }
    }

    //update the UI with the user's information
// ...

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener { signInTask ->
            if (signInTask.isSuccessful) {
                // Get user information from the GoogleSignInAccount
                val uid = auth.currentUser?.uid
                val name = account.displayName
                val email = account.email
                val imageUrl = account.photoUrl?.toString()

                // Create a user object
                val user = hashMapOf(
                    "uid" to uid,
                    "name" to name,
                    "email" to email,
                    "image_url" to imageUrl
                )

                // Store the user information in Firestore
                val fire_store= FirebaseFirestore.getInstance()
                fire_store.collection("users").document(uid ?: "")
                    .set(user)
                    .addOnSuccessListener {
                        // Successfully stored user information in Firestore
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT
                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener {
                        // Failed to store user information in Firestore
                        Toast.makeText(this, "Failed to store user information", Toast.LENGTH_LONG)
                            .show()
                    }
            } else {
                Toast.makeText(this, "Sign in with Google failed", Toast.LENGTH_LONG).show()
            }
        }
    }

// ...

}















