package com.tripstips.app.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.tripstips.app.R
import com.tripstips.app.adapters.CityAdapter
import com.tripstips.app.databinding.FragmentExploreBinding
import com.tripstips.app.model.City
import com.tripstips.app.model.cities

class ExploreFragment : Fragment() {

    private lateinit var binding:FragmentExploreBinding
    private lateinit var adapter:CityAdapter
    private var citiesList = mutableListOf<City>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        citiesList.addAll(cities.subList(1, cities.size))
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