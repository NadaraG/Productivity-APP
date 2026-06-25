package com.example.data

import kotlinx.coroutines.flow.Flow

class BodyMeasurementRepository(private val dao: BodyMeasurementDao) {
    val allMeasurements: Flow<List<BodyMeasurement>> = dao.getAll()

    suspend fun insert(measurement: BodyMeasurement) {
        dao.insert(measurement)
    }

    suspend fun delete(measurement: BodyMeasurement) {
        dao.delete(measurement)
    }

    suspend fun deleteById(id: Int) {
        dao.deleteById(id)
    }
}
