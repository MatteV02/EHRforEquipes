package com.MatteV02.EHRforEquipes.commonModuleBenchmark.benchmarkExecutionTimeViewer

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.MatteV02.EHRforEquipes.commonModuleBenchmark.benchmark.BenchmarkExecutionTime
import io.github.koalaplot.core.line.LinePlot
import io.github.koalaplot.core.style.LineStyle
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.xygraph.*
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log10
import kotlin.time.Duration
import kotlin.time.DurationUnit

private fun Duration.getUnit(): DurationUnit {
    val unitList = listOf(DurationUnit.NANOSECONDS, DurationUnit.MICROSECONDS, DurationUnit.MILLISECONDS).reversed()

    unitList.forEach { durationUnit ->
        if (this.toInt(durationUnit) != 0) {
            return durationUnit
        }
    }

    return DurationUnit.MILLISECONDS
}

class BenchmarkExecutionTimeViewerImpl(
    private val label: String
) : BenchmarkExecutionTimeViewer {
    private val plots = mutableSetOf<Plot>()

    private data class Plot(
        val data: List<BenchmarkExecutionTime.Result>,
        val label: String,
        val color: SolidColor
    )

    private fun computeUnit(): DurationUnit {
        var durationUnit = DurationUnit.MILLISECONDS

        plots.forEach { plot ->
            plot.data.forEach { result ->
                if (result.duration.getUnit().ordinal < durationUnit.ordinal) {
                    durationUnit = result.duration.getUnit()
                }
            }
        }

        return durationUnit
    }

    private fun xRange(): IntRange {
        val rangeMin = plots.map { it.data }.minOf { list -> list.minOf { r -> r.numberOfElement } }
        val rangeMax = plots.map { it.data }.maxOf { list -> list.maxOf { r -> r.numberOfElement } }

        return rangeMin..rangeMax
    }

    private fun yRange(): IntRange {
        val rangeMin = plots.map { it.data }.minOf { list -> list.minOf { r -> r.duration.toInt(computeUnit()) } }
        val rangeMax = plots.map { it.data }.maxOf { list -> list.maxOf { r -> r.duration.toInt(computeUnit()) } }

        return rangeMin..rangeMax
    }

    override fun addGraph(results: List<BenchmarkExecutionTime.Result>, label: String, color: SolidColor) {
        plots.add(Plot(results, label, color))
    }

    @OptIn(ExperimentalKoalaPlotApi::class)
    @Composable
    override fun view(modifier: Modifier) {
        Column(modifier) {
            Text(
                label,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 10.dp)
            )

            Box {
                XYGraph(
                    xAxisModel = IntLinearAxisModel(xRange()),
                    xAxisTitle = "Elements",
                    yAxisModel = LogAxisModel(yRange().logRange()),
                    yAxisTitle = "Time (${computeUnit().name.lowercase()})"
                ) {
                    plots.forEach { plot ->
                        LinePlot(
                            plot.data.map {
                                DefaultPoint(
                                    it.numberOfElement,
                                    it.duration.toInt(computeUnit()).toFloat()
                                )
                            },
                            lineStyle = LineStyle(plot.color, strokeWidth = 2.dp),
                        )
                    }
                }

                Surface(
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(all = 10.dp)
                ) {
                    Column {
                        plots.forEach { plot ->
                            Row(
                                modifier = Modifier.padding(bottom = 10.dp)
                            ) {
                                Surface(
                                    Modifier
                                        .size(20.dp, 20.dp)
                                        .padding(end = 10.dp),
                                    color = plot.color.value
                                ) { }
                                Text(plot.label, Modifier.align(Alignment.CenterVertically))
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun IntRange.logRange() = floor(log10(start.toDouble())).toInt()..ceil(log10(last.toDouble())).toInt()
