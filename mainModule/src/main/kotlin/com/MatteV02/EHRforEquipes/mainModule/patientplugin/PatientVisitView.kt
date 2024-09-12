package com.MatteV02.EHRforEquipes.mainModule.patientplugin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.MatteV02.EHRforEquipes.commonModule.entities.patient.Patient
import com.MatteV02.EHRforEquipes.commonModule.entities.visit.Visit
import java.time.format.DateTimeFormatter
import java.util.ResourceBundle


private val bundle = ResourceBundle.getBundle("PatientVisitView")

@Composable
fun PatientVisitView(patient: Patient, visits: List<Visit>, modifier: Modifier) {
    var selectedVisit by remember { mutableStateOf<Visit?>(null) }
    val visitToShow = visits.filter { visit -> visit.patient == patient }

    if (selectedVisit == null) {
        Column(modifier) {
            PatientVisitViewHeading(patient.name, Modifier.fillMaxWidth())
            if (visitToShow.isNotEmpty()) {
                LazyColumn {
                    items(visitToShow) { visit ->
                        VisitRow(
                            visit,
                            Modifier
                                .fillMaxWidth()
                                .clickable(
                                    onClick = {
                                        selectedVisit = visit
                                    }
                                )
                        )
                    }
                }
            } else {
                Text(bundle.getString("nothingToShow"), Modifier.align(Alignment.CenterHorizontally))
            }
        }
    } else {
        selectedVisit?.let { v ->
            Column {
                VisitEditView(v, modifier = Modifier.padding(10.dp))
                FloatingActionButton(
                    onClick = {
                        selectedVisit = null
                    }
                ) {
                    Icon(Icons.Default.Close, null)
                }
            }
        }
    }
}

@Composable
private fun PatientVisitViewHeading(name : String, modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(
            bundle.getString("PatientVisitViewHeading"),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Text(
            name,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VisitRow(visit: Visit, modifier: Modifier = Modifier) {
    Row(modifier) {
        TextField(
            value = visit.date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            onValueChange = {},
            readOnly = true,
            enabled = false,
            modifier = modifier.weight(1.0f).padding(end = 10.dp),
            colors = TextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        )
        TextField(
            value = visit.specialist,
            onValueChange = {},
            readOnly = true,
            enabled = false,
            modifier = modifier.weight(1.0f),
            colors = TextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant)
        )
    }
}