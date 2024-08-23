package com.MatteV02.EHRforEquipes.mainModule.patientplugin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.MatteV02.EHRforEquipes.commonModule.entities.patient.Patient
import com.MatteV02.EHRforEquipes.mainModule.patientplugin.PatientPlugin.dbEntityManager
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

private val bundle = ResourceBundle.getBundle("PatientRow", Locale.getDefault())

@Composable
fun PatientRow(selected: Boolean, patient: Patient, onDelete: () -> Unit, modifier: Modifier = Modifier) {
    Row(modifier
        .fillMaxWidth()
        .background(if(selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.background)
        .padding(10.dp)
    ) {
        NameTextField(patient, bundle.getString("NameTextField"), Modifier.weight(1.0f).align(Alignment.CenterVertically).padding(end = 10.dp))
        BirthDateSelector(patient, bundle.getString("BirthDateSelector"), Modifier.align(Alignment.CenterVertically).padding(end = 10.dp))
        ResidenceTextField(patient, bundle.getString("ResidenceTextField"), Modifier.weight(1.0f).align(Alignment.CenterVertically))
        IconButton(
            onClick = onDelete,
            modifier = Modifier.padding(start = 20.dp).align(Alignment.CenterVertically)
        ) {
            Icon(Icons.Default.Delete, null)
        }
    }
}

@Composable
private fun NameTextField(patient: Patient, label: String = "", modifier: Modifier = Modifier) {
    var name by mutableStateOf(patient.name)
    TextField(
        value = name,
        onValueChange = { newName ->
            name = newName
            patient.name = newName
            dbEntityManager.update(patient)
        },
        label = { Text(label) },
        modifier = modifier
    )
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BirthDateSelector(patient: Patient, label: String = "", modifier: Modifier = Modifier) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    var selectedDate = patient.dateOfBirth


    OutlinedTextField(
        value = selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
        onValueChange = { },
        label = { Text(label) },
        readOnly = true,
        trailingIcon = {
            IconButton(onClick = { showDatePicker = !showDatePicker }) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Select date"
                )
            }
        },
        modifier = modifier
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                Button(
                    onClick = {
                        showDatePicker = false
                        selectedDate = datePickerState.selectedDateMillis?.let {
                            convertMillisToLocalDate(it)
                        } ?: selectedDate
                        patient.dateOfBirth = selectedDate
                        dbEntityManager.update(patient)
                    }
                ) {
                    Text("OK")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                showModeToggle = false,
            )
        }
    }
}

private fun convertMillisToLocalDate(millis: Long): LocalDate {
    return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
}


@Composable
private fun ResidenceTextField(patient: Patient, label: String, modifier: Modifier = Modifier) {
    var residence by mutableStateOf(patient.residence)

    TextField(
        value = residence,
        onValueChange = { newResidence ->
            residence = newResidence
            patient.residence = newResidence
            dbEntityManager.update(patient)
        },
        label = { Text(label) },
        modifier = modifier
    )
}