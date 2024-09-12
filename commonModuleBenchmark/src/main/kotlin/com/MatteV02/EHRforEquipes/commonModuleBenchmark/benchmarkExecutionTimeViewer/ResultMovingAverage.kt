package com.MatteV02.EHRforEquipes.commonModuleBenchmark.benchmarkExecutionTimeViewer

import com.MatteV02.EHRforEquipes.commonModuleBenchmark.benchmark.BenchmarkExecutionTime.Result
import kotlin.time.Duration

fun List<Result>.movingAverage(): List<Result> {
    val movingAverageList = mutableListOf<Result>()

    this.forEachIndexed { index, result ->
        var average: Duration = Duration.ZERO
        var denominator = 0

        for (j in -5..5) {
            val r = this.getOrNull(index + j)
            if (r != null) {
                average += r.duration
                denominator++
            }
        }

        movingAverageList.add(Result(result.numberOfElement, average.div(denominator)))
    }

    return movingAverageList
}