package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CigaretteDao {
    @Query("SELECT * FROM cigarette_logs ORDER BY timestamp DESC")
    fun getAll(): Flow<List<CigaretteLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: CigaretteLog)

    @Delete
    suspend fun delete(log: CigaretteLog)

    @Query("DELETE FROM cigarette_logs WHERE id = :id")
    suspend fun deleteById(id: Int)
    
    @Query("DELETE FROM cigarette_logs")
    suspend fun deleteAll()
}
