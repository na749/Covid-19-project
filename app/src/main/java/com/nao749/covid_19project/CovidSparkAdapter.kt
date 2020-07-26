package com.nao749.covid_19project

import android.graphics.RectF
import com.robinhood.spark.SparkAdapter

class CovidSparkAdapter(private  val historyData: List<CovidData>): SparkAdapter() {

    var metric = Metric.PCR
    var daysAgo = TimeScale.MAX

    override fun getY(index: Int): Float {
        val chodenData = historyData[index]
        return when(metric) {

            Metric.POSITIVE -> chodenData.pcr.toFloat()
            Metric.PCR -> chodenData.positive.toFloat()
            Metric.DEATH -> chodenData.death.toFloat()

        }
    }

    override fun getItem(index: Int) = historyData[index]

    override fun getCount() = historyData.size

    override fun getDataBounds(): RectF {
        val bounds = super.getDataBounds()

        if (daysAgo != TimeScale.MAX) {
            bounds.left = count - daysAgo.numDays.toFloat()
        }

        return bounds
    }

}
