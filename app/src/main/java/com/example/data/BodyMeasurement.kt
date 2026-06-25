package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "body_measurements")
data class BodyMeasurement(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val weight: Float? = null,
    val leftBicep: Float? = null,
    val rightBicep: Float? = null,
    val leftCalf: Float? = null,
    val rightCalf: Float? = null,
    val chest: Float? = null,
    val waist: Float? = null,
    val hips: Float? = null,
    val leftForearm: Float? = null,
    val rightForearm: Float? = null,
    val leftThigh: Float? = null,
    val rightThigh: Float? = null,
    val notes: String? = null
)
