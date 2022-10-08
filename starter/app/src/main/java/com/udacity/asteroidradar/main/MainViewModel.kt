package com.udacity.asteroidradar.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.PictureOfDayApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import kotlinx.coroutines.launch
import org.json.JSONObject

class MainViewModel : ViewModel() {

    private val _asteroids = MutableLiveData<List<Asteroid>>()
    val asteroids: LiveData<List<Asteroid>>
        get() = _asteroids

    private val _pictureOfDay = MutableLiveData<PictureOfDay>()
    val pictureOfDay: LiveData<PictureOfDay>
        get() = _pictureOfDay

    init {
        getAsteroids("2022-10-9", "2022-10-10")
        getPictureOfDay()
    }

    private fun getAsteroids(startDate: String, endDate: String) {
        viewModelScope.launch {
            try {
                val jsonResult: String = AsteroidApi.retrofitService.getAsteroids(
                    startDate,
                    endDate,
                    "p6UXJrgVhx4QRP75Qh09mCpJASs0AS13vtQ9274t"
                )
                _asteroids.value = parseAsteroidsJsonResult(JSONObject(jsonResult))
//                var size = asteroids.value?.size
            } catch (e: Exception) {
                _asteroids.value = ArrayList()
            }
        }
    }

    private fun getPictureOfDay() {
        viewModelScope.launch {
            try {
                _pictureOfDay.value = PictureOfDayApi.retrofitService.getPictureOfDay("p6UXJrgVhx4QRP75Qh09mCpJASs0AS13vtQ9274t")
                var url = pictureOfDay.value?.url
            } catch (e: Exception) {
                _pictureOfDay.value = null
            }
        }
    }

    fun updateDates(startDate: String, endDate: String) {

    }
}