package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.BuildConfig
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.database.AsteroidsDatabase
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.network.api.*
import com.udacity.asteroidradar.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AsteroidsRepository(private val database: AsteroidsDatabase) {

    val allAsteroids: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAllAsteroids()) {
            it.asDomainModel()
        }

    val todayAsteroids : LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAsteroidsByDate(getTodayFormattedDate())) {
            it.asDomainModel()
        }


    val weekAsteroids: LiveData<List<Asteroid>> =
         Transformations.map(database.asteroidDao.getWeekAsteroids(getTodayFormattedDate(), getNextSevenDayFormattedDate())) {
            it.asDomainModel()
        }

    suspend fun getTodayAsteroidsService(): LiveData<List<Asteroid>> {
        return MutableLiveData(
            parseAsteroidsJsonResult(
                JSONObject(
                    AsteroidApi.retrofitService.getAsteroids(
                        getTodayFormattedDate(), getTodayFormattedDate(), BuildConfig.API_KEY
                    )
                )
            )
        )
    }

    suspend fun getWeekAsteroidsService(): LiveData<List<Asteroid>> {
        return MutableLiveData(
            parseAsteroidsJsonResult(
                JSONObject(
                    AsteroidApi.retrofitService.getAsteroids(
                        getTodayFormattedDate(), getNextSevenDayFormattedDate(), BuildConfig.API_KEY
                    )
                )
            )
        )
    }

    suspend fun getPictureOfDay() : PictureOfDay{
        return PictureOfDayApi.retrofitService.getPictureOfDay(BuildConfig.API_KEY)
    }

    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            val asteroidsJsonResult = AsteroidApi.retrofitService.getAsteroids(
                getTodayFormattedDate(),
                getNextSevenDayFormattedDate(),
                BuildConfig.API_KEY
            )
            val asteroids = parseAsteroidsJsonResult(JSONObject(asteroidsJsonResult))
            database.asteroidDao.insertAll(asteroids.asDatabaseModel())
        }
    }
}