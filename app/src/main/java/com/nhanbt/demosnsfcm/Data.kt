package com.nhanbt.demosnsfcm

import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("EventType")
    var eventType: String? = null,

    @SerializedName("Message")
    var message: String? = null
)
