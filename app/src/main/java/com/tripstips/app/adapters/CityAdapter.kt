package com.tripstips.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tripstips.app.model.City
import com.tripstips.app.databinding.ItemCityBinding

class CityAdapter(private val cities: List<City>,
                  private val onCityClick: (City) -> Unit
) :
    RecyclerView.Adapter<CityAdapter.CityViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val binding = ItemCityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CityViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        val city = cities[position]
        holder.bind(city, onCityClick)
    }

    override fun getItemCount() = cities.size

    class CityViewHolder(private val binding: ItemCityBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(city: City, onCityClick: (City) -> Unit) {
            binding.cityName.text = city.name
            binding.cityImage.setImageResource(city.imageRes)

            binding.root.setOnClickListener {
                onCityClick(city) // Invoke the lambda function when clicked
            }
        }
    }
}
