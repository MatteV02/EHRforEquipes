package com.MatteV02.EHRforEquipes.commonModuleBenchmark.benchmark.insertVisitBenchmark

import com.MatteV02.EHRforEquipes.commonModule.dbManagement.DBEntityManager
import com.MatteV02.EHRforEquipes.commonModule.entities.visit.Exam
import com.MatteV02.EHRforEquipes.commonModule.entities.visit.Visit
import com.MatteV02.EHRforEquipes.commonModuleBenchmark.benchmark.BenchmarkExecutionTime
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.time.measureTime

object InsertVisitBenchmark: BenchmarkExecutionTime {
    override fun run(dbEntityManager: DBEntityManager, range: IntRange): List<BenchmarkExecutionTime.Result> {
        val subRanges = range.linearSubdivision(1000)
        val results = mutableListOf<BenchmarkExecutionTime.Result>()

        for (subRange in subRanges) {
            val duration = measureTime {
                insertNVisits(dbEntityManager, subRange)
            }.div(subRange.count())

            results.add(
                BenchmarkExecutionTime.Result(
                numberOfElement = subRange.last,
                duration
            ))
        }

        return results
    }

    private fun insertNVisits(dbEntityManager: DBEntityManager, range: IntRange) {
        for (i in range) {
            dbEntityManager.insert(Visit.numeratedVisit(i))
        }
    }
}

private fun IntRange.linearSubdivision(steps: Int): List<IntRange> {
    val returnList = mutableListOf<IntRange>()
    val subRangeLength = count() / steps

    if (subRangeLength <= 1) {
        this.forEach { i ->
            returnList.add(i..i)
        }
    } else {
        var startSubRange = first
        for (i in this step subRangeLength) {
            if (i != startSubRange) {
                returnList.add(startSubRange.until(i))
                startSubRange = i
            }
        }
        returnList.add(startSubRange..last())
    }

    return returnList
}

private fun Exam.Companion.numeratedExam(i: Int): Exam {
    return Exam(
        description = "Exam$i"
    )
}

private fun Visit.Companion.numeratedVisit(i: Int): Visit {
    val visit = Visit(
        specialist = "Visit$i"
    )

    for (e in 0..Random.nextInt(0..4)) {
        visit.addExam(Exam.numeratedExam(e))
    }

    return visit
}