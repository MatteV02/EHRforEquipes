import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
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
import ui.visitEditView
import java.time.LocalDate

class VisitEditViewTest : FunSpec({
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

        val pl = Place(
            name = "FKT", city = "Carpi"
        )
        DBEntityManager.insert(pl)

        val visit = Visit()
        DBEntityManager.insert(visit)

        Window(onCloseRequest = ::exitApplication) {
            MaterialTheme {
                visitEditView(visit, Modifier)
            }
        }
    }

    test("VisitEditViewTest") {
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