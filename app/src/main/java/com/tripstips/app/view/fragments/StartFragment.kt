package com.tripstips.app.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.tripstips.app.R
import com.tripstips.app.databinding.FragmentStartBinding
import com.tripstips.app.viewmodel.AuthViewModel


class StartFragment : Fragment() {

    private lateinit var binding:FragmentStartBinding
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
        binding = FragmentStartBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.progressBar.visibility = View.VISIBLE

        authViewModel.currentUser.observe(requireActivity()) { user ->
            user?.let {
                findNavController().navigate(R.id.action_startFragment_to_exploreFragment)
            } ?: run {
                binding.progressBar.visibility = View.GONE
                binding.letsGoBtn.visibility = View.VISIBLE
            }
        }

        binding.letsGoBtn.setOnClickListener {
           findNavController().navigate(R.id.action_startFragment_to_signInFragment)
        }
    }

}