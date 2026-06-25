package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BodyMeasurementDao {
    @Query("SELECT * FROM body_measurements ORDER BY timestamp DESC")
    fun getAll(): Flow<List<BodyMeasurement>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(measurement: BodyMeasurement)

    @Delete
    suspend fun delete(measurement: BodyMeasurement)

    @Query("DELETE FROM body_measurements WHERE id = :id")
    suspend fun deleteById(id: Int)
}
