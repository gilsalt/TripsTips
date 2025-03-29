package com.tripstips.app.view.fragments

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.tripstips.app.R
import com.tripstips.app.databinding.FragmentNewPostBinding
import com.tripstips.app.interfaces.PostCallback
import com.tripstips.app.model.Post
import com.tripstips.app.model.cities
import com.tripstips.app.repos.PostRepository
import com.tripstips.app.room.PostDatabase
import com.tripstips.app.view.activities.BaseActivity
import com.tripstips.app.viewmodel.PostViewModel
import com.tripstips.app.viewmodelfactory.PostViewModelFactory


class NewPostFragment : Fragment() {
    private lateinit var binding:FragmentNewPostBinding
    private lateinit var pickImageLauncher: ActivityResultLauncher<String>
    private lateinit var locationPickerLauncher: ActivityResultLauncher<Intent>
    private var selectedImageUri: Uri? = null
    private var selectedLocation: String = ""
    private var selectedCity: String = ""
    private val postViewModel: PostViewModel by viewModels {
        PostViewModelFactory(PostRepository(PostDatabase.getDatabase(requireContext()).postDao()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                selectedImageUri = uri
                binding.postImageView.setImageURI(uri)
                binding.postImageView.visibility = View.VISIBLE
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
        binding = FragmentNewPostBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initPlaces()
        initCitySpinner()
        binding.uploadPhotoButton.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.savePostButton.setOnClickListener {
            val description = binding.postDescription.text.toString()
            if(validation(description,selectedLocation)){
                binding.progressBar.visibility = View.VISIBLE
                binding.savePostButton.isEnabled = false
                binding.postDescription.isEnabled = false
                binding.citySpinner.isEnabled = false
                binding.location.isEnabled = false
                binding.uploadPhotoButton.isEnabled = false
                val post = Post(city = selectedCity, description = description, address = selectedLocation, user = BaseActivity.loggedUser)
                postViewModel.insert(post, selectedImageUri, object : PostCallback {
                    override fun onSuccess(message: String) {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        findNavController().navigateUp()
                    }

                    override fun onFailure(error: String) {
                        binding.progressBar.visibility = View.GONE
                        binding.savePostButton.isEnabled = true
                        binding.postDescription.isEnabled = true
                        binding.citySpinner.isEnabled = true
                        binding.location.isEnabled = true
                        binding.uploadPhotoButton.isEnabled = true
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }

    }

    private fun validation(
        description: String,
        selectedLocation: String
    ): Boolean {
        if (description.isEmpty()){
            Toast.makeText(requireActivity(),"Description field is required",Toast.LENGTH_SHORT).show()
            return false
        }
        else if (selectedLocation.isEmpty()){
            Toast.makeText(requireActivity(),"Location field is required",Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun initPlaces() {
        if (!Places.isInitialized()) {
            Places.initialize(requireActivity().applicationContext, getString(R.string.google_map_api_key))
        }

        locationPickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                handleLocationPickerResult(result.resultCode, result.data)
            }

        binding.location.setOnClickListener {
            launchLocationPicker()

        }
    }

    private fun initCitySpinner(){
        selectedCity = cities[0].name
        val adapter = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_dropdown_item, cities)
        binding.citySpinner.adapter = adapter

        binding.citySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    selectedCity = cities[position].name
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    private fun launchLocationPicker() {
        val fields = listOf(Place.Field.ID, Place.Field.ADDRESS, Place.Field.LAT_LNG)
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
            .build(requireActivity())
        locationPickerLauncher.launch(intent)
    }

    private fun handleLocationPickerResult(resultCode: Int, data: Intent?) {
        when (resultCode) {
            RESULT_OK -> {
                val place = Autocomplete.getPlaceFromIntent(data!!)
                val locationName = place.address
                val locationLatLng = place.latLng
                if (locationName != null) {
                    selectedLocation = locationName
                }
                binding.location.setText(locationName)
            }

            AutocompleteActivity.RESULT_ERROR -> {
                val status: Status = Autocomplete.getStatusFromIntent(data!!)

            }

            RESULT_CANCELED -> {

            }
        }
    }

}