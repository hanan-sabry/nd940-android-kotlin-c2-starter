package com.udacity.asteroidradar.ui.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.network.api.AsteroidApiFilter
import com.udacity.asteroidradar.repository.AsteroidsRepository
import kotlinx.coroutines.launch

enum class PictureOfDayMediaType(val value: String) { IMAGE("image"), VIDEO("video") }

enum class AsteroidApiStatus { LOADING, ERROR, DONE }

class MainViewModel(application: Application) : ViewModel() {


    private val database = getDatabase(application)
    private val asteroidsRepository = AsteroidsRepository(database)

    private val _pictureOfDay = MutableLiveData<PictureOfDay>()
    val pictureOfDay: LiveData<PictureOfDay>
        get() = _pictureOfDay

    private val _navigateToSelectedAsteroid = MutableLiveData<Asteroid>()
    val navigateToSelectedAsteroid: LiveData<Asteroid>
        get() = _navigateToSelectedAsteroid

    private val _databaseAsteroids = asteroidsRepository.weekAsteroids
    private val _asteroids: MutableLiveData<List<Asteroid>> = _databaseAsteroids as MutableLiveData<List<Asteroid>>
    val asteroids: LiveData<List<Asteroid>>
        get() = _asteroids

    init {
        viewModelScope.launch {
            try {
                _pictureOfDay.value = asteroidsRepository.getPictureOfDay()
                asteroidsRepository.refreshAsteroids()
            } catch (e: Exception) {
                println(e.message)
                _pictureOfDay.value = null
            }
        }
    }

    fun updateFilter(filter: AsteroidApiFilter) {
        _asteroids.value = when (filter) {
            AsteroidApiFilter.TODAY ->
                asteroidsRepository.todayAsteroids.value
            AsteroidApiFilter.WEEK ->
                asteroidsRepository.weekAsteroids.value
            AsteroidApiFilter.SAVED ->
                asteroidsRepository.allAsteroids.value
        }
    }

    fun displayAsteroidDetails(asteroid: Asteroid) {
        _navigateToSelectedAsteroid.value = asteroid
    }

    fun displayAsteroidDetailsComplete() {
        _navigateToSelectedAsteroid.value = null
    }

    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }

}