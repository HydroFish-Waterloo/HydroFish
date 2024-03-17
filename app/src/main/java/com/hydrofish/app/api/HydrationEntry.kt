package com.hydrofish.app.api

import com.google.gson.annotations.SerializedName
import java.util.Date

data class HydrationEntry(
    @SerializedName("day") val date: Date,
    @SerializedName("total_ml") val hydrationAmount: Int
)