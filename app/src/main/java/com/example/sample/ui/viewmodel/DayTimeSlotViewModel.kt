package com.example.sample.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sample.common.model.IcasAppointment
import com.example.sample.repo.AppointmentRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DayTimeSlotViewModel(
    private val appointmentRepo: AppointmentRepo
): ViewModel() {

    val stateShowError = mutableStateOf(false)
    val errorText = mutableStateOf(null)

    private val _appointmentData = MutableStateFlow(emptyList<IcasAppointment>())
    val appointmentData = _appointmentData.asStateFlow()

    fun getAppointments() {
        viewModelScope.launch {
            appointmentRepo.getAllAppointments { result ->

                println("getAppointments result = $result, isSuccess = ${result.isSuccess}")

                if (result.isFailure) {
                    stateShowError.value = true
                }

                if (result.isSuccess) {
                    _appointmentData.value = result.getOrNull() ?: emptyList()
                }

            }
        }
    }

}