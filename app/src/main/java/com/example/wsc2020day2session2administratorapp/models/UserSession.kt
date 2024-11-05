package com.example.wsc2020day2session2administratorapp.models

import kotlinx.serialization.Serializable

@Serializable
data class UserSession(
    val token: String,
    val role: String,
    val userId: String
)