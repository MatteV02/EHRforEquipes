package com.MatteV02.EHRforEquipes.commonModuleBenchmark.benchmark

import com.MatteV02.EHRforEquipes.commonModule.dbManagement.DBEntityManager
import kotlin.time.Duration

interface BenchmarkExecutionTime {
    fun run(dbEntityManager: DBEntityManager, range: IntRange = 1..10_000): List<Result>

    data class Result(
        val numberOfElement: Int,
        val duration: Duration
    )
}