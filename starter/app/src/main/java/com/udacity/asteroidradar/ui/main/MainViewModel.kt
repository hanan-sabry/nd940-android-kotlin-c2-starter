package com.udacity.asteroidradar.ui.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.BuildConfig
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.network.api.AsteroidApiFilter
import com.udacity.asteroidradar.network.api.PictureOfDayApi
import com.udacity.asteroidradar.network.api.getNextSevenDayFormattedDate
import com.udacity.asteroidradar.network.api.getTodayFormattedDate
import com.udacity.asteroidradar.repository.AsteroidsRepository
import kotlinx.coroutines.launch

enum class PictureOfDayMediaType(val value: String) { IMAGE("image"), VIDEO("video") }

enum class AsteroidApiStatus { LOADING, ERROR, DONE }

class MainViewModel(application: Application) : ViewModel() {


    private val database = getDatabase(application)
    private val asteroidsRepository = AsteroidsRepository(database)

    private val _status = MutableLiveData<AsteroidApiStatus>()
    val status: LiveData<AsteroidApiStatus>
        get() = _status

//    init {
//        _status.value = AsteroidApiStatus.LOADING
//        getPictureOfDay()
//        viewModelScope.launch {
//            try {
//                asteroidsRepository.refreshAsteroids()
//                _status.value = AsteroidApiStatus.DONE
//            } catch (ex: java.lang.Exception) {
//                _status.value = AsteroidApiStatus.ERROR
//            }
//        }
//    }


    //
//    private val _asteroids = MutableLiveData<List<Asteroid>>()
//    val asteroids: LiveData<List<Asteroid>>
//        get() = _asteroids

    var asteroidList: LiveData<List<Asteroid>> = asteroidsRepository.getTodayAsteroidsBy(
        getTodayFormattedDate()
    )

    private val _pictureOfDay = MutableLiveData<PictureOfDay>()
    val pictureOfDay: LiveData<PictureOfDay>
        get() = _pictureOfDay

    private val _navigateToSelectedAsteroid = MutableLiveData<Asteroid>()
    val navigateToSelectedAsteroid: LiveData<Asteroid>
        get() = _navigateToSelectedAsteroid

    init {
        viewModelScope.launch {
            asteroidsRepository.refreshAsteroids()
        }
        getPictureOfDay()
    }

    private fun getPictureOfDay() {
        viewModelScope.launch {
            try {
                _pictureOfDay.value =
                    PictureOfDayApi.retrofitService.getPictureOfDay(BuildConfig.API_KEY)
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
        asteroidList = when (filter) {
            AsteroidApiFilter.TODAY ->
                asteroidsRepository.getTodayAsteroidsBy(getTodayFormattedDate())
            AsteroidApiFilter.WEEK ->
                asteroidsRepository.getWeekAsteroids(
                    getTodayFormattedDate(),
                    getNextSevenDayFormattedDate()
                )
            AsteroidApiFilter.SAVED ->
                asteroidsRepository.allAsteroids
        }
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