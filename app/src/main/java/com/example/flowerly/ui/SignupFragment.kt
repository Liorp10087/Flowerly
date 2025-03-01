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
import com.example.flowerly.viewmodel.AuthViewModel

class SignupFragment : Fragment() {
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var binding: FragmentSignupBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSignupBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.validationMessage.observe(viewLifecycleOwner) { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }

        viewModel.authResult.observe(viewLifecycleOwner) { success ->
            if (success) findNavController().navigate(R.id.mainFragment)
            else Toast.makeText(context, "Signup failed", Toast.LENGTH_SHORT).show()
        }

        binding.btnSignup.setOnClickListener {
            viewModel.validateAndSignup()
        }

        return binding.root
    }
}