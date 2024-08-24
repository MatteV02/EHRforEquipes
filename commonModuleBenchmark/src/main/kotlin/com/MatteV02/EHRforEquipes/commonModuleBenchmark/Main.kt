package com.MatteV02.EHRforEquipes.commonModuleBenchmark

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.MatteV02.EHRforEquipes.commonModule.dbManagement.DBEntityManager
import com.MatteV02.EHRforEquipes.commonModule.dbManagement.HibernateDBEntityManager
import com.MatteV02.EHRforEquipes.commonModule.dbManagement.SQLDBEntityManager
import com.MatteV02.EHRforEquipes.commonModuleBenchmark.benchmark.benchmarkExecutionTimeViewer.BenchmarkExecutionTimeViewerImpl
import com.MatteV02.EHRforEquipes.commonModuleBenchmark.benchmark.insertPatientBenchmark.InsertPatientBenchmark
import com.MatteV02.EHRforEquipes.commonModuleBenchmark.benchmark.insertPlaceBenchmark.InsertPlaceBenchmark
import com.MatteV02.EHRforEquipes.commonModuleBenchmark.benchmark.insertVisitBenchmark.InsertVisitBenchmark
import com.MatteV02.EHRforEquipes.commonModuleBenchmark.benchmark.removePatientBenchmark.RemovePatientBenchmark
import com.MatteV02.EHRforEquipes.commonModuleBenchmark.benchmark.updatePatientBenchmark.UpdatePatientBenchmark

enum class Views(
    val label: String,
    val icon: @Composable () -> Unit
) {
    INSERT_PATIENT(
        label = "Patients insertion",
        icon = { Icon(painter = painterResource("icons/patient_insertion_24px.xml"), null) }
    ),
    INSERT_PLACES(
        label = "Places insertion",
        icon = { Icon(painter = painterResource("icons/place_insertion_24px.xml"), null) }
    ),
    INSERT_VISITS(
        label = "Visits insertion",
        icon = { Icon(painter = painterResource("icons/visit_insertion_24px.xml"), null) }
    ),
    UPDATE_PATIENT(
        label = "Patients update fields",
        icon = { Icon(painter = painterResource("icons/update_patient_24px.xml"), null) }
    ),
    REMOVE_PATIENT(
        label = "Patients removal",
        icon = { Icon(painter = painterResource("icons/remove_patient_24px.xml"), null) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
fun mainWindow() = application {

    var viewing by remember { mutableStateOf(Views.INSERT_PATIENT) }
    var numberOfElements by remember { mutableIntStateOf(1_000) }

    Window(onCloseRequest = ::exitApplication, title = "Benchmark", icon = painterResource("icons/app_icon_24px.xml")) {
        Column(Modifier.fillMaxSize()) {

            when (viewing) {
                Views.INSERT_PATIENT -> {
                    HibernateDBEntityManager.dbConnection(DBEntityManager.ConnectionTypes.TEMPORARY_FILE)
                    SQLDBEntityManager.dbConnection(DBEntityManager.ConnectionTypes.TEMPORARY_FILE)

                    val hibernateResults = InsertPatientBenchmark.run(HibernateDBEntityManager, 1..numberOfElements)
                    val sqlResult = InsertPatientBenchmark.run(SQLDBEntityManager, 1..numberOfElements)

                    val viewer = BenchmarkExecutionTimeViewerImpl(viewing.label)
                    viewer.addGraph(hibernateResults, "Hibernate", SolidColor(Color.Red))
                    viewer.addGraph(sqlResult, "JDBC", SolidColor(Color.Blue))

                    viewer.view(Modifier.weight(1.0f))
                }
                Views.INSERT_PLACES -> {
                    HibernateDBEntityManager.dbConnection(DBEntityManager.ConnectionTypes.TEMPORARY_FILE)
                    SQLDBEntityManager.dbConnection(DBEntityManager.ConnectionTypes.TEMPORARY_FILE)

                    val hibernateResults = InsertPlaceBenchmark.run(HibernateDBEntityManager, 1..numberOfElements)
                    val sqlResult = InsertPlaceBenchmark.run(SQLDBEntityManager, 1..numberOfElements)

                    val viewer = BenchmarkExecutionTimeViewerImpl(viewing.label)
                    viewer.addGraph(hibernateResults, "Hibernate", SolidColor(Color.Red))
                    viewer.addGraph(sqlResult, "JDBC", SolidColor(Color.Blue))

                    viewer.view(Modifier.weight(1.0f))
                }
                Views.INSERT_VISITS -> {
                    HibernateDBEntityManager.dbConnection(DBEntityManager.ConnectionTypes.TEMPORARY_FILE)
                    SQLDBEntityManager.dbConnection(DBEntityManager.ConnectionTypes.TEMPORARY_FILE)

                    val hibernateResults = InsertVisitBenchmark.run(HibernateDBEntityManager, 1..numberOfElements)
                    val sqlResult = InsertVisitBenchmark.run(SQLDBEntityManager, 1..numberOfElements)

                    val viewer = BenchmarkExecutionTimeViewerImpl(viewing.label)
                    viewer.addGraph(hibernateResults, "Hibernate", SolidColor(Color.Red))
                    viewer.addGraph(sqlResult, "JDBC", SolidColor(Color.Blue))

                    viewer.view(Modifier.weight(1.0f))
                }
                Views.UPDATE_PATIENT -> {
                    HibernateDBEntityManager.dbConnection(DBEntityManager.ConnectionTypes.TEMPORARY_FILE)
                    SQLDBEntityManager.dbConnection(DBEntityManager.ConnectionTypes.TEMPORARY_FILE)

                    val hibernateResults = UpdatePatientBenchmark.run(HibernateDBEntityManager, 1..numberOfElements)
                    val sqlResult = UpdatePatientBenchmark.run(SQLDBEntityManager, 1..numberOfElements)

                    val viewer = BenchmarkExecutionTimeViewerImpl(viewing.label)
                    viewer.addGraph(hibernateResults, "Hibernate", SolidColor(Color.Red))
                    viewer.addGraph(sqlResult, "JDBC", SolidColor(Color.Blue))

                    viewer.view(Modifier.weight(1.0f))
                }
                Views.REMOVE_PATIENT -> {
                    HibernateDBEntityManager.dbConnection(DBEntityManager.ConnectionTypes.TEMPORARY_FILE)
                    SQLDBEntityManager.dbConnection(DBEntityManager.ConnectionTypes.TEMPORARY_FILE)

                    val hibernateResults = RemovePatientBenchmark.run(HibernateDBEntityManager, 1..numberOfElements)
                    val sqlResult = RemovePatientBenchmark.run(SQLDBEntityManager, 1..numberOfElements)

                    val viewer = BenchmarkExecutionTimeViewerImpl(viewing.label)
                    viewer.addGraph(hibernateResults, "Hibernate", SolidColor(Color.Red))
                    viewer.addGraph(sqlResult, "JDBC", SolidColor(Color.Blue))

                    viewer.view(Modifier.weight(1.0f))
                }
            }

            Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text(
                    "Number of elements:",
                    Modifier
                        .align(Alignment.CenterVertically)
                        .padding(end = 10.dp)
                )
                NumberOfElementsPicker(
                    numberOfElements,
                    onValueChange = { newNumberOfElements ->
                        numberOfElements = newNumberOfElements
                    }
                )
            }

            NavigationBar {
                Views.entries.forEach { entry ->
                    NavigationBarItem(
                        selected = viewing == entry,
                        label = { Text(entry.label) },
                        icon = entry.icon,
                        onClick = {
                            viewing = entry
                        }
                    )
                }
            }
        }
    }
}

fun main() {
    mainWindow()
}
