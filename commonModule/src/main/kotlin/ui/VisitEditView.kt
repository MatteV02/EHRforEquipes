package ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
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
import dbManagement.DBEntityManager
import entities.Patient
import entities.Visit
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

private val bundle = ResourceBundle.getBundle("visitEditView", Locale.getDefault())

enum class VisitUIMenu(
    val icon: ImageVector,
    var label: String,
    val view: @Composable (visit: Visit, modifier: Modifier) -> Unit
) {
    GENERALITIES(
        Icons.AutoMirrored.Filled.Assignment,
        label = bundle.getString("VisitUIMenu.generalities"),
        view = { visit, modifier ->
            Column {
                Row {
                    datePicker(visit, bundle.getString("datePicker"),
                        modifier
                            .align(Alignment.CenterVertically)
                            .weight(0.5f)
                    )
                    placeDropDownMenu(visit, bundle.getString("placeDropDownMenu"),
                        modifier
                            .weight(1.0f)
                    )
                }

                patientDropDownMenu(visit, bundle.getString("patientDropDownMenu"), modifier)

                Row {
                    specialistDropDownMenu(visit, bundle.getString("specialistDropDownMenu"),
                        modifier
                            .weight(1.0f)
                            .padding(end = 10.dp)
                    )

                    val typeLabels = mutableMapOf<Visit.Companion.VisitType, String>()
                    entities.Visit.Companion.VisitType.getTypes().forEach { type ->
                        typeLabels[type] = bundle.getString("typeDropDownMenu.${type}")
                    }

                    typeDropDownMenu(visit, bundle.getString("typeDropDownMenu"), typeLabels,
                        modifier
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
            Column {
                anamnesisTextField(visit, bundle.getString("anamnesisTextField"), modifier)
                objectiveExamTextField(visit, bundle.getString("objectiveExamTextField"), modifier)
                examView(visit, modifier)
            }
        })
    ,
    CONCLUSIONS(
        Icons.Default.HistoryEdu,
        label = bundle.getString("VisitUIMenu.conclusions"),
        view = { visit, modifier ->
            Column {
                indicationsTextField(visit, bundle.getString("indicationsTextField"), modifier)
                nextStepsTextField(visit, bundle.getString("nextStepsTextField"), modifier)
                letterTextTextField(visit, bundle.getString("letterTextTextField"),
                    modifier
                        .fillMaxHeight()
                )
            }
        }
    );
}

@Composable
fun visitEditView(visit: Visit, modifier: Modifier = Modifier) {

    var selected by remember { mutableStateOf(VisitUIMenu.GENERALITIES) }

    Row {
        NavigationRail(modifier) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun datePicker(visit: Visit, label: String, modifier: Modifier = Modifier) {
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
        Text("$label : $date", modifier.padding(end = 10.dp))
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


@Composable
fun placeDropDownMenu(visit: Visit, label: String, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }

    val places = DBEntityManager.getPlaces()

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
fun patientDropDownMenu(visit: Visit, label : String, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    var patient by mutableStateOf(visit.patient)
    var name by remember { mutableStateOf(patient?.name ?: "") }

    val patients = DBEntityManager.getPatients()

    Column(modifier.padding(10.dp)) {
        Row {
            Box(modifier.weight(1.0f)) {
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
                    label = { Text(label) },
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
                modifier = modifier
                    .align(Alignment.CenterVertically)
                    .weight(0.1f)
            ) {
                Icon(Icons.Default.AddCircle, contentDescription = null)
            }
        }

        patientEditView(patient ?: Patient(), enabled = patient != null)
    }
}


@Composable
fun specialistDropDownMenu(visit: Visit, label: String, modifier: Modifier = Modifier) {
    var specialist by mutableStateOf(visit.specialist)
    var expanded by remember { mutableStateOf(false) }

    val specialists = DBEntityManager.getSpecialists()

    Box(modifier = modifier) {
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
                        DBEntityManager.update(visit)
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
            DBEntityManager.update(visit)
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
            DBEntityManager.update(visit)
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
fun examView(visit: Visit, modifier: Modifier) {
    val exams = mutableStateListOf<Visit.Companion.Exam>()

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    val newExam = Visit.Companion.Exam()
                    exams.add(newExam)
                    visit.exams.add(newExam)
                    DBEntityManager.update(visit)
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Text(bundle.getString("addExam"))
            }
        }
    ) {
        LazyColumn {
            items(exams) { exam ->
                examRow(visit, exam, modifier)
            }
        }
    }
}

@Composable
fun examRow(visit : Visit, exam: Visit.Companion.Exam, modifier: Modifier) {
    Row {
        examDatePicker(visit, exam, modifier)
        examTypeDropDownMenu(visit, exam, modifier)
        examDescriptionTextField(visit, exam, modifier)
        examDiagnosisTextField(visit, exam, modifier)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun examDatePicker(visit: Visit, exam: Visit.Companion.Exam, modifier: Modifier = Modifier) {
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


@Composable
fun examTypeDropDownMenu(visit: Visit, exam: Visit.Companion.Exam, modifier: Modifier = Modifier) {
    var type by remember { mutableStateOf(exam.type) }
    var expanded by remember { mutableStateOf(false) }

    Box {
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
            },
            modifier = modifier
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = modifier
        ) {
            Visit.Companion.Exam.Companion.ExamType.entries.forEach { entry ->
                DropdownMenuItem(
                    text = { Text(entry.toString()) },
                    onClick = {
                        type = entry
                        exam.type = entry
                        DBEntityManager.update(visit)
                    },
                    modifier = modifier
                )
            }
        }
    }
}

@Composable
fun examDescriptionTextField(visit: Visit, exam: Visit.Companion.Exam, modifier: Modifier = Modifier) {
    var description by remember { mutableStateOf(exam.description) }

    TextField(
        value = description,
        onValueChange = { newDescription ->
            description = newDescription
            exam.description = newDescription
            DBEntityManager.update(visit)
        },
        modifier = modifier
    )
}

@Composable
fun examDiagnosisTextField(visit: Visit, exam: Visit.Companion.Exam, modifier: Modifier = Modifier) {
    var diagnosis by remember { mutableStateOf(exam.diagnosis) }

    TextField(
        value = diagnosis,
        onValueChange = { newDiagnosis ->
            diagnosis = newDiagnosis
            exam.diagnosis = newDiagnosis
            DBEntityManager.update(visit)
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
            DBEntityManager.update(visit)
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
            DBEntityManager.update(visit)
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
            DBEntityManager.update(visit)
        },
        label = { Text(label) },
        modifier = modifier
    )
}