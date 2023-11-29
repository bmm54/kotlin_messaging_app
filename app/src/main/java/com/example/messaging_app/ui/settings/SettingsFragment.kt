package com.example.messaging_app.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.messaging_app.databinding.FragmentSettingsBinding
import com.example.messaging_app.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val settingsViewModel =
            ViewModelProvider(this).get(SettingsViewModel::class.java)

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textSettings
        settingsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        var auth= FirebaseAuth.getInstance()


        //logoutOut button

        val text_name:TextView=binding.textName
        val text_email:TextView=binding.textEmail

        var user=auth.currentUser
        if (user!=null){
            var name:String= user.displayName!!
            var email:String= user.email!!

            text_name.text = name
            text_email.text=email
        }
        binding.logoutBtn.setOnClickListener {
            try
            {
                auth.signOut()
                val intent = Intent(activity, LoginActivity::class.java)
                startActivity(intent)
            }
            catch (e:Exception){
                Toast.makeText(this.context,"Error logging out",Toast.LENGTH_LONG).show()
            }
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}