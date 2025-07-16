package com.example.sample.common.dto

import com.example.sample.common.model.IcasAppointment
import com.example.sample.common.model.IcasAppointmentEntity
import com.example.sample.common.model.IcasAppointmentStatus
import com.example.sample.common.model.IcasAppointmentType
import com.example.sample.network.appointment.data.IcasAppointmentData
import java.time.ZonedDateTime


fun IcasAppointmentData.toModel(): IcasAppointment {
    val appointmentStatus = icasAppointmentStatusfromString(status)
    return IcasAppointment(
        uuid = id,
        startDate = ZonedDateTime.parse(checkNotNull(startDate)),
        endDate = endDate?.let(ZonedDateTime::parse),
        displayName = displayName.orEmpty(),
        description = description.orEmpty(),
        status = appointmentStatus,
        type = icasAppointmentTypeFromString(type),
        createdBy = icasAppointmentEntityfromString(createdBy?.type),
        canceledBy = if (appointmentStatus.isCancelled) icasAppointmentEntityfromString(canceledBy?.type) else null,
        canCancel = actions?.cancel ?: false && !appointmentStatus.isCanceledOrArchived,
        canMove = actions?.move ?: false,
        canRequestAgain = actions?.reqAgain ?: false,
        uploadDocsAllowed = actions?.uploadDocs ?: false,
    )
}

fun IcasAppointmentStatus.toDataModel(): String = when (this) {
    IcasAppointmentStatus.ApproveRejected -> STATUS_APPROVE_REJECTED
    IcasAppointmentStatus.ApproveWaiting -> STATUS_APPROVE_WAITING
    IcasAppointmentStatus.Approved -> STATUS_APPROVED
    IcasAppointmentStatus.Archived -> STATUS_ARCHIVED
    IcasAppointmentStatus.BookingError -> STATUS_BOOKING_ERROR
    IcasAppointmentStatus.Cancelled -> STATUS_CANCELED
    IcasAppointmentStatus.CancelledByTenant -> STATUS_CANCELED_BY_TENANT
    IcasAppointmentStatus.CancelledByExtSys -> STATUS_CANCELED_BY_EXT_SYS
    IcasAppointmentStatus.Completed -> STATUS_COMPLETED
    IcasAppointmentStatus.Pending -> STATUS_PENDING
    IcasAppointmentStatus.UserNotFinished -> STATUS_USER_NOT_FINISHED
    IcasAppointmentStatus.Unknown -> error("Not allowed")
}


fun icasAppointmentStatusfromString(status: String?) = when (status) {
    STATUS_APPROVE_REJECTED -> IcasAppointmentStatus.ApproveRejected
    STATUS_APPROVE_WAITING -> IcasAppointmentStatus.ApproveWaiting
    STATUS_APPROVED -> IcasAppointmentStatus.Approved
    STATUS_ARCHIVED -> IcasAppointmentStatus.Archived
    STATUS_BOOKING_ERROR -> IcasAppointmentStatus.BookingError
    STATUS_CANCELED -> IcasAppointmentStatus.Cancelled
    STATUS_CANCELED_BY_TENANT -> IcasAppointmentStatus.CancelledByTenant
    STATUS_CANCELED_BY_EXT_SYS -> IcasAppointmentStatus.CancelledByExtSys
    STATUS_COMPLETED -> IcasAppointmentStatus.Completed
    STATUS_PENDING -> IcasAppointmentStatus.Pending
    STATUS_USER_NOT_FINISHED -> IcasAppointmentStatus.UserNotFinished
    else -> IcasAppointmentStatus.Unknown
}


fun icasAppointmentEntityfromString(value: String?) = when (value) {
    ENTITY_REFERRAL -> IcasAppointmentEntity.Referral
    ENTITY_HOSPITAL -> IcasAppointmentEntity.Hospital
    else -> IcasAppointmentEntity.Person
}

fun icasAppointmentTypeFromString(value: String?): IcasAppointmentType = when (value) {
    TYPE_APPOINTMENT -> IcasAppointmentType.Appointment
    TYPE_STAY -> IcasAppointmentType.Stay
    else -> IcasAppointmentType.Unknown
}


private const val STATUS_APPROVE_REJECTED = "APPROVE_REJECTED"
private const val STATUS_APPROVE_WAITING = "APPROVE_WAITING"
private const val STATUS_APPROVED = "APPROVED"
private const val STATUS_ARCHIVED = "ARCHIVED"
private const val STATUS_BOOKING_ERROR = "BOOKING_ERROR"
private const val STATUS_CANCELED = "CANCELED"
private const val STATUS_CANCELED_BY_TENANT = "CANCELED_BY_TENANT"
private const val STATUS_CANCELED_BY_EXT_SYS = "CANCELED_BY_EXT_SYS"
private const val STATUS_COMPLETED = "COMPLETED"
private const val STATUS_PENDING = "PENDING"
private const val STATUS_USER_NOT_FINISHED = "USER_NOT_FINISHED"

private const val ENTITY_REFERRAL = "tenant"
private const val ENTITY_HOSPITAL = "extSys"

private const val TYPE_APPOINTMENT = "APPOINTMENT"
private const val TYPE_STAY = "STAY"
