package com.example.sample.common.model

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.TypeParceler
import java.time.ZonedDateTime

internal object ZonedDateTimeParceler : Parceler<ZonedDateTime> {
    override fun create(parcel: Parcel): ZonedDateTime =
        ZonedDateTime.parse(parcel.readString())

    override fun ZonedDateTime.write(parcel: Parcel, flags: Int) {
        parcel.writeString(this.toString())
    }
}

@Parcelize
@TypeParceler<ZonedDateTime, ZonedDateTimeParceler>()
@SuppressLint("ParcelCreator")
data class IcasAppointment(
    val uuid: String,
    val startDate: ZonedDateTime,
    val endDate: ZonedDateTime?,
    val displayName: String,
    val description: String,
    val status: IcasAppointmentStatus,
    val type: IcasAppointmentType,
    val createdBy: IcasAppointmentEntity,
    val canceledBy: IcasAppointmentEntity?,
    val canCancel: Boolean,
    val canRequestAgain: Boolean,
    val canMove: Boolean,
    val uploadDocsAllowed: Boolean,
) : Parcelable

enum class IcasAppointmentStatus {
    ApproveRejected,
    ApproveWaiting,
    Approved,
    Archived, // Can only happen if appointment was cancelled and then the user deleted it (apparently)
    BookingError,
    Cancelled,
    CancelledByTenant,
    CancelledByExtSys,
    Completed,
    Pending,
    UserNotFinished,
    Unknown;

    val isCanceledOrArchived: Boolean
        get() = when (this) {
            ApproveRejected,
            Archived,
            Cancelled,
            CancelledByTenant,
            CancelledByExtSys -> true

            else -> false
        }

    val isCancelled
        get() = when (this) {
            Archived,
            Cancelled,
            CancelledByTenant,
            CancelledByExtSys -> true

            else -> false
        }
}

enum class IcasAppointmentEntity {
    Referral,
    Hospital,
    Person,
}

enum class IcasAppointmentType {
    Appointment,
    Stay,
    Unknown,
}
