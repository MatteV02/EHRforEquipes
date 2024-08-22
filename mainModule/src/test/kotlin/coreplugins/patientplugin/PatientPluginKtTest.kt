package coreplugins.patientplugin

import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dbManagement.DBEntityManager.ConnectionTypes
import dbManagement.HibernateDBEntityManager
import entities.patient.Patient
import entities.visit.Visit
import io.kotest.core.spec.style.FunSpec
import java.time.LocalDate

class PatientPluginKtTest : FunSpec({

    fun mainWindow() = application {
        val plugin = PatientPlugin
        var showDialog by remember { mutableStateOf(false) }



//        plugin.loadData(
//            patients = DBEntityManager.getPatients().toMutableList(),
//            places = DBEntityManager.getPlaces().toMutableList(),
//            visits = DBEntityManager.getVisits().toMutableList()
//        )

        Window(onCloseRequest = ::exitApplication) {
            MaterialTheme {
                Column {
                    plugin.view(modifier = Modifier.weight(1.0f))
                    NavigationBar {
                        plugin.menuEntry(
                            true,
                            onClick = {
                                showDialog = true
                            },
                            modifier = Modifier
                        )
                    }
                }
            }
        }

        if (showDialog) {
            DialogWindow(onCloseRequest = { showDialog = false }) {
                MaterialTheme {
                    Text("clicked")
                }
            }
        }
    }

    test("PatientPlugin") {
        val plugin = PatientPlugin
        val dbEntityManager = HibernateDBEntityManager
        dbEntityManager.dbConnection(ConnectionTypes.VOLATILE)

        val marioRossi = Patient(name = "Mario Rossi", dateOfBirth = LocalDate.of(2001, 1, 1), residence = "via Ponti, 7, Carpi (MO)")


        val patients = mutableListOf(
                marioRossi,
                Patient(name = "Angelica Alvani", dateOfBirth = LocalDate.of(1997, 1, 1), residence = "via Quartz, 7, Carpi (MO)"),
                Patient(name = "siujm", dateOfBirth = LocalDate.of(2001, 1, 1), residence = "via Ponti, 7, Carpi (MO)")
            )

        val visits = mutableListOf(
                Visit(
                    patient = marioRossi,
                    specialist = "Marco Pini",
                    date = LocalDate.of(2001, 11, 27)
                )
            )

        listOf(patients, visits).forEach { list ->
            list.forEach { o -> dbEntityManager.insert(o) }
        }

        plugin.start(dbEntityManager)

        mainWindow()
    }
})
