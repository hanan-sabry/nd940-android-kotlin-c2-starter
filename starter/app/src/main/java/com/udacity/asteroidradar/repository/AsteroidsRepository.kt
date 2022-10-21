package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.BuildConfig
import com.udacity.asteroidradar.database.AsteroidsDatabase
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.network.api.*
import com.udacity.asteroidradar.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.HttpException

class AsteroidsRepository(private val database: AsteroidsDatabase) {

    val allAsteroids: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAllAsteroids()) {
            it.asDomainModel()
        }

    fun getTodayAsteroidsBy(date: String) : LiveData<List<Asteroid>> {
        return Transformations.map(database.asteroidDao.getAsteroidsByDate(date)) {
            it.asDomainModel()
        }
    }

    fun getWeekAsteroids(date1: String, date2: String) : LiveData<List<Asteroid>> {
        return Transformations.map(database.asteroidDao.getWeekAsteroids(date1, date2)) {
            it.asDomainModel()
        }
    }

    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            try {
                val asteroidsJsonResult = AsteroidApi.retrofitService.getAsteroids(
                    getTodayFormattedDate(),
                    getNextSevenDayFormattedDate(),
                    BuildConfig.API_KEY
                )
                val asteroids = parseAsteroidsJsonResult(JSONObject(asteroidsJsonResult))
                database.asteroidDao.insertAll(asteroids.asDatabaseModel())
            } catch (ex: HttpException) {
                println(ex.message())
            }
        }
    }
}