package dbManagement

import entities.*
import org.hibernate.SessionFactory
import org.hibernate.annotations.NamedQuery
import org.hibernate.annotations.processing.CheckHQL
import org.hibernate.cfg.AvailableSettings
import org.hibernate.cfg.Configuration
import org.hibernate.cfg.JdbcSettings.*
import org.hibernate.tool.schema.Action

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
    fun getDefault(): SessionFactory = Configuration()
        .addAnnotatedClass(Patient::class.java)
        .addAnnotatedClass(Place::class.java)
        .addAnnotatedClass(Visit::class.java)
        .addAnnotatedClass(DBEntityManager::class.java)
        .setProperty(AvailableSettings.JAKARTA_JDBC_USER, "sa")
        .setProperty(AvailableSettings.JAKARTA_JDBC_PASSWORD, "")
        .setProperty(AvailableSettings.JAKARTA_HBM2DDL_CREATE_SCHEMAS, true)
        .setProperty(AvailableSettings.JAKARTA_HBM2DDL_DATABASE_ACTION, Action.UPDATE)
        .setProperty("hibernate.agroal.maxSize", 20)
        .setProperty(SHOW_SQL, true)
        .setProperty(FORMAT_SQL, true)
        .setProperty(HIGHLIGHT_SQL, true)
        .setProperty(AvailableSettings.JAKARTA_JDBC_URL,  "jdbc:h2:db")
        .buildSessionFactory()

    var sessionFactory: SessionFactory = getDefault()

    fun insert(ob: Any) {
        sessionFactory.inTransaction { session ->
            session.persist(ob)
        }
    }

    fun update(ob: Any) {
        sessionFactory.inTransaction { session ->
            session.merge(ob)
        }
    }

    fun remove(ob: Any) {
        sessionFactory.inTransaction { session ->
            session.remove(ob)
        }
    }

    fun getPatients() : List<Patient> {
        return DBEntityManager_.getPatientsFromDB(sessionFactory.createEntityManager())
    }

    fun getPlaces() : List<Place> {
        return DBEntityManager_.getPlacesFromDB(sessionFactory.createEntityManager())
    }

    fun getVisits() : List<Visit> {
        return DBEntityManager_.getVisitsFromDB(sessionFactory.createEntityManager())
    }

    fun getVisitsOfPatient(patient: Patient) : List<Visit> {
        return DBEntityManager_.getVisitsRelatedToPatientFromDB(sessionFactory.createEntityManager(), patient)
    }

    fun getSpecialists() : List<String> {
        return DBEntityManager_.getSpecialistsFromDB(sessionFactory.createEntityManager())
    }
}