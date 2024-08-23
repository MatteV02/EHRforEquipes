package coreplugins.patientplugin

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.MatteV02.EHRforEquipes.commonModule.dbManagement.DBEntityManager.*
import com.MatteV02.EHRforEquipes.commonModule.dbManagement.HibernateDBEntityManager
import com.MatteV02.EHRforEquipes.commonModule.entities.patient.Patient
import com.MatteV02.EHRforEquipes.commonModule.entities.place.Place
import com.MatteV02.EHRforEquipes.commonModule.entities.visit.Visit
import com.MatteV02.EHRforEquipes.mainModule.patientplugin.PatientPlugin
import com.MatteV02.EHRforEquipes.mainModule.patientplugin.VisitEditView
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