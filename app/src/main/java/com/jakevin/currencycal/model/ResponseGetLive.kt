package com.jakevin.currencycal.model

data class ResponseGetLive(
    val success: Boolean,
    val timestamp:Long,
    val source: String,
    val quotes:HashMap<String,Double>
)