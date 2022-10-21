package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AsteroidDao {
    @Query("select * from databaseasteroid")
    fun getAllAsteroids(): LiveData<List<DatabaseAsteroid>>

    @Query("select * from databaseasteroid where closeApproachDate = :date")
    fun getAsteroidsByDate(date: String): LiveData<List<DatabaseAsteroid>>

    @Query("select * from databaseasteroid where closeApproachDate between :date1 and :date2")
    fun getWeekAsteroids(date1: String, date2: String): LiveData<List<DatabaseAsteroid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(asteroids: List<DatabaseAsteroid>)
}

@Database(entities = [DatabaseAsteroid::class], version = 1)
abstract class AsteroidsDatabase : RoomDatabase() {
    abstract val asteroidDao: AsteroidDao
}

private lateinit var INSTANCE: AsteroidsDatabase

fun getDatabase(context: Context): AsteroidsDatabase {
    synchronized(AsteroidsDatabase::class.java) {}
    if (!::INSTANCE.isInitialized) {
        INSTANCE = Room.databaseBuilder(
            context.applicationContext,
            AsteroidsDatabase::class.java,
            "asteroids"
        ).build()
    }
    return INSTANCE
}