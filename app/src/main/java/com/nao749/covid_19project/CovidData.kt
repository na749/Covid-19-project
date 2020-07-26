package com.nao749.covid_19project

import java.util.*

data class CovidData(
    val date: Date,
    val pcr: Int,
    val positive: Int,
    val death : Int
)