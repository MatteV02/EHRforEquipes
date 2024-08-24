package com.MatteV02.EHRforEquipes.commonModuleBenchmark.benchmark.insertPlaceBenchmark

import com.MatteV02.EHRforEquipes.commonModule.dbManagement.DBEntityManager
import com.MatteV02.EHRforEquipes.commonModule.entities.place.Place
import com.MatteV02.EHRforEquipes.commonModuleBenchmark.benchmark.BenchmarkExecutionTime
import kotlin.time.measureTime

object InsertPlaceBenchmark: BenchmarkExecutionTime {
    override fun run(dbEntityManager: DBEntityManager, range: IntRange): List<BenchmarkExecutionTime.Result> {
        val subRanges = range.linearSubdivision(1000)
        val results = mutableListOf<BenchmarkExecutionTime.Result>()

        for (subRange in subRanges) {
            val duration = measureTime {
                insertNPlaces(dbEntityManager, subRange)
            }.div(subRange.count())

            results.add(
                BenchmarkExecutionTime.Result(
                numberOfElement = subRange.last,
                duration
            ))
        }

        return results
    }

    private fun insertNPlaces(dbEntityManager: DBEntityManager, range: IntRange) {
        for (i in range) {
            dbEntityManager.insert(Place.numeratedPlace(i))
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

private fun Place.Companion.numeratedPlace(i: Int) = Place(
    city = "Place$i"
)
