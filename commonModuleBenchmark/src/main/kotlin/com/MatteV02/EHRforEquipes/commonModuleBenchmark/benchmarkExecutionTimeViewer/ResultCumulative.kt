package com.MatteV02.EHRforEquipes.commonModuleBenchmark.benchmarkExecutionTimeViewer

import com.MatteV02.EHRforEquipes.commonModuleBenchmark.benchmark.BenchmarkExecutionTime.Result
import kotlin.time.Duration

fun List<Result>.cumulative(): List<Result> {
    val cumulativeResults = mutableListOf<Result>()
    var prev = Duration.ZERO

    this.forEach { result ->
        cumulativeResults.add(Result(result.numberOfElement, prev + result.duration))
        prev += result.duration
    }

    return cumulativeResults
}