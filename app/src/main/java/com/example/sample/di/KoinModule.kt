package com.example.sample.di

import com.example.sample.network.Network
import com.example.sample.network.appointment.AppointmentApi
import com.example.sample.repo.AppointmentRepo
import com.example.sample.ui.viewmodel.DayTimeSlotViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val koinModule = module {
    single { Network.getHttpClient(androidContext()) }
    single { AppointmentApi(get()) }

    singleOf(::AppointmentRepo)

    viewModelOf(::DayTimeSlotViewModel)
}