package com.nao749.covid_19project

enum class Metric{
    PCR,POSITIVE,DEATH
}

enum class TimeScale(val numDays : Int){
    MAX(-1),MONTH(30),WEEK(7)
}

