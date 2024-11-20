package com.cs407.forgeyourdiet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class LoginFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.login_fragment, container, false)

        val loginButton = view.findViewById<Button>(R.id.loginButton)
        loginButton.setOnClickListener {
            // Navigate to HomePageFragment
//            parentFragmentManager.beginTransaction()
//                .replace(R.id.loginFragment, HomePageFragment()) // Replace with your container ID
//                .addToBackStack(null)
//                .commit()
            // TODO: add signup/login logic

            // TODO: add database integration, consider adding cache
            findNavController().navigate(R.id.action_loginFragment_to_homepageFragment)
        }

        return view
    }
}
