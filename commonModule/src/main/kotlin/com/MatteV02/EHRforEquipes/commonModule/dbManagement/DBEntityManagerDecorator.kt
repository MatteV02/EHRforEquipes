package com.MatteV02.EHRforEquipes.commonModule.dbManagement

import com.MatteV02.EHRforEquipes.commonModule.entities.patient.Patient
import com.MatteV02.EHRforEquipes.commonModule.entities.place.Place
import com.MatteV02.EHRforEquipes.commonModule.entities.visit.Visit

abstract class DBEntityManagerDecorator(
    protected val entityManager: DBEntityManager
) : DBEntityManager {
    override fun dbConnection(connectionType: DBEntityManager.ConnectionTypes) {
        entityManager.dbConnection(connectionType)
    }

    override fun insert(o: Any) {
        entityManager.insert(o)
    }

    override fun update(o: Any) {
        entityManager.update(o)
    }

    override fun remove(o: Any) {
        entityManager.remove(o)
    }

    override fun getPatients(): List<Patient> {
        return entityManager.getPatients()
    }

    override fun getPlaces(): List<Place> {
        return entityManager.getPlaces()
    }

    override fun getVisits(): List<Visit> {
        return entityManager.getVisits()
    }
}