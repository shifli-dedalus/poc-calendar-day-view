package com.example.sample.network.appointment

import com.example.sample.network.appointment.data.IcasAppointmentData
import com.example.sample.network.send
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod

class AppointmentApi(
    private val networkClient: HttpClient
) {

    internal val path = "/v1/appointments"

    suspend fun getAppointments(
        completion: (Result<List<IcasAppointmentData>>) -> Unit
    ) {
        networkClient.send<List<IcasAppointmentData>>(
            method = HttpMethod.Get,
            path = path,
            requestBody = null,
            dataProcesser = completion
        )
    }

}