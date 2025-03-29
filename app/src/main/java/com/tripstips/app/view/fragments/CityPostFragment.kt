package com.tripstips.app.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navArgument
import androidx.recyclerview.widget.LinearLayoutManager
import com.tripstips.app.R
import com.tripstips.app.adapters.PostAdapter
import com.tripstips.app.databinding.FragmentCityPostBinding
import com.tripstips.app.model.City
import com.tripstips.app.model.Post
import com.tripstips.app.repos.PostRepository
import com.tripstips.app.room.PostDatabase
import com.tripstips.app.view.activities.MainActivity
import com.tripstips.app.viewmodel.PostViewModel
import com.tripstips.app.viewmodel.WeatherViewModel
import com.tripstips.app.viewmodelfactory.PostViewModelFactory


class CityPostFragment : Fragment() {

    private lateinit var binding:FragmentCityPostBinding
    private var city:City?=null
    val apiKey = "e572674eea9c73a4e16cf8e04e675e9a" // Replace with your API key
    private val weatherViewModel: WeatherViewModel by viewModels()
    private var postsList = mutableListOf<Post>()
    private lateinit var adapter: PostAdapter
    private val postViewModel: PostViewModel by viewModels {
        PostViewModelFactory(PostRepository(PostDatabase.getDatabase(requireContext()).postDao()))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCityPostBinding.inflate(inflater, container, false)

        val args : CityPostFragmentArgs by navArgs()
        city = args.city
        (activity as? MainActivity)?.updateToolbarTitle("${city?.name} Posts")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        displayWeatherDetails()
        binding.postsRecyclerview.layoutManager = LinearLayoutManager(requireActivity())
        binding.postsRecyclerview.hasFixedSize()
        adapter = PostAdapter(postsList)
        binding.postsRecyclerview.adapter = adapter
        adapter.setOnItemClickListener(object :PostAdapter.OnItemClickListener{
            override fun onItemClick(position: Int, post: Post) {
                val action = CityPostFragmentDirections.actionCityPostFragmentToDetailFragment(post)
                findNavController().navigate(action)

            }

            override fun onItemLikeClick(position: Int, post: Post) {
                postViewModel.updateLikeCount("${post.id}",true)
                adapter.notifyItemChanged(position)
            }
        })
        getPostsByCity("${city?.name}")
    }

    private fun getPostsByCity(city: String) {
        postViewModel.getPostsByCity(city).observe(viewLifecycleOwner, Observer { posts ->
            if (posts.isNotEmpty()){
                postsList.clear()
            }
            postsList.addAll(posts)
            adapter.notifyItemChanged(0,postsList.size)
        })
    }

    private fun displayWeatherDetails(){
        weatherViewModel.fetchWeather("${city?.name}", apiKey)
        weatherViewModel.weatherData.observe(requireActivity(), Observer { weather ->
            weather?.let {
                binding.weatherTextView.setTextColor(ContextCompat.getColor(requireContext(),R.color.green))
                binding.weatherTextView.text = "City: ${it.name} : Temp: ${it.main.temp}°C"
            } ?: run {
                binding.weatherTextView.setTextColor(ContextCompat.getColor(requireContext(),R.color.red))
                binding.weatherTextView.text = "No Weather data, Check API KEY"
            }
        })
    }

}