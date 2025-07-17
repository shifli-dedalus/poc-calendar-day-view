package com.example.sample.repo

import com.example.sample.common.dto.toModel
import com.example.sample.common.model.IcasAppointment
import com.example.sample.network.appointment.AppointmentApi

class AppointmentRepo(
    val appointmentApi: AppointmentApi
) {

    suspend fun getAllAppointments(completion: (Result<List<IcasAppointment>>) -> Unit) {
        appointmentApi.getAppointments { result ->
            runCatching {
                result.onSuccess { data ->
                    println("getAllAppointments runCatching onSuccess data = $data")
                    // if needed we can store in db
                    val icasAppointments: List<IcasAppointment> = data.map { it.toModel() }
                    completion(Result.success(icasAppointments))
                }
            }.onFailure {
                println("getAllAppointments runCatching onFailure err = $it")
                completion(Result.failure(it))
            }

            result.onFailure {
                println("getAllAppointments result onFailure err = $it")
                completion(Result.failure(it))
            }
        }
    }

}