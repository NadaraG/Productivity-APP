package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.BodyMeasurement
import com.example.data.BodyMeasurementRepository
import com.example.data.CigaretteLog
import com.example.data.CigaretteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppLanguage {
    ENGLISH,
    GEORGIAN
}

enum class MetricUnit(val symbol: String) {
    METRIC("Metric (kg, cm)"),
    IMPERIAL("Imperial (lbs, in)")
}

sealed interface SelectedMetric {
    object Weight : SelectedMetric
    object Chest : SelectedMetric
    object Waist : SelectedMetric
    object Hips : SelectedMetric
    object LeftBicep : SelectedMetric
    object RightBicep : SelectedMetric
    object LeftForearm : SelectedMetric
    object RightForearm : SelectedMetric
    object LeftThigh : SelectedMetric
    object RightThigh : SelectedMetric
    object LeftCalf : SelectedMetric
    object RightCalf : SelectedMetric

    val displayName: String
        get() = when (this) {
            Weight -> "Weight"
            Chest -> "Chest"
            Waist -> "Waist"
            Hips -> "Hips"
            LeftBicep -> "Left Bicep"
            RightBicep -> "Right Bicep"
            LeftForearm -> "Left Forearm"
            RightForearm -> "Right Forearm"
            LeftThigh -> "Left Thigh"
            RightThigh -> "Right Thigh"
            LeftCalf -> "Left Calf"
            RightCalf -> "Right Calf"
        }
    
    fun getValue(measurement: BodyMeasurement): Float? {
        return when (this) {
            Weight -> measurement.weight
            Chest -> measurement.chest
            Waist -> measurement.waist
            Hips -> measurement.hips
            LeftBicep -> measurement.leftBicep
            RightBicep -> measurement.rightBicep
            LeftForearm -> measurement.leftForearm
            RightForearm -> measurement.rightForearm
            LeftThigh -> measurement.leftThigh
            RightThigh -> measurement.rightThigh
            LeftCalf -> measurement.leftCalf
            RightCalf -> measurement.rightCalf
        }
    }
}

class TrackerViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: BodyMeasurementRepository
    val measurements: StateFlow<List<BodyMeasurement>>

    private val cigaretteRepository: CigaretteRepository
    val cigaretteLogs: StateFlow<List<CigaretteLog>>

    private val prefs = application.getSharedPreferences("cigarette_prefs", Context.MODE_PRIVATE)

    private val _pricePerPack = MutableStateFlow(prefs.getFloat("price_per_pack", 12.50f))
    val pricePerPack: StateFlow<Float> = _pricePerPack.asStateFlow()

    private val _cigarettesPerPack = MutableStateFlow(prefs.getInt("cigarettes_per_pack", 20))
    val cigarettesPerPack: StateFlow<Int> = _cigarettesPerPack.asStateFlow()

    private val _dailyTarget = MutableStateFlow(prefs.getInt("daily_target", 10))
    val dailyTarget: StateFlow<Int> = _dailyTarget.asStateFlow()

    private val _monthlyTarget = MutableStateFlow(prefs.getInt("monthly_target", 200))
    val monthlyTarget: StateFlow<Int> = _monthlyTarget.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = BodyMeasurementRepository(database.bodyMeasurementDao())
        measurements = repository.allMeasurements.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        cigaretteRepository = CigaretteRepository(database.cigaretteDao())
        cigaretteLogs = cigaretteRepository.allLogs.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    private val _appLanguage = MutableStateFlow(
        try {
            AppLanguage.valueOf(prefs.getString("app_language", AppLanguage.ENGLISH.name) ?: AppLanguage.ENGLISH.name)
        } catch (e: Exception) {
            AppLanguage.ENGLISH
        }
    )
    val appLanguage: StateFlow<AppLanguage> = _appLanguage.asStateFlow()

    fun setLanguage(language: AppLanguage) {
        _appLanguage.value = language
        prefs.edit().putString("app_language", language.name).apply()
    }

    private val _unitPreference = MutableStateFlow(MetricUnit.METRIC)
    val unitPreference: StateFlow<MetricUnit> = _unitPreference.asStateFlow()

    private val _selectedMetric = MutableStateFlow<SelectedMetric>(SelectedMetric.Weight)
    val selectedMetric: StateFlow<SelectedMetric> = _selectedMetric.asStateFlow()

    fun toggleUnit() {
        _unitPreference.value = if (_unitPreference.value == MetricUnit.METRIC) {
            MetricUnit.IMPERIAL
        } else {
            MetricUnit.METRIC
        }
    }

    fun selectMetric(metric: SelectedMetric) {
        _selectedMetric.value = metric
    }

    fun addMeasurement(measurement: BodyMeasurement) {
        viewModelScope.launch {
            repository.insert(measurement)
        }
    }

    fun deleteMeasurement(id: Int) {
        viewModelScope.launch {
            repository.deleteById(id)
        }
    }

    // Pre-populates clean data for a quick and beautiful visual demo of the progress charts if requested
    fun populateDemoData() {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val oneDayMs = 24 * 60 * 60 * 1000L
            val demoList = listOf(
                BodyMeasurement(
                    timestamp = now - 15 * oneDayMs,
                    weight = 84.5f, chest = 104.0f, waist = 90.0f, hips = 102.0f,
                    leftBicep = 37.0f, rightBicep = 37.2f, leftForearm = 30.0f, rightForearm = 30.1f,
                    leftThigh = 60.0f, rightThigh = 60.2f, leftCalf = 39.0f, rightCalf = 39.1f,
                    notes = "Starting tracking!"
                ),
                BodyMeasurement(
                    timestamp = now - 10 * oneDayMs,
                    weight = 83.2f, chest = 103.5f, waist = 88.5f, hips = 101.5f,
                    leftBicep = 37.1f, rightBicep = 37.3f, leftForearm = 30.1f, rightForearm = 30.2f,
                    leftThigh = 59.5f, rightThigh = 59.7f, leftCalf = 38.9f, rightCalf = 39.0f,
                    notes = "Active workouts."
                ),
                BodyMeasurement(
                    timestamp = now - 5 * oneDayMs,
                    weight = 82.0f, chest = 103.0f, waist = 87.0f, hips = 101.0f,
                    leftBicep = 37.3f, rightBicep = 37.4f, leftForearm = 30.2f, rightForearm = 30.3f,
                    leftThigh = 59.0f, rightThigh = 59.2f, leftCalf = 38.8f, rightCalf = 38.9f,
                    notes = "Clean diet feeling great!"
                ),
                BodyMeasurement(
                    timestamp = now,
                    weight = 81.1f, chest = 102.5f, waist = 85.5f, hips = 100.2f,
                    leftBicep = 37.5f, rightBicep = 37.6f, leftForearm = 30.3f, rightForearm = 30.4f,
                    leftThigh = 58.2f, rightThigh = 58.4f, leftCalf = 38.7f, rightCalf = 38.8f,
                    notes = "Steady body composition progress."
                )
            )
            for (item in demoList) {
                repository.insert(item)
            }
        }
    }

    fun updatePricePerPack(value: Float) {
        _pricePerPack.value = value
        prefs.edit().putFloat("price_per_pack", value).apply()
    }

    fun updateCigarettesPerPack(value: Int) {
        _cigarettesPerPack.value = value
        prefs.edit().putInt("cigarettes_per_pack", value).apply()
    }

    fun updateDailyTarget(value: Int) {
        _dailyTarget.value = value
        prefs.edit().putInt("daily_target", value).apply()
    }

    fun updateMonthlyTarget(value: Int) {
        _monthlyTarget.value = value
        prefs.edit().putInt("monthly_target", value).apply()
    }

    fun logCigarette(quantity: Int = 1) {
        viewModelScope.launch {
            cigaretteRepository.insert(
                CigaretteLog(
                    timestamp = System.currentTimeMillis(),
                    quantity = quantity
                )
            )
        }
    }

    fun deleteCigaretteLog(id: Int) {
        viewModelScope.launch {
            cigaretteRepository.deleteById(id)
        }
    }

    fun clearAllCigaretteLogs() {
        viewModelScope.launch {
            cigaretteRepository.deleteAll()
        }
    }

    fun populateCigaretteDemoData() {
        viewModelScope.launch {
            cigaretteRepository.deleteAll()
            val now = System.currentTimeMillis()
            val oneDayMs = 24 * 60 * 60 * 1000L
            
            // Populate logs for the past 30 days
            for (i in 0..30) {
                val dayTimestamp = now - i * oneDayMs
                val countForDay = when {
                    i == 0 -> 4       // Today: 4
                    i % 7 == 0 -> 12  // Exceeded limit (10)
                    i % 5 == 0 -> 3
                    i % 3 == 0 -> 8
                    else -> 6
                }
                
                if (countForDay > 0) {
                    for (c in 1..countForDay) {
                        val logTimestamp = dayTimestamp - (c * 2 * 60 * 60 * 1000L)
                        cigaretteRepository.insert(
                            CigaretteLog(
                                timestamp = logTimestamp,
                                quantity = 1
                            )
                        )
                    }
                }
            }
        }
    }
}
