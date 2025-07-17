package com.example.sample.network.appointment.data


import kotlinx.serialization.Serializable

@Serializable
data class IcasAppointmentData(
    val id: String,
    val createdBy: IcasAppointmentActor? = null,
    val canceledBy: IcasAppointmentActor? = null,
    val displayName: String? = null,
    val description: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val status: String? = null,
    val type: String? = null,
    val actions: IcasAppointmentActions? = null,
    val attributes: IcasAppointmentAttributes? = null,
)

@Serializable
data class IcasAppointmentActor(
    val id: String? = null,
    val type: String? = null,
    val name: String? = null,
)

@Serializable
data class IcasAppointmentActions(
    val reqAgain: Boolean? = false,
    val retryBook: Boolean? = false,
    val cancel: Boolean? = false,
    val move: Boolean? = false,
    val uploadDocs: Boolean? = false,
)

@Serializable
data class IcasAppointmentAttributes(
    val tasksTab: Boolean? = false,
    val htmlInfoTab: Boolean? = false,
    val meetingTab: Boolean? = false,
)
