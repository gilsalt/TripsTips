package com.tripstips.app.view.fragments

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.squareup.picasso.Picasso
import com.tripstips.app.R
import com.tripstips.app.databinding.FragmentEditProfileBinding
import com.tripstips.app.view.activities.BaseActivity
import com.tripstips.app.viewmodel.AuthViewModel

class EditProfileFragment : Fragment() {
    private lateinit var binding:FragmentEditProfileBinding
    private var imageUri: Uri? = null
    private lateinit var pickImageLauncher: ActivityResultLauncher<String>
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                imageUri = uri
                binding.profileImageView.setImageURI(uri)
            } else {
                Toast.makeText(requireContext(), "No image selected", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)

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
                    .into(binding.profileImageView)
            }
            binding.name.setText(user.name)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageUploadBtn.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        authViewModel.profileUpdateStatus.observe(viewLifecycleOwner) { message ->
            message?.let {
                binding.updateBtn.isEnabled = true
                binding.progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        }

        binding.updateBtn.setOnClickListener {
            val name = binding.name.text.toString().trim()

            if (name.isNotEmpty()) {
                binding.updateBtn.isEnabled = false
                binding.progressBar.visibility = View.VISIBLE
                authViewModel.updateProfile(name, imageUri)
            } else {
                binding.updateBtn.isEnabled = true
                binding.progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Name fields are required", Toast.LENGTH_SHORT).show()
            }
        }

    }

}