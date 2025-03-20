package com.example.flowerly.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.flowerly.R
import com.example.flowerly.dao.AppDatabase
import com.example.flowerly.databinding.FragmentLoginBinding
import com.example.flowerly.viewmodel.AuthViewModel
import com.example.flowerly.viewmodel.AuthViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView

class LoginFragment : Fragment() {

    private val viewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(AppDatabase.getDatabase(requireContext()).userDao())
    }

    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomNavigation = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.visibility = View.GONE

        viewModel.authResult.observe(viewLifecycleOwner) { success ->
            if (!success) {
                Toast.makeText(context, "Incorrect email or password", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.user.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                bottomNavigation.visibility = View.VISIBLE
                bottomNavigation.selectedItemId = R.id.nav_home

                findNavController().navigate(R.id.mainFragment)
            }
        }

        binding.btnLogin.setOnClickListener {
            viewModel.login()
        }

        binding.btnNavigateToSignup.setOnClickListener {
            findNavController().navigate(R.id.signupFragment)
        }
    }
}
