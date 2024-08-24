package com.MatteV02.EHRforEquipes.commonModuleBenchmark.benchmark.benchmarkExecutionTimeViewer

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import com.MatteV02.EHRforEquipes.commonModuleBenchmark.benchmark.BenchmarkExecutionTime

interface BenchmarkExecutionTimeViewer {

    fun addGraph(results: List<BenchmarkExecutionTime.Result>, label: String, color: SolidColor)

    @Composable
    fun view(modifier: Modifier)

}