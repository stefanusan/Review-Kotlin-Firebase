package com.lazday.moneykotlin.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import java.io.Serializable

data class Transaction (
        var id: String?,
        var amount: Int,
        var category: String,
        var note: String,
        var type: String,
        var username: String,
//        @ServerTimestamp
        var created: Timestamp? = Timestamp.now()
) : Serializable