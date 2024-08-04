import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dbManagement.DBEntityManager
import entities.Patient
import entities.Patient.Companion.Gender.MALE
import entities.Place
import entities.Visit
import io.kotest.core.spec.style.FunSpec
import org.hibernate.cfg.AvailableSettings
import org.hibernate.cfg.Configuration
import org.hibernate.cfg.JdbcSettings.*
import org.hibernate.tool.schema.Action
import ui.patientEditView
import java.time.LocalDate
import java.util.Locale.ENGLISH

class PatientUITest : FunSpec({
    fun mainWindow() = application {
        val p = Patient(
            name = "Veroni Matteo",
            gender = MALE,
            dateOfBirth = LocalDate.parse("2002-11-23"),
            placeOfBirth = "Modena",
            residence = "Carpi",
            FC = "VRNMTT8347724",
            phoneNumber = "01874016220",
            landlinePhoneNumber = "",
            mail = "matteo.veroni@mail.com",
            doctor = "Elisa Lombardi"
        )
        DBEntityManager.insert(p)

        Window(onCloseRequest = ::exitApplication) {
            MaterialTheme {
                val locale = ENGLISH
                patientEditView(
                    patient = p, locale
                )
            }
        }
    }

    test("PatientUITest") {
        val sessionFactory = Configuration()
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
            .setProperty(AvailableSettings.JAKARTA_JDBC_URL,  "jdbc:h2:mem:")
            .buildSessionFactory()
        DBEntityManager.sessionFactory = sessionFactory

        mainWindow()
    }
})