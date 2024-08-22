package coreplugins.patientplugin

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dbManagement.DBEntityManager.ConnectionTypes
import dbManagement.HibernateDBEntityManager
import entities.patient.Patient
import entities.patient.Patient.Companion.Gender.MALE
import entities.place.Place
import entities.visit.Visit
import io.kotest.core.spec.style.FunSpec
import java.time.LocalDate

class VisitEditViewTest : FunSpec({
    fun mainWindow(visit: Visit) = application {


        Window(onCloseRequest = ::exitApplication) {
            MaterialTheme {
                VisitEditView(visit, Modifier)
            }
        }
    }

    test("coreplugins.patientplugin.VisitEditViewTest") {
        val plugin = PatientPlugin
        val dbEntityManager = HibernateDBEntityManager
        dbEntityManager.dbConnection(ConnectionTypes.VOLATILE)

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
        dbEntityManager.insert(p)

        val pl = Place(
            name = "FKT", city = "Carpi"
        )
        dbEntityManager.insert(pl)

        val visit = Visit()
        dbEntityManager.insert(visit)

        plugin.start(dbEntityManager)

        mainWindow(visit)
    }
})