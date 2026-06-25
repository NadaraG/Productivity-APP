package com.example.data

import kotlinx.coroutines.flow.Flow

class CigaretteRepository(private val dao: CigaretteDao) {
    val allLogs: Flow<List<CigaretteLog>> = dao.getAll()

    suspend fun insert(log: CigaretteLog) {
        dao.insert(log)
    }

    suspend fun delete(log: CigaretteLog) {
        dao.delete(log)
    }

    suspend fun deleteById(id: Int) {
        dao.deleteById(id)
    }
    
    suspend fun deleteAll() {
        dao.deleteAll()
    }
}
