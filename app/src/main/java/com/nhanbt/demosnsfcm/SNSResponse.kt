package com.nhanbt.demosnsfcm

import com.google.gson.annotations.SerializedName

data class SNSResponse(
    @SerializedName("data")
    var data: Data? = Data()
)
