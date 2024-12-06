package com.cs407.forgeyourdiet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.cs407.forgeyourdiet.data.AuthRepository
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var signUpButton: Button
    private lateinit var authRepository: AuthRepository
    private lateinit var userViewModel: UserViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.login_fragment, container, false)

        requireContext().deleteDatabase("forge_your_diet_database")
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)

        usernameEditText = view.findViewById(R.id.userNameEditText)
        passwordEditText = view.findViewById(R.id.passwordEditText)
        loginButton = view.findViewById(R.id.loginButton)
        signUpButton = view.findViewById(R.id.signUpButton)

        authRepository = AuthRepository(requireContext())

        // Handle login button click
        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "Please enter both username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val loginSuccessful = authRepository.loginUser(username, password)
                if (loginSuccessful) {
                    userViewModel.setUser(UserState(username))
                    navigateToHomepage()
                } else {
                    Toast.makeText(context, "Invalid username or password", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Handle sign-up button click
        signUpButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "Please enter both username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val userExists = authRepository.loginUser(username, password)
                if (userExists) {
                    Toast.makeText(context, "User already exists. Please log in.", Toast.LENGTH_SHORT).show()
                } else {
                    val registrationSuccessful = authRepository.registerUser(username, password)
                    if (registrationSuccessful) {
                        Toast.makeText(context, "User created successfully. Logging in...", Toast.LENGTH_SHORT).show()
                        userViewModel.setUser(UserState(username))
                        navigateToHomepage()
                    } else {
                        Toast.makeText(context, "Failed to create user. Please try again.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        return view
    }

    private fun navigateToHomepage() {
        findNavController().navigate(R.id.action_loginFragment_to_homepageFragment)
    }
}
