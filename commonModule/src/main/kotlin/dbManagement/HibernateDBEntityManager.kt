package dbManagement

import dbManagement.DBEntityManager.ConnectionTypes
import entities.patient.Patient
import entities.place.Place
import entities.visit.Visit
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
    name = "#getPlacesFromDB",
    query = "from Place"
)
object HibernateDBEntityManager : DBEntityManager {
    var sessionFactory: SessionFactory? = null

    object Defaults {
        val permanentConfiguration: Configuration = Configuration()
            .addAnnotatedClass(Patient::class.java)
            .addAnnotatedClass(Place::class.java)
            .addAnnotatedClass(Visit::class.java)
            .addAnnotatedClass(HibernateDBEntityManager::class.java)
            .setProperty(AvailableSettings.JAKARTA_JDBC_USER, "sa")
            .setProperty(AvailableSettings.JAKARTA_JDBC_PASSWORD, "")
            .setProperty(AvailableSettings.JAKARTA_HBM2DDL_CREATE_SCHEMAS, true)
            .setProperty(AvailableSettings.JAKARTA_HBM2DDL_DATABASE_ACTION, Action.UPDATE)
            .setProperty(CONNECTION_PROVIDER, "org.hibernate.hikaricp.internal.HikariCPConnectionProvider")
            .setProperty("hibernate.hikari.maximumPoolSize", 20)
            .setProperty(AvailableSettings.JAKARTA_JDBC_URL, "jdbc:h2:./db")

        val volatileConfiguration: Configuration = Configuration()
            .addAnnotatedClass(Patient::class.java)
            .addAnnotatedClass(Place::class.java)
            .addAnnotatedClass(Visit::class.java)
            .addAnnotatedClass(HibernateDBEntityManager::class.java)
            .setProperty(AvailableSettings.JAKARTA_JDBC_USER, "sa")
            .setProperty(AvailableSettings.JAKARTA_JDBC_PASSWORD, "")
            .setProperty(AvailableSettings.JAKARTA_HBM2DDL_CREATE_SCHEMAS, true)
            .setProperty(AvailableSettings.JAKARTA_HBM2DDL_DATABASE_ACTION, Action.UPDATE)
            .setProperty(CONNECTION_PROVIDER, "org.hibernate.hikaricp.internal.HikariCPConnectionProvider")
            .setProperty("hibernate.hikari.maximumPoolSize", 20)
            .setProperty(SHOW_SQL, true)
            .setProperty(FORMAT_SQL, true)
            .setProperty(HIGHLIGHT_SQL, true)
            .setProperty(AvailableSettings.JAKARTA_JDBC_URL, "jdbc:h2:mem:")

    }

    override fun dbConnection(connectionType: ConnectionTypes) {
        val configuration = when (connectionType) {
            ConnectionTypes.PERMANENT -> Defaults.permanentConfiguration
            ConnectionTypes.VOLATILE -> Defaults.volatileConfiguration
        }

        sessionFactory = configuration.buildSessionFactory()
    }

    override fun insert(o: Any) {
        sessionFactory?.inTransaction { session ->
            session.persist(o)
        } ?: throw DBEntityManagerNotConnectedException()
    }

    override fun update(o: Any) {
        sessionFactory?.inTransaction { session ->
            session.merge(o)
        } ?: throw DBEntityManagerNotConnectedException()
    }

    override fun remove(o: Any) {
        if (o is Patient || o is Place) {
            val visits = getVisits()
                .filter { v ->
                    when (o) {
                        is Patient -> v.patient == o
                        is Place -> v.place == o
                        else -> false
                    }
                }
                visits.forEach { remove(it) }
        }

        sessionFactory?.inTransaction { session ->
            val toDelete = session.merge(o)
            session.remove(toDelete)
        } ?: throw DBEntityManagerNotConnectedException()

    }

    override fun getPatients() : List<Patient> {
        return HibernateDBEntityManager_.getPatientsFromDB(sessionFactory?.createEntityManager())
            ?: throw DBEntityManagerNotConnectedException()
    }

    override fun getPlaces() : List<Place> {
        return HibernateDBEntityManager_.getPlacesFromDB(sessionFactory?.createEntityManager())
            ?: throw DBEntityManagerNotConnectedException()
    }

    override fun getVisits() : List<Visit> {
        return HibernateDBEntityManager_.getVisitsFromDB(sessionFactory?.createEntityManager())
            ?: throw DBEntityManagerNotConnectedException()
    }
}