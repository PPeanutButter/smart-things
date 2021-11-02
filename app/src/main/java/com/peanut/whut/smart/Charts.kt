package com.peanut.whut.smart

import android.graphics.Color
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.DecimalFormat

object Charts {
    fun PieChart.applyData(list: List<Pair<String, Int>>) {
        val entries: ArrayList<PieEntry> = ArrayList()
        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        for (pair in list) {
            entries.add(
                PieEntry(
                    pair.second.toFloat(),
                    pair.first,
                    null
                )
            )
        }
        val dataSet = PieDataSet(entries, "")
        dataSet.setDrawIcons(false)
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 5f
        // add a lot of colors
        val colors: ArrayList<Int> = ArrayList()
        for (c in ColorTemplate.COLORFUL_COLORS) colors.add(c)
        for (c in ColorTemplate.LIBERTY_COLORS) colors.add(c)
        for (c in ColorTemplate.PASTEL_COLORS) colors.add(c)
        colors.add(ColorTemplate.getHoloBlue())
        dataSet.colors = colors
        //dataSet.setSelectionShift(0f);
        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(11f)
        data.setValueTextColor(Color.WHITE)
        this.data = data
        // undo all highlights
        this.highlightValues(null)
        this.invalidate()
    }

    fun PieChart.applyStyle(centerText: String, entryLabel: Boolean): PieChart {
        this.setUsePercentValues(false)
        this.description.isEnabled = false
        this.setExtraOffsets(0f, 0f, 30f, 0f)
        this.dragDecelerationFrictionCoef = 0.95f
        this.centerText = centerText
        this.isDrawHoleEnabled = true
        this.setHoleColor(Color.WHITE)
        this.setTransparentCircleColor(Color.WHITE)
        this.setTransparentCircleAlpha(255)
        this.holeRadius = 58f
        this.transparentCircleRadius = 61f
        this.setDrawCenterText(true)
        this.setCenterTextSize(20f)
        this.rotationAngle = -90f
        // enable rotation of the chart by touch
        this.isRotationEnabled = true
        this.isHighlightPerTapEnabled = true
        this.animateY(1400, Easing.EaseInOutQuad)
        // chart.spin(2000, 0, 360);
        val l: Legend = this.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l.orientation = Legend.LegendOrientation.VERTICAL
        l.setDrawInside(false)
        l.xEntrySpace = 7f
        l.yEntrySpace = 0f
        l.yOffset = 0f
        // entry label styling
        this.setDrawEntryLabels(entryLabel)
        this.setEntryLabelColor(Color.WHITE)
        this.setEntryLabelTextSize(12f)
        return this
    }

    fun LineChart.applyStyle(): LineChart {
        this.setViewPortOffsets(0f, 0f, 0f, 0f)
        this.setBackgroundColor(Color.WHITE)
        // no description text
        this.description.isEnabled = false
        // enable touch gestures
        this.setTouchEnabled(true)
        // enable scaling and dragging
        this.isDragEnabled = true
        this.setScaleEnabled(true)
        // if disabled, scaling can be done on x- and y-axis separately
        this.setPinchZoom(false)
        this.setDrawGridBackground(false)
        this.maxHighlightDistance = 300f
        val x: XAxis = this.xAxis
        x.setLabelCount(6, false)
        x.textColor = Color.BLACK
        x.position = XAxis.XAxisPosition.BOTTOM_INSIDE
        x.setDrawGridLines(false)
        x.axisLineColor = Color.BLACK
        val y: YAxis = this.axisLeft
        y.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return value.toInt().toString() + "mL"
            }
        }
        y.setLabelCount(6, false)
        y.textColor = Color.BLACK
        y.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
        y.setDrawGridLines(false)
        y.axisLineColor = Color.BLACK
        this.axisRight.isEnabled = false
        // lower max, as cubic runs significantly slower than linear
        this.legend.isEnabled = false
        this.animateXY(2000, 2000)
        // don't forget to refresh the drawing
        this.invalidate()
        return this
    }

    fun LineChart.applyData(list: List<Pair<String, Int>>) {
        val values: ArrayList<Entry> = ArrayList()
        val temp = list.reversed()
        for ((i, pair) in temp.withIndex()) {
            values.add(Entry(i.toFloat(), DecimalFormat("#.00").format(pair.second).toFloat()))
        }
        // create a dataset and give it a type
        val set1 = LineDataSet(values, "DataSet 1")
        set1.mode = LineDataSet.Mode.CUBIC_BEZIER
        set1.cubicIntensity = 0.2f
        set1.setDrawFilled(true)
        set1.setDrawCircles(false)
        set1.lineWidth = 1.8f
        set1.highLightColor = Color.rgb(244, 117, 117)
        set1.color = Color.BLACK
        set1.fillColor = Color.RED
        set1.fillAlpha = 100
        set1.setDrawHorizontalHighlightIndicator(false)
        set1.fillFormatter = IFillFormatter { _, _ ->
            return@IFillFormatter this.axisLeft.axisMinimum
        }
        // create a data object with the data sets
        val data = LineData(set1)
        data.setValueTextSize(9f)
        data.setDrawValues(false)
        val x = this.xAxis
        x.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return temp[value.toInt()].first
            }
        }
        // set data
        this.data = data
    }
}