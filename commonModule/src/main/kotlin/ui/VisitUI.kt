package ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dbManagement.DBEntityManager
import entities.Patient
import entities.Visit
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Locale
import java.util.ResourceBundle

enum class VisitUIMenu {
    GENERALITIES, ANAMNESIS, CONCLUSIONS
}

@Composable
fun visitEditView(visit: Visit, locale: Locale, modifier: Modifier = Modifier) {

    val labels = ResourceBundle.getBundle("visitEditView", locale)

    var view by remember { mutableStateOf(VisitUIMenu.GENERALITIES) }

    Column {
        Row {
            ElevatedButton(
                onClick = {
                    view = VisitUIMenu.GENERALITIES
                }
            ) {
                Text(labels.getString("VisitUIMenu.generalities"))
            }
            ElevatedButton(
                onClick = {
                    view = VisitUIMenu.ANAMNESIS
                }
            ) {
                Text(labels.getString("VisitUIMenu.anamnesis"))
            }
            ElevatedButton(
                onClick = {
                    view = VisitUIMenu.CONCLUSIONS
                }
            ) {
                Text(labels.getString("VisitUIMenu.conclusions"))
            }
        }
        when(view) {
            VisitUIMenu.GENERALITIES -> Column {
                Row {
                    datePicker(visit, modifier)
                    placeDropDownMenu(visit, modifier)
                }

                patientDropDownMenu(visit, locale, modifier)

                Row {
                    specialistDropDownMenu(visit, labels.getString("specialistDropDownMenu"), modifier)

                    val typeLabels = mutableMapOf<Visit.Companion.VisitType, String>()
                    Visit.Companion.VisitType.getTypes().forEach { type ->
                        typeLabels[type] = labels.getString("typeDropDownMenu.${type}")
                    }

                    typeDropDownMenu(visit, labels.getString("typeDropDownMenu"), typeLabels, modifier)
                }

            }
            VisitUIMenu.ANAMNESIS -> Column {

            }
            VisitUIMenu.CONCLUSIONS -> Column {

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun datePicker(visit: Visit, modifier: Modifier = Modifier) {
    var date by mutableStateOf(visit.date)
    var openDialog by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(initialDisplayMode = DisplayMode.Input)

    TextButton(
        onClick = {
            openDialog = true
        },
        modifier = modifier
            .padding(horizontal = 10.dp)
    ) {
        Text(date.toString(), modifier.padding(end = 10.dp))
        Icon(Icons.Default.CalendarMonth, contentDescription = "Select date of visit")
    }

    if (openDialog) {
        DatePickerDialog(
            onDismissRequest = {
                openDialog = false
            }, confirmButton = {
                Button(
                    onClick = {
                        openDialog = false
                        date = datePickerState.selectedDateMillis?.let {
                            Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                        } ?: LocalDate.now()
                        visit.date = date
                        DBEntityManager.update(visit)
                    }
                ) {
                    Text("OK")
                }
            }
        ) {
            DatePicker(datePickerState)
        }
    }
}

@Preview
@Composable
fun datePickerPreview() {
    datePicker(
        visit = Visit(date = LocalDate.now())
    )
}


@Composable
fun placeDropDownMenu(visit: Visit, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }

    val places = DBEntityManager.getPlaces()

    Box(modifier) {
        TextField(
            value = visit.place?.name ?: "",
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                IconButton(
                    onClick = {
                        expanded = !expanded
                    },
                ) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
            },
            modifier = modifier.clickable {
                expanded = !expanded
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            places.forEach { place ->
                DropdownMenuItem(
                    text = {
                        Text(place.name)
                    },
                    onClick = {
                        visit.place = place
                        DBEntityManager.update(visit)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun patientDropDownMenu(visit: Visit, locale: Locale, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    var patient by mutableStateOf(visit.patient)
    var name by remember { mutableStateOf(patient?.name ?: "") }

    val patients = DBEntityManager.getPatients()

    Column {
        Row {
            Box(modifier) {
                TextField(
                    value = name,
                    onValueChange = { newName ->
                        val newPatient = patients.firstOrNull { p ->
                            p.name.startsWith(newName)
                        }
                        name = newPatient?.name ?: newName

                        visit.patient = newPatient
                        DBEntityManager.update(visit)
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                expanded = !expanded
                            },
                        ) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    },
                    modifier = modifier.clickable {
                        expanded = !expanded
                    }
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {
                        expanded = false
                    }
                ) {
                    patients.forEach { patient ->
                        DropdownMenuItem(
                            text = {
                                Text(patient.name)
                            },
                            onClick = {
                                visit.patient = patient
                                DBEntityManager.update(visit)
                                expanded = false
                            }
                        )
                    }
                }
            }

            IconButton(
                onClick = {
                    patient = Patient()
                    DBEntityManager.insert(patient!!)
                    visit.patient = patient
                    DBEntityManager.update(visit)
                },
                modifier = modifier.align(Alignment.CenterVertically)
            ) {
                Icon(Icons.Default.AddCircle, contentDescription = null)
            }
        }

        patientEditView(patient ?: Patient(), enabled = patient != null, locale = locale, modifier = modifier)
    }
}


@Composable
fun specialistDropDownMenu(visit: Visit, label: String, modifier: Modifier = Modifier) {
    var specialist by mutableStateOf(visit.specialist)
    var expanded by remember { mutableStateOf(false) }

    val specialists = DBEntityManager.getSpecialists()

    Box(modifier) {
        TextField(
            value = specialist,
            onValueChange = { newSpecialist ->
                specialist = newSpecialist
                visit.specialist = specialist
                DBEntityManager.update(visit)
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        expanded = !expanded
                    }
                ) {
                    if (!expanded) {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    } else {
                        Icon(Icons.Default.ArrowDropUp, contentDescription = null)
                    }
                }
            },
            label = {
                Text(label)
            },
            modifier = modifier
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            specialists.forEach { s ->
                DropdownMenuItem(
                    text = {
                        Text(s)
                    },
                    onClick = {
                        expanded = false
                        specialist = s
                        visit.specialist = s
                        DBEntityManager.update(visit)
                    }
                )
            }
        }
    }
}

@Composable @Preview
fun specialistDropDownMenuPreview() {
    specialistDropDownMenu(
        visit = Visit(specialist = "Veroni Andrea"),
        label = "Specialist"
    )
}


@Composable
fun typeDropDownMenu(visit: Visit, label: String, dropDownEntries: Map<Visit.Companion.VisitType, String>, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }

    var type by mutableStateOf(visit.type)

    Box(modifier) {
        TextField(
            value = dropDownEntries[type] ?: "",
            readOnly = true,
            onValueChange = {},
            trailingIcon = {
                IconButton(
                    onClick = {
                        expanded = !expanded
                    }
                ) {
                    Icon(
                        if (!expanded) { Icons.Default.ArrowDropDown }
                        else { Icons.Default.ArrowDropUp },
                        contentDescription = null
                    )
                }
            },
            label = { Text(label) }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            dropDownEntries.forEach { (newType, label) ->
                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = {
                        expanded = false
                        type = newType
                        visit.type = newType
                        DBEntityManager.update(visit)
                    }
                )
            }
        }
    }
}