package com.example.wsc2020day2session2administratorapp.models

import kotlinx.serialization.Serializable

@Serializable
data class CreateUser (
    val fullName: String,
    val email: String,
    val password: String,
)