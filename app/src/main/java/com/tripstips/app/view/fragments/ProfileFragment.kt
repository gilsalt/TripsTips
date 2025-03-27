package com.tripstips.app.view.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.squareup.picasso.Picasso
import com.tripstips.app.R
import com.tripstips.app.databinding.FragmentProfileBinding
import com.tripstips.app.view.activities.BaseActivity
import com.tripstips.app.view.activities.MainActivity
import com.tripstips.app.viewmodel.AuthViewModel


class ProfileFragment : Fragment() {

    private lateinit var binding:FragmentProfileBinding
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
        binding =  FragmentProfileBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        val user = BaseActivity.loggedUser
        if (user != null){
            if (user.image.isNotEmpty()){
                Picasso.get().load(user.image)
                    .placeholder(R.drawable.loader)
                    .error(R.drawable.placeholder)
                    .resize(100,100)
                    .centerCrop()
                    .into(binding.profileImage)
            }
            binding.nameTv.text = user.name
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.editProfileTv.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }

        binding.signOutBtn.setOnClickListener {
            BaseActivity.loggedUser = null
            authViewModel.signOut()
            startActivity(Intent(requireActivity(),MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }).apply {
                requireActivity().finish()
            }
        }

    }
}