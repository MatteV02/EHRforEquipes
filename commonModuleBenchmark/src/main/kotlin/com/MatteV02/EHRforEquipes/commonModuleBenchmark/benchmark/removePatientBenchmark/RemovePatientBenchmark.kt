package com.MatteV02.EHRforEquipes.commonModuleBenchmark.benchmark.removePatientBenchmark

import com.MatteV02.EHRforEquipes.commonModule.dbManagement.DBEntityManager
import com.MatteV02.EHRforEquipes.commonModule.entities.patient.Patient
import com.MatteV02.EHRforEquipes.commonModuleBenchmark.benchmark.BenchmarkExecutionTime
import kotlin.time.measureTime

object RemovePatientBenchmark: BenchmarkExecutionTime {
    override fun run(dbEntityManager: DBEntityManager, range: IntRange): List<BenchmarkExecutionTime.Result> {
        val subRanges = range.linearSubdivision(1000)
        val results = mutableListOf<BenchmarkExecutionTime.Result>()

        for (subRange in subRanges) {
            val patients = subRange.map { i -> Patient.numeratedPatient(i) }
            patients.forEach { dbEntityManager.insert(it) }

            val duration = measureTime {
                removeNPatients(patients, dbEntityManager)
            }.div(subRange.count())

            results.add(
                BenchmarkExecutionTime.Result(
                numberOfElement = subRange.last,
                duration
            ))
        }

        return results
    }

    private fun removeNPatients(patients: List<Patient>, dbEntityManager: DBEntityManager) {
        patients.forEach{ patient ->
            dbEntityManager.remove(patient)
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

private fun Patient.Companion.numeratedPatient(i: Int) = Patient(
    name = "patient$i"
)

