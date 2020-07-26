package com.nao749.covid_19project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.google.gson.GsonBuilder
import com.robinhood.ticker.TickerUtils
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

private const val BASE_URL = "https://covid19-japan-web-api.now.sh/api/v1/"
class MainActivity : AppCompatActivity() {

    private lateinit var currentlyShownData: List<CovidData>
    private lateinit var adapter: CovidSparkAdapter
    private lateinit var HistoryData: List<CovidData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val gson = GsonBuilder().setDateFormat("yyyyMMdd").create()
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        val covidService = retrofit.create(CovidService::class.java)

        //データをとってくる
        covidService.getHistoryData().enqueue(object : Callback<List<CovidData>>{
            override fun onFailure(call: Call<List<CovidData>>, t: Throwable) {
                Log.e("key" , "onFailure $t")
            }

            override fun onResponse(
                call: Call<List<CovidData>>,
                response: Response<List<CovidData>>
            ) {
                Log.i("key","onResponse $response")
                val historyData =  response.body()
                if(historyData == null){
                    Log.w("key","Did'nt receive a valid response body")
                    return
                }
                setupEventListeners()
                HistoryData = historyData
                Log.i("key","great")
                updateDisplayData(HistoryData)
                //Todo

            }

        })

    }

    private fun setupEventListeners() {
        tvMetricLabel.setCharacterLists(TickerUtils.provideNumberList())

        sparkView.isScrubEnabled = true
        sparkView.setScrubListener { itemData ->
            if(itemData is CovidData){
                updateInfoForDate(itemData)
            }
        }

        radioGroupTime.setOnCheckedChangeListener{ _,checkedId ->
            when(checkedId){
                R.id.radioButtonWeek -> {
                    adapter.daysAgo = TimeScale.WEEK
                    textView.text = "週間"
                }
                R.id.radioButtonMonth -> {
                    adapter.daysAgo = TimeScale.MONTH
                    textView.text = "月間"
                }
                else -> {
                    adapter.daysAgo = TimeScale.MAX
                    textView.text = "累計"

                }
            }
            adapter.notifyDataSetChanged()
        }


        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when(checkedId){
                R.id.radioButtonPCR -> {
                    updateDisplayMetric(Metric.PCR)
                    textViewResearch.text = "PCR検査数"
                }
                R.id.radioButtonPositive-> {
                    updateDisplayMetric(Metric.POSITIVE)
                    textViewResearch.text = "陽性患者数"
                }
                R.id.radioButtonDeath -> {
                    updateDisplayMetric(Metric.DEATH)
                    textViewResearch.text = "死亡者数"
                }
            }
        }
    }

    private fun updateDisplayMetric(metric: Metric) {
        val colorRes = when(metric){
            Metric.PCR -> R.color.colorPCR
            Metric.POSITIVE -> R.color.colorPositive
            Metric.DEATH -> R.color.colordeath
        }

        @ColorInt val colorInt = ContextCompat.getColor(this,colorRes)
        sparkView.lineColor = colorInt
        tvMetricLabel.setTextColor(colorInt)

        adapter.metric = metric
        adapter.notifyDataSetChanged()

        updateInfoForDate(currentlyShownData.last())
    }

    private fun updateDisplayData(historyData: List<CovidData>) {

        currentlyShownData = historyData

        adapter = CovidSparkAdapter(historyData)

        sparkView.adapter = adapter

        radioButtonPCR.isChecked = true
        radioButtonMax.isChecked = true

        updateDisplayMetric(Metric.PCR)

    }



    private fun updateInfoForDate(covidData: CovidData) {
        val numCases = when(adapter.metric){
            Metric.PCR -> covidData.pcr
            Metric.POSITIVE -> covidData.positive
            Metric.DEATH -> covidData.death
        }
        tvMetricLabel.text = NumberFormat.getInstance().format(numCases)
        val outputDateFormat = SimpleDateFormat("MM dd, yyyy", Locale.JAPAN)
        tvDataLabel.text = outputDateFormat.format(covidData.date)
    }
}
