package com.example.wsc2020day2session2administratorapp.models

import kotlinx.serialization.Serializable

@Serializable
data class Auth(
    val token: String?
)