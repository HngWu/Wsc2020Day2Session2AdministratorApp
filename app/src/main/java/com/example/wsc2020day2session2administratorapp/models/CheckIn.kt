package com.example.wsc2020day2session2administratorapp.models

import kotlinx.serialization.Serializable

@Serializable
data class CheckIn(
    val competitorId: String,
    val checkInTime: String? = null
)