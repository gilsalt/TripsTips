package com.tripstips.app.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.tripstips.app.R
import com.tripstips.app.databinding.FragmentSignInBinding
import com.tripstips.app.viewmodel.AuthViewModel

class SignInFragment : Fragment() {

    private lateinit var binding:FragmentSignInBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignInBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signUpTv.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
        }

        authViewModel.currentUserStatus.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                binding.signInBtn.isEnabled = true
                binding.progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }

        authViewModel.currentUser.observe(requireActivity()) { user ->
            user?.let {
             findNavController().navigate(R.id.action_signInFragment_to_exploreFragment)
            }
        }

        binding.signInBtn.setOnClickListener {
            val email = binding.email.text.toString().trim()
            val password = binding.password.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                binding.signInBtn.isEnabled = false
                binding.progressBar.visibility = View.VISIBLE
                authViewModel.signIn(email, password)
            } else {
                binding.signInBtn.isEnabled = true
                binding.progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

}