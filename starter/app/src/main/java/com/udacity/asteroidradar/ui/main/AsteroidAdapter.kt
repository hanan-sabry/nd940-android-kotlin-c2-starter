package com.udacity.asteroidradar.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.databinding.AsteroidViewItemBinding

class AsteroidAdapter(val onClickListener: OnClickListener) : ListAdapter<Asteroid, AsteroidAdapter.AsteroidViewHolder>(DiffCallback){

    class AsteroidViewHolder(private var binding:AsteroidViewItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
            fun bind(asteroid: Asteroid) {
                binding.astorid = asteroid
                binding.executePendingBindings()
            }
        }

    companion object DiffCallback : DiffUtil.ItemCallback<Asteroid>() {
        override fun areItemsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return oldItem === newItem;
        }

        override fun areContentsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsteroidViewHolder {
        return AsteroidViewHolder(AsteroidViewItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: AsteroidViewHolder, position: Int) {
        val asteroid = getItem(position)
        holder.itemView.setOnClickListener{
            onClickListener.onClick(asteroid)
        }
        holder.bind(asteroid)
    }

    class OnClickListener(val clickListener : (asteroid: Asteroid) -> Unit) {
        fun onClick(asteroid: Asteroid) = clickListener(asteroid)
    }

}