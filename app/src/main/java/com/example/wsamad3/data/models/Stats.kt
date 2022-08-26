package com.example.wsamad3.data.models

data class Stats(
    val tittle :String,
    val infected : Int,
    val death : Int,
    val recovered : Int,
    val vaccinated : Int,
    val recovered_adults : Int,
    val recovered_young : Int,
)
