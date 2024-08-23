package com.MatteV02.commonModuleBenchmark.benchmark

import com.MatteV02.EHRforEquipes.commonModule.dbManagement.DBEntityManager
import java.awt.Point

interface BenchmarkExecutionTime {
    fun run(dbEntityManager: DBEntityManager): Map<Point, Point>
}