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
import com.example.flowerly.dao.AppDatabase
import com.example.flowerly.databinding.FragmentSignupBinding
import com.example.flowerly.viewmodel.AuthViewModel
import com.example.flowerly.viewmodel.AuthViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView

class SignupFragment : Fragment() {

    private val viewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(AppDatabase.getDatabase(requireContext()).userDao())
    }

    private lateinit var binding: FragmentSignupBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSignupBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomNavigation = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.visibility = View.GONE

        viewModel.validationMessage.observe(viewLifecycleOwner) { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }

        viewModel.authResult.observe(viewLifecycleOwner) { success ->
            if (success) {
                bottomNavigation.visibility = View.VISIBLE
                bottomNavigation.selectedItemId = R.id.nav_home

                findNavController().navigate(R.id.mainFragment)
            } else {
                Toast.makeText(context, "Signup failed", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnSignup.setOnClickListener {
            viewModel.validateAndSignup()
        }
    }
}
