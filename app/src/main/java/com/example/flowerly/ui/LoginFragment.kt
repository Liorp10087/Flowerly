package com.example.flowerly.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.flowerly.R
import com.example.flowerly.databinding.FragmentLoginBinding
import com.example.flowerly.viewmodel.AuthViewModel

class LoginFragment : Fragment() {
    private lateinit var viewModel: AuthViewModel
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.authResult.observe(viewLifecycleOwner) { success ->
            if (success) findNavController().navigate(R.id.mainFragment)
            else Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT).show()
        }

        binding.btnNavigateToSignup.setOnClickListener {
            findNavController().navigate(R.id.signupFragment)
        }

        return binding.root
    }
}