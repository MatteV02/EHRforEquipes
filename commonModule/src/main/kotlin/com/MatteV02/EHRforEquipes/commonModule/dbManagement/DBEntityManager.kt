package com.MatteV02.EHRforEquipes.commonModule.dbManagement

import com.MatteV02.EHRforEquipes.commonModule.entities.patient.Patient
import com.MatteV02.EHRforEquipes.commonModule.entities.place.Place
import com.MatteV02.EHRforEquipes.commonModule.entities.visit.Visit

/**
 *
 */
interface DBEntityManager {
    fun dbConnection(connectionType: ConnectionTypes = ConnectionTypes.PERMANENT)

    fun insert(o: Any)

    fun update(o: Any)

    fun remove(o: Any)

    fun getPatients(): List<Patient>

    fun getPlaces(): List<Place>

    fun getVisits(): List<Visit>

    enum class ConnectionTypes {
        PERMANENT,
        VOLATILE
    }
}