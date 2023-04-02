package com.lazday.moneykotlin.model

import com.google.firebase.Timestamp

data class User (
        val name: String,
        val username: String,
        val password: String,
        val created: Timestamp
)