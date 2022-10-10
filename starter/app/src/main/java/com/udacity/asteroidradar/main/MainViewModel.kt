package com.udacity.asteroidradar.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.BuildConfig
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.*
import kotlinx.coroutines.launch
import org.json.JSONObject

enum class PictureOfDayMediaType(val value: String) { IMAGE("image"), VIDEO("video") }

enum class AsteroidApiStatus { LOADING, ERROR, DONE }

class MainViewModel : ViewModel() {

    private val _status = MutableLiveData<AsteroidApiStatus>()
    val status: LiveData<AsteroidApiStatus>
        get() = _status

    private val _asteroids = MutableLiveData<List<Asteroid>>()
    val asteroids: LiveData<List<Asteroid>>
        get() = _asteroids

    private val _pictureOfDay = MutableLiveData<PictureOfDay>()
    val pictureOfDay: LiveData<PictureOfDay>
        get() = _pictureOfDay

    private val _navigateToSelectedAsteroid = MutableLiveData<Asteroid>()
    val navigateToSelectedAsteroid: LiveData<Asteroid>
        get() = _navigateToSelectedAsteroid

    init {
        getPictureOfDay()
        getAsteroids(AsteroidApiFilter.WEEK)
    }

    private fun getAsteroids(filter: AsteroidApiFilter) {
        viewModelScope.launch {
            _status.value = AsteroidApiStatus.LOADING
            try {
                val jsonResult: String =
                    when (filter) {
                        AsteroidApiFilter.WEEK ->
                            AsteroidApi.retrofitService.getAsteroids(
                                "", "",
                                BuildConfig.API_KEY
                            )
                        AsteroidApiFilter.TODAY ->
                            AsteroidApi.retrofitService.getAsteroids(
                                getTodayFormattedDate(), getTodayFormattedDate(),
                                BuildConfig.API_KEY
                            )
                        AsteroidApiFilter.SAVED ->
                            ""
                    }
                _asteroids.value = parseAsteroidsJsonResult(JSONObject(jsonResult))
                _status.value = AsteroidApiStatus.DONE
            } catch (e: Exception) {
                _status.value = AsteroidApiStatus.ERROR
                _asteroids.value = ArrayList()
            }
        }
    }

    private fun getPictureOfDay() {
        viewModelScope.launch {
            try {
                _pictureOfDay.value =
                    PictureOfDayApi.retrofitService.getPictureOfDay("nyuXOo8itTcFCmdTFCD5skTLdb5uWPV4cTbDj6sQ")
            } catch (e: Exception) {
                _pictureOfDay.value = null
            }
        }
    }

    fun displayAsteroidDetails(asteroid: Asteroid) {
        _navigateToSelectedAsteroid.value = asteroid
    }

    fun displayAsteroidDetailsComplete() {
        _navigateToSelectedAsteroid.value = null
    }

    fun updateFilter(filter: AsteroidApiFilter) {
        getAsteroids(filter)
    }

}