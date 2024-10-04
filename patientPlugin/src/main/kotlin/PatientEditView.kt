import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.EHRforEquipes.patientPlugin.PluginImpl.Companion.dbEntityManager
import com.MatteV02.EHRforEquipes.commonModule.entities.patient.Patient
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.*


private val bundle = ResourceBundle.getBundle("patientEditView", Locale.getDefault())

@Composable
fun PatientEditView(patient: Patient, enabled: Boolean = true, modifier: Modifier = Modifier) {

    Column(modifier
        .padding(10.dp)
    ) {
        Row(modifier
            .padding(bottom = 5.dp)
        ) {
            nameTextField(patient, label = bundle.getString("nameTextField"), enabled, modifier.weight(1.0f))
            genderPicker(patient, bundle.getString("genderPickerMale"), bundle.getString("genderPickerFemale"), enabled, modifier.padding(horizontal = 5.dp))
        }
        Row(modifier
            .padding(bottom = 5.dp)
        ) {
            dateOfBirthPicker(patient, enabled, modifier.align(Alignment.CenterVertically))
            placeOfBirthTextField(patient, bundle.getString("placeOfBirthTextField"), enabled, modifier.weight(1.0f))
        }
        Row(modifier
            .padding(bottom = 5.dp)
        ) {
            residenceTextField(patient, bundle.getString("residenceTextField"), enabled, modifier.weight(1.0f).padding(end = 5.dp))
            FCTextField(patient, bundle.getString("FCTextField"), enabled, modifier.weight(1.0f))
        }
        Row(modifier
            .padding(bottom = 5.dp)
        ) {
            phoneNumberTextField(patient, bundle.getString("phoneNumberTextField"), enabled, modifier.weight(1.0f).padding(end = 5.dp))
            landlinePhoneNumberTextField(patient, bundle.getString("landlinePhoneNumberTextField"), enabled, modifier.weight(1.0f))
        }
        Row(modifier) {
            mailTextField(patient, bundle.getString("mailTextField"), enabled, modifier.weight(1.0f).padding(end = 5.dp))
            doctorTextField(patient, bundle.getString("doctorTextField"), enabled, modifier.weight(1.0f))
        }
    }
}




@Composable
fun nameTextField(patient: Patient, label : String, enabled: Boolean = true, modifier: Modifier = Modifier) {
    var name by mutableStateOf(patient.name)
    TextField(
        value = name,
        onValueChange = { newName ->
            name = newName
            patient.name = newName
            dbEntityManager.update(patient)
        },
        label = {
            Text(label)
        },
        enabled = enabled,
        modifier = modifier
    )
}

@Preview @Composable
fun nameTextFieldPreview() {
    nameTextField(
        Patient(name = "Veroni Matteo"),
        label = "Name"
    )
}


@Composable
fun genderPicker(patient: Patient, maleLabel : String, femaleLabel : String, enabled: Boolean = true, modifier: Modifier = Modifier) {
    var isMale by mutableStateOf(patient.gender == Patient.Companion.Gender.MALE)
    Row(modifier) {
        Row(modifier.padding(end = 5.dp)) {
            RadioButton(
                isMale,
                onClick = {
                    isMale = true
                    patient.gender = Patient.Companion.Gender.MALE
                    dbEntityManager.update(patient)
                },
                enabled = enabled
            )
            Text(maleLabel, modifier.align(Alignment.CenterVertically))
        }
        Row(modifier.padding(start = 5.dp)) {
            RadioButton(
                !isMale,
                onClick = {
                    isMale = false
                    patient.gender = Patient.Companion.Gender.FEMALE
                    dbEntityManager.update(patient)
                },
                enabled = enabled
            )
            Text(femaleLabel, modifier.align(Alignment.CenterVertically))
        }
    }
}

@Composable @Preview
fun genderPickerPreview() {
    genderPicker(
        Patient(
            gender = Patient.Companion.Gender.MALE
        ),
        maleLabel = "M",
        femaleLabel = "F"
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun dateOfBirthPicker(patient: Patient, enabled: Boolean = true, modifier: Modifier = Modifier) {
    var dateOfBirth by mutableStateOf(patient.dateOfBirth)
    var openDialog by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(initialDisplayMode = DisplayMode.Input)

    TextButton(
        onClick = {
            openDialog = true
        },
        enabled = enabled,
        modifier = modifier
            .padding(horizontal = 10.dp)
    ) {
        Text(dateOfBirth.toString(), modifier.padding(end = 10.dp))
        Icon(Icons.Default.CalendarMonth, contentDescription = "Select date of birth")
    }

    if (openDialog) {
        DatePickerDialog(
            onDismissRequest = {
                openDialog = false
            }, confirmButton = {
                Button(
                    onClick = {
                        openDialog = false
                        dateOfBirth = datePickerState.selectedDateMillis?.let {
                            Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                        } ?: LocalDate.now()
                        patient.dateOfBirth = dateOfBirth
                        dbEntityManager.update(patient)
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

@Preview @Composable
fun dateOfBirthPickerPreview() {
    dateOfBirthPicker(
        patient = Patient(dateOfBirth = LocalDate.parse("2002-11-23"))
    )
}


@Composable
fun placeOfBirthTextField(patient: Patient, label: String, enabled: Boolean = true, modifier: Modifier = Modifier) {
    var placeOfBirth by mutableStateOf(patient.placeOfBirth)

    TextField(
        placeOfBirth,
        onValueChange = { newPlaceOfBirth ->
            placeOfBirth = newPlaceOfBirth
            patient.placeOfBirth = newPlaceOfBirth
            dbEntityManager.update(patient)
        },
        label = {
            Text(label)
        },
        enabled = enabled,
        modifier = modifier
    )
}

@Preview @Composable
fun placeOfBirthTextFieldPreview() {
    placeOfBirthTextField(
        patient = Patient(placeOfBirth = "Modena, Italy"),
        label = "Place of birth"
    )
}


@Composable
fun residenceTextField(patient: Patient, label: String, enabled: Boolean = true, modifier: Modifier = Modifier) {
    var residence by mutableStateOf(patient.residence)

    TextField(
        value = residence,
        onValueChange = { newResidence ->
            residence = newResidence
            patient.residence = newResidence
            dbEntityManager.update(patient)
        },
        label = {
            Text(label)
        },
        enabled = enabled,
        modifier = modifier
    )
}

@Composable @Preview
fun residenceTextFieldPreview() {
    residenceTextField(
        patient = Patient(residence = "via Cremaschi, 13, Carpi (MO)"),
        label = "Residence"
    )
}


@Composable
fun FCTextField(patient: Patient, label: String, enabled: Boolean = true, modifier: Modifier = Modifier) {
    var FC by mutableStateOf(patient.FC)

    TextField(
        value = FC,
        onValueChange = { newFC ->
            FC = newFC
            patient.FC = newFC
            dbEntityManager.update(patient)
        },
        label = {
            Text(label)
        },
        enabled = enabled,
        modifier = modifier
    )
}

@Composable @Preview
fun FCTextFieldPreview() {
    FCTextField(
        patient = Patient(FC = "eijhf876483476"),
        label = "FC"
    )
}


@Composable
fun phoneNumberTextField(patient: Patient, label: String, enabled: Boolean = true, modifier: Modifier = Modifier) {
    var phoneNumber by mutableStateOf(patient.phoneNumber)

    TextField(
        value = phoneNumber,
        onValueChange = { newPhoneNumber ->
            phoneNumber = newPhoneNumber
            patient.phoneNumber = newPhoneNumber
            dbEntityManager.update(patient)
        },
        label = {
            Text(label)
        },
        enabled = enabled,
        modifier = modifier
    )
}

@Composable @Preview
fun phoneNumberTextFieldPreview() {
    phoneNumberTextField(
        patient = Patient(phoneNumber = "125478963"),
        label = "Phone Number"
    )
}


@Composable
fun landlinePhoneNumberTextField(patient: Patient, label: String, enabled: Boolean = true, modifier: Modifier = Modifier) {
    var landlinePhoneNumber by mutableStateOf(patient.landlinePhoneNumber)

    TextField(
        value = landlinePhoneNumber,
        onValueChange = { newLandlinePhoneNumber ->
            landlinePhoneNumber = newLandlinePhoneNumber
            patient.landlinePhoneNumber = newLandlinePhoneNumber
            dbEntityManager.update(patient)
        },
        label = {
            Text(label)
        },
        enabled = enabled,
        modifier = modifier
    )
}

@Composable @Preview
fun landlinePhoneNumberTextFieldPreview() {
    landlinePhoneNumberTextField(
        patient = Patient(landlinePhoneNumber = "125478963"),
        label = "Landline Phone Number"
    )
}


@Composable
fun mailTextField(patient: Patient, label: String, enabled: Boolean = true, modifier: Modifier = Modifier) {
    var mail by mutableStateOf(patient.mail)

    TextField(
        value = mail,
        onValueChange = { newMail ->
            mail = newMail
            patient.mail = newMail
            dbEntityManager.update(patient)
        },
        label = {
            Text(label)
        },
        enabled = enabled,
        modifier = modifier
    )
}

@Composable @Preview
fun mailTextFieldPreview() {
    mailTextField(
        patient = Patient(mail = "test@mail.com"),
        label = "mail"
    )
}


@Composable
fun doctorTextField(patient: Patient, label: String, enabled: Boolean = true, modifier: Modifier = Modifier) {
    var doctor by mutableStateOf(patient.doctor)

    TextField(
        value = doctor,
        onValueChange = { newMail ->
            doctor = newMail
            patient.doctor = newMail
            dbEntityManager.update(patient)
        },
        label = {
            Text(label)
        },
        enabled = enabled,
        modifier = modifier
    )
}

@Composable @Preview
fun doctorTextFieldPreview() {
    doctorTextField(
        patient = Patient(doctor = "Laura Fo"),
        label = "Doctor"
    )
}