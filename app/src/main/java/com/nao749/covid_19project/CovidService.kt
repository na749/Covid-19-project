package com.nao749.covid_19project

import retrofit2.Call
import retrofit2.http.GET

interface CovidService{

    @GET("total?history=true")
    fun getHistoryData():Call<List<CovidData>>

}