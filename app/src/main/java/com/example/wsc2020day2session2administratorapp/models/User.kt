package com.example.wsc2020day2session2administratorapp.models

import kotlinx.serialization.Serializable


@Serializable
data class User(
    val email: String,
    val password: String
)