package dbManagement

import entities.patient.Patient
import entities.place.Place
import entities.visit.Visit

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