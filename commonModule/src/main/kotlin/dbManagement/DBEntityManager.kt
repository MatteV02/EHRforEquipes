package dbManagement

import entities.*
import org.hibernate.SessionFactory
import org.hibernate.annotations.NamedQuery
import org.hibernate.annotations.processing.CheckHQL

@CheckHQL
@NamedQuery(
    name = "#getPatientsFromDB",
    query = "from Patient"
)
@NamedQuery(
    name = "#getVisitsFromDB",
    query = "from Visit"
)
@NamedQuery(
    name = "#getSpecialistsFromDB",
    query = "select visit.specialist from Visit visit"
)
@NamedQuery(
    name = "#getVisitsRelatedToPatientFromDB",
    query = "select visit from Visit visit where visit.patient = :patient"
)
@NamedQuery(
    name = "#getPlacesFromDB",
    query = "from Place"
)
object DBEntityManager {
    var sessionFactory: SessionFactory? = null

    fun insert(ob: Any) {
        sessionFactory?.inTransaction { session ->
            session.persist(ob)
        } ?: throw DBEntityManagerNotInitializedException()
    }

    fun update(ob: Any) {
        sessionFactory?.inTransaction { session ->
            session.merge(ob)
        } ?: throw DBEntityManagerNotInitializedException()
    }

    fun remove(ob: Any) {
        sessionFactory?.inTransaction { session ->
            session.remove(ob)
        } ?: throw DBEntityManagerNotInitializedException()
    }

    fun getPatients() : List<Patient> {
        return DBEntityManager_.getPatientsFromDB(sessionFactory?.createEntityManager())
            ?: throw DBEntityManagerNotInitializedException()
    }

    fun getPlaces() : List<Place> {
        return DBEntityManager_.getPlacesFromDB(sessionFactory?.createEntityManager())
            ?: throw DBEntityManagerNotInitializedException()
    }

    fun getVisits() : List<Visit> {
        return DBEntityManager_.getVisitsFromDB(sessionFactory?.createEntityManager())
            ?: throw DBEntityManagerNotInitializedException()
    }

    fun getVisitsOfPatient(patient: Patient) : List<Visit> {
        return DBEntityManager_.getVisitsRelatedToPatientFromDB(sessionFactory?.createEntityManager(), patient)
            ?: throw DBEntityManagerNotInitializedException()
    }

    fun getSpecialists() : List<String> {
        return DBEntityManager_.getSpecialistsFromDB(sessionFactory?.createEntityManager())
            ?: throw DBEntityManagerNotInitializedException()
    }

    class DBEntityManagerNotInitializedException : RuntimeException("DBEntityManager not initialized")
}