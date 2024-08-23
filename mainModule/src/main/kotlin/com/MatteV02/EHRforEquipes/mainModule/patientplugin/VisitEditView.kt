package com.MatteV02.EHRforEquipes.mainModule.patientplugin

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.MatteV02.EHRforEquipes.commonModule.dbManagement.DBEntityManager
import com.MatteV02.EHRforEquipes.commonModule.entities.patient.Patient
import com.MatteV02.EHRforEquipes.commonModule.entities.visit.Exam
import com.MatteV02.EHRforEquipes.commonModule.entities.visit.Visit
import com.MatteV02.EHRforEquipes.mainModule.patientplugin.PatientPlugin.dbEntityManager
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

private val bundle = ResourceBundle.getBundle("visitEditView", Locale.getDefault())

@Composable
fun VisitEditView(visit: Visit, modifier: Modifier = Modifier) {

    var selected by remember { mutableStateOf(VisitUIMenu.GENERALITIES) }

    Row(modifier) {
        NavigationRail(modifier.defaultMinSize()) {
            VisitUIMenu.entries.forEach { menuEntry ->
                NavigationRailItem(
                    selected = selected == menuEntry,
                    onClick = {
                        selected = menuEntry
                    },
                    label = { Text(menuEntry.label) },
                    icon = { Icon(menuEntry.icon, contentDescription = null) }
                )
            }
        }
        selected.view(visit, modifier.fillMaxWidth())
    }
}

private enum class VisitUIMenu(
    val icon: ImageVector,
    var label: String,
    val view: @Composable (visit: Visit, modifier: Modifier) -> Unit
) {
    GENERALITIES(
        Icons.AutoMirrored.Filled.Assignment,
        label = bundle.getString("VisitUIMenu.generalities"),
        view = { visit, modifier ->
            Column(modifier) {
                Row {
                    datePicker(
                        visit, Modifier
                            .align(Alignment.CenterVertically)
                            .weight(0.5f)
                    )
                    placeDropDownMenu(visit, bundle.getString("placeDropDownMenu"),
                        Modifier
                            .weight(1.0f)
                    )
                }

                patientDropDownMenu(visit, bundle.getString("patientDropDownMenu"), modifier)

                Row {
                    specialistDropDownMenu(visit, bundle.getString("specialistDropDownMenu"),
                        Modifier
                            .weight(1.0f)
                            .padding(end = 10.dp)
                    )

                    val typeLabels = mutableMapOf<Visit.Companion.VisitType, String>()
                    Visit.Companion.VisitType.entries.forEach { type ->
                        typeLabels[type] = bundle.getString("typeDropDownMenu.${type.name}")
                    }

                    typeDropDownMenu(visit, bundle.getString("typeDropDownMenu"), typeLabels,
                        Modifier
                            .weight(1.0f)
                    )
                }
            }
        }
    ),
    ANAMNESIS(
        Icons.Default.Inventory,
        label = bundle.getString("VisitUIMenu.anamnesis"),
        view = { visit, modifier ->
            Column(modifier) {
                anamnesisTextField(visit, bundle.getString("anamnesisTextField"))
                objectiveExamTextField(visit, bundle.getString("objectiveExamTextField"))
                examView(visit)
            }
        })
    ,
    CONCLUSIONS(
        Icons.Default.HistoryEdu,
        label = bundle.getString("VisitUIMenu.conclusions"),
        view = { visit, modifier ->
            Column(modifier) {
                indicationsTextField(visit, bundle.getString("indicationsTextField"))
                nextStepsTextField(visit, bundle.getString("nextStepsTextField"))
                letterTextTextField(visit, bundle.getString("letterTextTextField"),
                    Modifier
                        .fillMaxHeight()
                )
            }
        }
    );
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun datePicker(visit: Visit, modifier: Modifier = Modifier) {
    var date by mutableStateOf(visit.date)
    var openDialog by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    OutlinedTextField(
        value = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
        onValueChange = {},
        trailingIcon = {
            IconButton(
                onClick = {
                    openDialog = true
                }
            ) {
                Icon(Icons.Default.CalendarMonth, contentDescription = "Select date of visit")
            }
        },
        readOnly = true,
        modifier = modifier
            .padding(horizontal = 10.dp)
    )

    if (openDialog) {
        DatePickerDialog(
            onDismissRequest = {
                openDialog = false
            },
            confirmButton = {
                Button(
                    onClick = {
                        openDialog = false
                        date = datePickerState.selectedDateMillis?.let {
                            Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                        } ?: LocalDate.now()
                        visit.date = date
                        dbEntityManager.update(visit)
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


@Composable
fun placeDropDownMenu(visit: Visit, label: String, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }

    val places = dbEntityManager.getPlaces()

    Box(modifier) {
        TextField(
            value = visit.place?.name ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                IconButton(
                    onClick = {
                        expanded = !expanded
                    },
                ) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
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
                        dbEntityManager.update(visit)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun patientDropDownMenu(visit: Visit, label : String, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    var patient by mutableStateOf(visit.patient)
    var name by remember { mutableStateOf(patient?.name ?: "") }

    val patients = dbEntityManager.getPatients()

    Column(modifier.padding(10.dp)) {
        Row {
            Box {
                TextField(
                    value = name,
                    onValueChange = { newName ->
                        val newPatient = patients.firstOrNull { p ->
                            p.name.startsWith(newName)
                        }
                        name = newPatient?.name ?: newName

                        visit.patient = newPatient
                        dbEntityManager.update(visit)
                    },
                    label = { Text(label) },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                expanded = !expanded
                            },
                        ) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
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
                                dbEntityManager.update(visit)
                                expanded = false
                            }
                        )
                    }
                }
            }

            IconButton(
                onClick = {
                    patient = Patient()
                    dbEntityManager.insert(patient!!)
                    visit.patient = patient
                    dbEntityManager.update(visit)
                },
                modifier = Modifier
                    .align(Alignment.CenterVertically)
            ) {
                Icon(Icons.Default.AddCircle, contentDescription = null)
            }
        }

        PatientEditView(patient ?: Patient(), enabled = patient != null)
    }
}


@Composable
fun specialistDropDownMenu(visit: Visit, label: String, modifier: Modifier = Modifier) {
    var specialist by mutableStateOf(visit.specialist)
    var expanded by remember { mutableStateOf(false) }

    val specialists = dbEntityManager.getSpecialists()

    Box(modifier = modifier) {
        TextField(
            value = specialist,
            onValueChange = { newSpecialist ->
                specialist = newSpecialist
                visit.specialist = specialist
                dbEntityManager.update(visit)
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
            modifier = Modifier.fillMaxWidth()
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
                        dbEntityManager.update(visit)
                    }
                )
            }
        }
    }
}

private fun DBEntityManager.getSpecialists(): List<String> {
    return getVisits().map { it.specialist }
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
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth()
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
                        dbEntityManager.update(visit)
                    }
                )
            }
        }
    }
}


@Composable
fun anamnesisTextField(visit: Visit, label: String, modifier: Modifier = Modifier) {
    var anamnesis by remember { mutableStateOf(visit.anamnesis) }
    TextField(
        value = anamnesis,
        label = { Text(label) },
        onValueChange = { newAnamnesis ->
            anamnesis = newAnamnesis
            visit.anamnesis = newAnamnesis
            dbEntityManager.update(visit)
        },
        modifier = modifier
    )
}

@Preview @Composable
fun anamnesisTextFieldPreview() {
    anamnesisTextField(
        Visit(anamnesis = "test"),
        label = "anamnesis"
    )
}


@Composable
fun objectiveExamTextField(visit: Visit, label: String, modifier: Modifier = Modifier) {
    var objectiveExam by remember { mutableStateOf(visit.objectiveExam) }

    TextField(
        value = objectiveExam,
        onValueChange = { newObjectiveExam ->
            objectiveExam = newObjectiveExam
            visit.objectiveExam = objectiveExam
            dbEntityManager.update(visit)
        },
        label = { Text(label) },
        modifier = modifier
    )
}

@Preview @Composable
fun objectiveExamTextFieldPreview() {
    objectiveExamTextField(
        visit = Visit(objectiveExam = "test"),
        label = "Objective Exam"
    )
}


@Composable
fun examView(visit: Visit, modifier: Modifier = Modifier) {
    val exams = mutableStateListOf<Exam>()

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    val newExam = Exam()
                    exams.add(newExam)
                    visit.exams.add(newExam)
                    dbEntityManager.update(visit)
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Text(bundle.getString("addExam"))
            }
        },
        modifier = modifier
    ) {
        LazyColumn {
            items(exams) { exam ->
                examRow(visit, exam)
            }
        }
    }
}

@Composable
fun examRow(visit : Visit, exam: Exam, modifier: Modifier = Modifier) {
    Row {
        examDatePicker(visit, exam, modifier)
        examTypeDropDownMenu(visit, exam, modifier)
        examDescriptionTextField(visit, exam, modifier)
        examDiagnosisTextField(visit, exam, modifier)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun examDatePicker(visit: Visit, exam: Exam, modifier: Modifier = Modifier) {
    var date by mutableStateOf(exam.date)
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
        Icon(Icons.Default.CalendarMonth, contentDescription = null)
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
                        exam.date = date
                        dbEntityManager.update(visit)
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


@Composable
fun examTypeDropDownMenu(visit: Visit, exam: Exam, modifier: Modifier = Modifier) {
    var type by remember { mutableStateOf(exam.type) }
    var expanded by remember { mutableStateOf(false) }

    Box(modifier) {
        TextField(
            value = type.toString(),
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }
                ) {
                    Icon(
                        if (expanded) {
                            Icons.Default.ArrowDropDown
                        } else {
                            Icons.Default.ArrowDropUp
                        },
                        contentDescription = null
                    )
                }
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            Exam.Companion.ExamType.entries.forEach { entry ->
                DropdownMenuItem(
                    text = { Text(entry.toString()) },
                    onClick = {
                        type = entry
                        exam.type = entry
                        dbEntityManager.update(visit)
                    }
                )
            }
        }
    }
}

@Composable
fun examDescriptionTextField(visit: Visit, exam: Exam, modifier: Modifier = Modifier) {
    var description by remember { mutableStateOf(exam.description) }

    TextField(
        value = description,
        onValueChange = { newDescription ->
            description = newDescription
            exam.description = newDescription
            dbEntityManager.update(visit)
        },
        modifier = modifier
    )
}

@Composable
fun examDiagnosisTextField(visit: Visit, exam: Exam, modifier: Modifier = Modifier) {
    var diagnosis by remember { mutableStateOf(exam.diagnosis) }

    TextField(
        value = diagnosis,
        onValueChange = { newDiagnosis ->
            diagnosis = newDiagnosis
            exam.diagnosis = newDiagnosis
            dbEntityManager.update(visit)
        },
        modifier = modifier
    )
}


@Composable
fun indicationsTextField(visit: Visit, label: String, modifier: Modifier = Modifier) {
    var indications by remember { mutableStateOf(visit.indications) }

    TextField(
        value = indications,
        onValueChange = { newIndications ->
            indications = newIndications
            visit.indications = newIndications
            dbEntityManager.update(visit)
        },
        label = { Text(label) },
        modifier = modifier
    )
}

@Composable
fun nextStepsTextField(visit: Visit, label: String, modifier: Modifier = Modifier) {
    var nextSteps by remember { mutableStateOf(visit.nextSteps) }

    TextField(
        value = nextSteps,
        onValueChange = { newNextSteps ->
            nextSteps = newNextSteps
            visit.nextSteps = newNextSteps
            dbEntityManager.update(visit)
        },
        label = { Text(label) },
        modifier = modifier
    )
}

@Composable
fun letterTextTextField(visit: Visit, label: String, modifier: Modifier = Modifier) {
    var letterText by remember { mutableStateOf(visit.letterText) }

    TextField(
        value = letterText,
        onValueChange = { newLetterText ->
            letterText = newLetterText
            visit.letterText = newLetterText
            dbEntityManager.update(visit)
        },
        label = { Text(label) },
        modifier = modifier
    )
}