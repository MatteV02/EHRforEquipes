import dbManagement.DBEntityManager
import entities.Patient
import entities.Place
import entities.Visit
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.hibernate.cfg.AvailableSettings
import org.hibernate.cfg.Configuration
import org.hibernate.cfg.JdbcSettings.*
import org.hibernate.tool.schema.Action
import java.time.LocalDate
import kotlin.random.Random
import kotlin.random.nextInt

class UnitTest : FunSpec({
    val configuration = Configuration()
        .addAnnotatedClass(Patient::class.java)
        .addAnnotatedClass(Place::class.java)
        .addAnnotatedClass(Visit::class.java)
        .addAnnotatedClass(DBEntityManager::class.java)
        //.addAnnotatedClass(Visit.Companion.Exam::class.java)
        .setProperty(AvailableSettings.JAKARTA_JDBC_USER, "sa")
        .setProperty(AvailableSettings.JAKARTA_JDBC_PASSWORD, "")
        .setProperty(AvailableSettings.JAKARTA_HBM2DDL_CREATE_SCHEMAS, true)
        //.setProperty(AvailableSettings.JAKARTA_HBM2DDL_DATABASE_ACTION, Action.SPEC_ACTION_DROP_AND_CREATE)
        .setProperty("hibernate.agroal.maxSize", 20)
        .setProperty(SHOW_SQL, true)
        .setProperty(FORMAT_SQL, true)
        .setProperty(HIGHLIGHT_SQL, true)

    val memDBURL = "jdbc:h2:mem:"
    val fileDBURL = "jdbc:h2:./src/test/resources/"

    test("add patient") {
        val sessionFactory = configuration
            .setProperty(AvailableSettings.JAKARTA_JDBC_URL,  memDBURL)
            .setProperty(AvailableSettings.JAKARTA_HBM2DDL_DATABASE_ACTION, Action.SPEC_ACTION_DROP_AND_CREATE)
            .buildSessionFactory()
        DBEntityManager.sessionFactory = sessionFactory

        val p1 = Patient(
            name = "Veroni Matteo",
            gender = Patient.Companion.Gender.MALE,
            dateOfBirth = LocalDate.parse("2002-11-23"),
            placeOfBirth = "Modena",
            residence = "Carpi",
            FC = "VRNMTT8347724",
            phoneNumber = "01874016220",
            landlinePhoneNumber = "",
            mail = "matteo.veroni@mail.com",
            doctor = "Elisa Lombardi"
        )
        DBEntityManager.insert(p1)

        DBEntityManager.getPatients() shouldBe mutableListOf(p1)
    }

    test("edit patient") {
        val sessionFactory = configuration
            .setProperty(AvailableSettings.JAKARTA_JDBC_URL,  memDBURL + "editPatientDB")
            .setProperty(AvailableSettings.JAKARTA_HBM2DDL_DATABASE_ACTION, Action.SPEC_ACTION_DROP_AND_CREATE)
            .buildSessionFactory()
        DBEntityManager.sessionFactory = sessionFactory

        val p1 = Patient(
            name = "Veroni Matteo",
            gender = Patient.Companion.Gender.MALE,
            dateOfBirth = LocalDate.parse("2002-11-23"),
            placeOfBirth = "Modena",
            residence = "Carpi",
            FC = "VRNMTT8347724",
            phoneNumber = "01874016220",
            landlinePhoneNumber = "",
            mail = "matteo.veroni@mail.com",
            doctor = "Elisa Lombardi"
        )
        DBEntityManager.insert(p1)

        p1.name = "Pippo Baudo"
        DBEntityManager.update(p1)

        DBEntityManager.getPatients() shouldBe mutableListOf(p1)
    }

    test("remove patient") {
        val sessionFactory = configuration
            .setProperty(AvailableSettings.JAKARTA_JDBC_URL,  memDBURL)
            .setProperty(AvailableSettings.JAKARTA_HBM2DDL_DATABASE_ACTION, Action.SPEC_ACTION_DROP_AND_CREATE)
            .buildSessionFactory()
        DBEntityManager.sessionFactory = sessionFactory

        val p1 = Patient(
            name = "Veroni Matteo",
            gender = Patient.Companion.Gender.MALE,
            dateOfBirth = LocalDate.parse("2002-11-23"),
            placeOfBirth = "Modena",
            residence = "Carpi",
            FC = "VRNMTT8347724",
            phoneNumber = "01874016220",
            landlinePhoneNumber = "",
            mail = "matteo.veroni@mail.com",
            doctor = "Elisa Lombardi"
        )
        DBEntityManager.insert(p1)
        DBEntityManager.remove(p1)

        DBEntityManager.getPatients() shouldBe mutableListOf()
    }

    test("patient persistence") {
        val dbURL = fileDBURL + "patientPersistenceTestDB"
        var sessionFactory = configuration
            .setProperty(AvailableSettings.JAKARTA_JDBC_URL, dbURL)
            .setProperty(AvailableSettings.JAKARTA_HBM2DDL_DATABASE_ACTION, Action.SPEC_ACTION_DROP_AND_CREATE)
            .buildSessionFactory()
        DBEntityManager.sessionFactory = sessionFactory

        val patientList = mutableListOf<Patient>()
        for (i in 1..5) {
            val p = Patient(name = Random.nextInt().toString())
            patientList.add(p)
            DBEntityManager.insert(p)
        }

        sessionFactory = configuration
            .setProperty(AvailableSettings.JAKARTA_JDBC_URL,  dbURL)
            .setProperty(AvailableSettings.JAKARTA_HBM2DDL_DATABASE_ACTION, Action.ACTION_UPDATE)
            .buildSessionFactory()
        DBEntityManager.sessionFactory = sessionFactory

        DBEntityManager.getPatients() shouldBe patientList
    }

    test("add Place") {
        val sessionFactory = configuration
            .setProperty(AvailableSettings.JAKARTA_JDBC_URL,  memDBURL)
            .setProperty(AvailableSettings.JAKARTA_HBM2DDL_DATABASE_ACTION, Action.SPEC_ACTION_DROP_AND_CREATE)
            .buildSessionFactory()
        DBEntityManager.sessionFactory = sessionFactory

        val p1 = Place(
            name = "FKT",
            city = "Carpi"
        )
        DBEntityManager.insert(p1)

        DBEntityManager.getPlaces() shouldBe mutableListOf(p1)
    }

    test("edit Place") {
        val sessionFactory = configuration
            .setProperty(AvailableSettings.JAKARTA_JDBC_URL,  memDBURL + "editPatientDB")
            .setProperty(AvailableSettings.JAKARTA_HBM2DDL_DATABASE_ACTION, Action.SPEC_ACTION_DROP_AND_CREATE)
            .buildSessionFactory()
        DBEntityManager.sessionFactory = sessionFactory

        val p1 = Place(
            name = "FKT",
            city = "Carpi"
        )
        DBEntityManager.insert(p1)

        p1.name = "Clinica Tarabini"
        DBEntityManager.update(p1)

        DBEntityManager.getPlaces() shouldBe mutableListOf(p1)
    }

    test("remove Place") {
        val sessionFactory = configuration
            .setProperty(AvailableSettings.JAKARTA_JDBC_URL,  memDBURL)
            .setProperty(AvailableSettings.JAKARTA_HBM2DDL_DATABASE_ACTION, Action.SPEC_ACTION_DROP_AND_CREATE)
            .buildSessionFactory()
        DBEntityManager.sessionFactory = sessionFactory

        val p1 = Place(
            name = "FKT",
            city = "Carpi"
        )
        DBEntityManager.insert(p1)
        DBEntityManager.remove(p1)

        DBEntityManager.getPlaces() shouldBe mutableListOf()
    }

    test("places persistence") {
        val dbURL = fileDBURL + "placesPersistenceTestDB"
        var sessionFactory = configuration
            .setProperty(AvailableSettings.JAKARTA_JDBC_URL, dbURL)
            .setProperty(AvailableSettings.JAKARTA_HBM2DDL_DATABASE_ACTION, Action.SPEC_ACTION_DROP_AND_CREATE)
            .buildSessionFactory()
        DBEntityManager.sessionFactory = sessionFactory

        val placesList = mutableListOf<Place>()
        for (i in 1..5) {
            val p = Place(name = Random.nextInt().toString())
            placesList.add(p)
            DBEntityManager.insert(p)
        }

        sessionFactory = configuration
            .setProperty(AvailableSettings.JAKARTA_JDBC_URL,  dbURL)
            .setProperty(AvailableSettings.JAKARTA_HBM2DDL_DATABASE_ACTION, Action.ACTION_UPDATE)
            .buildSessionFactory()
        DBEntityManager.sessionFactory = sessionFactory

        DBEntityManager.getPlaces() shouldBe placesList
    }

    test("add Visit") {
        val sessionFactory = configuration
            .setProperty(AvailableSettings.JAKARTA_JDBC_URL,  memDBURL)
            .setProperty(AvailableSettings.JAKARTA_HBM2DDL_DATABASE_ACTION, Action.SPEC_ACTION_DROP_AND_CREATE)
            .buildSessionFactory()
        DBEntityManager.sessionFactory = sessionFactory

        val p1 = Patient(
            name = "Veroni Matteo",
            gender = Patient.Companion.Gender.MALE,
            dateOfBirth = LocalDate.parse("2002-11-23"),
            placeOfBirth = "Modena",
            residence = "Carpi",
            FC = "VRNMTT8347724",
            phoneNumber = "01874016220",
            landlinePhoneNumber = "",
            mail = "matteo.veroni@mail.com",
            doctor = "Elisa Lombardi"
        )
        DBEntityManager.insert(p1)

        val v1 = Visit(
            LocalDate.now(),
            p1,
            specialist = "Veroni Andrea",
        )
        v1.addExam(
            Visit.Companion.Exam(
                LocalDate.parse("2024-04-01"),
                description = "Try and cry"
            )
        )

        val v2 = Visit(
            LocalDate.now(),
            p1,
            specialist = "Malleo Andrea",
        )

        DBEntityManager.insert(v1)
        DBEntityManager.insert(v2)

        DBEntityManager.getVisits() shouldBe mutableListOf(v1, v2)
    }

    test("edit Visit") {
        val sessionFactory = configuration
            .setProperty(AvailableSettings.JAKARTA_JDBC_URL,  memDBURL)
            .setProperty(AvailableSettings.JAKARTA_HBM2DDL_DATABASE_ACTION, Action.SPEC_ACTION_DROP_AND_CREATE)
            .buildSessionFactory()
        DBEntityManager.sessionFactory = sessionFactory

        val p1 = Patient(
            name = "Veroni Matteo",
            gender = Patient.Companion.Gender.MALE,
            dateOfBirth = LocalDate.parse("2002-11-23"),
            placeOfBirth = "Modena",
            residence = "Carpi",
            FC = "VRNMTT8347724",
            phoneNumber = "01874016220",
            landlinePhoneNumber = "",
            mail = "matteo.veroni@mail.com",
            doctor = "Elisa Lombardi"
        )
        DBEntityManager.insert(p1)

        val v1 = Visit(
            LocalDate.now(),
            p1,
            specialist = "Veroni Andrea",
        )
        v1.addExam(
            Visit.Companion.Exam(
                LocalDate.parse("2024-04-01"),
                description = "Try and cry"
            )
        )

        val v2 = Visit(
            LocalDate.now(),
            p1,
            specialist = "Malleo Andrea",
        )

        DBEntityManager.insert(v1)
        DBEntityManager.insert(v2)

        v1.exams[0].description = "edited description"
        v2.date = LocalDate.parse("2024-09-14")
        DBEntityManager.update(v1)
        DBEntityManager.update(v2)

        DBEntityManager.getVisits() shouldBe mutableListOf(v1, v2)
    }

    test("remove Visit") {
        val sessionFactory = configuration
            .setProperty(AvailableSettings.JAKARTA_JDBC_URL,  memDBURL)
            .setProperty(AvailableSettings.JAKARTA_HBM2DDL_DATABASE_ACTION, Action.SPEC_ACTION_DROP_AND_CREATE)
            .buildSessionFactory()
        DBEntityManager.sessionFactory = sessionFactory

        val p1 = Patient(
            name = "Veroni Matteo",
            gender = Patient.Companion.Gender.MALE,
            dateOfBirth = LocalDate.parse("2002-11-23"),
            placeOfBirth = "Modena",
            residence = "Carpi",
            FC = "VRNMTT8347724",
            phoneNumber = "01874016220",
            landlinePhoneNumber = "",
            mail = "matteo.veroni@mail.com",
            doctor = "Elisa Lombardi"
        )
        DBEntityManager.insert(p1)

        val v1 = Visit(
            LocalDate.now(),
            p1,
            specialist = "Veroni Andrea",
        )
        v1.addExam(
            Visit.Companion.Exam(
                LocalDate.parse("2024-04-01"),
                description = "Try and cry"
            )
        )

        val v2 = Visit(
            LocalDate.now(),
            p1,
            specialist = "Malleo Andrea",
        )

        DBEntityManager.insert(v1)
        DBEntityManager.insert(v2)
        DBEntityManager.remove(v1)

        DBEntityManager.getVisits() shouldBe mutableListOf(v2)
    }

    test("visit persistence") {
        val dbURL = fileDBURL + "visitPersistenceTestDB"
        var sessionFactory = configuration
            .setProperty(AvailableSettings.JAKARTA_JDBC_URL, dbURL)
            .setProperty(AvailableSettings.JAKARTA_HBM2DDL_DATABASE_ACTION, Action.SPEC_ACTION_DROP_AND_CREATE)
            .buildSessionFactory()
        DBEntityManager.sessionFactory = sessionFactory
        val p1 = Patient(
            name = "Veroni Matteo",
            gender = Patient.Companion.Gender.MALE,
            dateOfBirth = LocalDate.parse("2002-11-23"),
            placeOfBirth = "Modena",
            residence = "Carpi",
            FC = "VRNMTT8347724",
            phoneNumber = "01874016220",
            landlinePhoneNumber = "",
            mail = "matteo.veroni@mail.com",
            doctor = "Elisa Lombardi"
        )
        DBEntityManager.insert(p1)
        val visitList = mutableListOf<Visit>()
        for (i in 1..5) {
            val v1 = Visit(
                LocalDate.parse("2024-06-${Random.nextInt(10..31)}"),
                p1,
                specialist = "Veroni Andrea",
            )
            v1.addExam(
                Visit.Companion.Exam(
                    LocalDate.parse("2024-04-01"),
                    description = "Try and cry"
                )
            )
            visitList.add(v1)
            DBEntityManager.insert(v1)
        }

        sessionFactory = configuration
            .setProperty(AvailableSettings.JAKARTA_JDBC_URL,  dbURL)
            .setProperty(AvailableSettings.JAKARTA_HBM2DDL_DATABASE_ACTION, Action.ACTION_UPDATE)
            .buildSessionFactory()
        DBEntityManager.sessionFactory = sessionFactory

        DBEntityManager.getVisits() shouldBe visitList
    }

    test("get visits of patient") {
        val sessionFactory = configuration
            .setProperty(AvailableSettings.JAKARTA_JDBC_URL,  memDBURL)
            .setProperty(AvailableSettings.JAKARTA_HBM2DDL_DATABASE_ACTION, Action.SPEC_ACTION_DROP_AND_CREATE)
            .buildSessionFactory()
        DBEntityManager.sessionFactory = sessionFactory

        val p1 = Patient(
            name = "Veroni Matteo",
            gender = Patient.Companion.Gender.MALE,
            dateOfBirth = LocalDate.parse("2002-11-23"),
            placeOfBirth = "Modena",
            residence = "Carpi",
            FC = "VRNMTT8347724",
            phoneNumber = "01874016220",
            landlinePhoneNumber = "",
            mail = "matteo.veroni@mail.com",
            doctor = "Elisa Lombardi"
        )
        DBEntityManager.insert(p1)

        val p2 = Patient(
            name = "Lucano Angelica",
            gender = Patient.Companion.Gender.FEMALE
        )
        DBEntityManager.insert(p2)

        val p1Visits = mutableListOf<Visit>()
        for (i in 1..5) {
            val v = Visit(
                LocalDate.parse("2024-05-${Random.nextInt(10..31)}"),
                patient = p1
            )
            p1Visits.add(v)
            DBEntityManager.insert(v)
        }

        val p2Visits = mutableListOf<Visit>()
        for (i in 1..5) {
            val v = Visit(
                LocalDate.parse("2024-05-${Random.nextInt(10..31)}"),
                patient = p2
            )
            p2Visits.add(v)
            DBEntityManager.insert(v)
        }

        DBEntityManager.getVisitsOfPatient(p1) shouldBe p1Visits
        DBEntityManager.getVisitsOfPatient(p2) shouldBe p2Visits
    }

    test("get specialists") {
        val sessionFactory = configuration
            .setProperty(AvailableSettings.JAKARTA_JDBC_URL,  memDBURL)
            .setProperty(AvailableSettings.JAKARTA_HBM2DDL_DATABASE_ACTION, Action.SPEC_ACTION_DROP_AND_CREATE)
            .buildSessionFactory()
        DBEntityManager.sessionFactory = sessionFactory

        val v1 = Visit(
            specialist = "Veroni Andrea"
        )
        DBEntityManager.insert(v1)

        val v2 = Visit(
            specialist = "Antonio Carlo"
        )
        DBEntityManager.insert(v2)

        val v3 = Visit(
            specialist = "Doctor House"
        )
        DBEntityManager.insert(v3)

        DBEntityManager.getSpecialists() shouldBe listOf("Veroni Andrea", "Antonio Carlo", "Doctor House")
    }
})