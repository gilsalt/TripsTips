package com.tripstips.app.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.tripstips.app.R
import com.tripstips.app.adapters.CityAdapter
import com.tripstips.app.databinding.FragmentExploreBinding
import com.tripstips.app.model.City
import com.tripstips.app.model.cities
import com.tripstips.app.repos.PostRepository
import com.tripstips.app.room.PostDatabase
import com.tripstips.app.viewmodel.PostViewModel
import com.tripstips.app.viewmodelfactory.PostViewModelFactory

class ExploreFragment : Fragment() {

    private lateinit var binding:FragmentExploreBinding
    private lateinit var adapter:CityAdapter
    private var citiesList = mutableListOf<City>()
    private val postViewModel: PostViewModel by viewModels {
        PostViewModelFactory(PostRepository(PostDatabase.getDatabase(requireContext()).postDao()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        citiesList.addAll(cities.subList(1, cities.size))
        postViewModel.syncPosts()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentExploreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = CityAdapter(citiesList) { city ->
            val action = ExploreFragmentDirections.actionExploreFragmentToCityPostFragment(city)
            findNavController().navigate(action)
        }
        binding.citiesRecyclerview.layoutManager = GridLayoutManager(requireActivity(), 3)
        binding.citiesRecyclerview.hasFixedSize()
        binding.citiesRecyclerview.adapter = adapter

        binding.berlinCardView.setOnClickListener {
            val action = ExploreFragmentDirections.actionExploreFragmentToCityPostFragment(cities[0])
            findNavController().navigate(action)
        }
    }

}