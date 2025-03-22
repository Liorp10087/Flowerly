package com.example.flowerly.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.flowerly.R
import com.example.flowerly.databinding.FragmentSignupBinding
import com.example.flowerly.viewmodel.UserViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class SignupFragment : Fragment() {

    private lateinit var binding: FragmentSignupBinding
    private lateinit var bottomNavigation: BottomNavigationView
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignupBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomNavigation = requireActivity().findViewById(R.id.bottom_navigation)
        bottomNavigation.visibility = View.GONE

        binding.btnSignup.setOnClickListener {
            handleSignup()
        }

        userViewModel.signupStatus.observe(viewLifecycleOwner) { success ->
            if (success) {
                showBottomNav()
                navigateToMain()
            } else {
                showToast("Signup failed")
            }
        }
    }

    private fun handleSignup() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()

        if (!isValidEmail(email)) {
            showToast("Invalid email format")
            return
        }

        if (password.length < 6) {
            showToast("Password must be at least 6 characters")
            return
        }

        userViewModel.signup(email, password, requireContext())
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun showBottomNav() {
        bottomNavigation.visibility = View.VISIBLE
        bottomNavigation.selectedItemId = R.id.nav_home
    }

    private fun navigateToMain() {
        findNavController().navigate(R.id.mainFragment)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
