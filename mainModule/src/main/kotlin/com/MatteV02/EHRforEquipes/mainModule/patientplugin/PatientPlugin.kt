package com.MatteV02.EHRforEquipes.mainModule.patientplugin


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import com.MatteV02.EHRforEquipes.commonModule.dbManagement.DBEntityManager
import com.MatteV02.EHRforEquipes.commonModule.entities.patient.Patient
import com.MatteV02.EHRforEquipes.commonModule.plugin.Plugin
import java.util.ResourceBundle

private val bundle = ResourceBundle.getBundle("PatientPlugin")

object PatientPlugin: Plugin {
    internal lateinit var dbEntityManager: DBEntityManager

    @Composable
    override fun menuEntry(selected: Boolean, onClick: () -> Unit, modifier: Modifier) {
        NavigationRailItem(
            selected = selected,
            onClick = onClick,
            icon = {
                Icon(
                    painter = painterResource("icons/patient_list_24px.xml"),
                    contentDescription = null
                )
            },
            label = {
                Text(bundle.getString("menuEntry"))
            }
        )
    }

    @Composable
    override fun view(modifier: Modifier) {
        val patients = remember { dbEntityManager.getPatients().toMutableStateList()  }

        var selectedPatient by remember { mutableStateOf(patients.getOrNull(0)) }
        var onEditView by remember { mutableStateOf(false) }
        var onShowVisitsView by remember { mutableStateOf(false) }

        if (!onEditView) {
            Box(modifier.fillMaxSize()) {
                LazyColumn {
                    items(patients) { patient ->
                        PatientRow(
                            selected = selectedPatient == patient,
                            patient,
                            onDelete = {
                                if (selectedPatient == patient) {
                                    selectedPatient = null
                                }
                                patients.remove(patient)
                                dbEntityManager.remove(patient)
                            },
                            Modifier.clickable(
                                onClick = {
                                    selectedPatient = patient
                                }
                            ))
                    }
                }

                Row(modifier = Modifier.align(Alignment.BottomEnd)) {
                    ExtendedFloatingActionButton(
                        onClick = {
                            onEditView = true
                        },
                    ) {
                        Icon(Icons.Default.Edit, null)
                        Text(bundle.getString("editFAB"))
                    }
                    ExtendedFloatingActionButton(
                        onClick = {
                            onShowVisitsView = true
                        }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ViewList, null)
                        Text(bundle.getString("visitsFAB"))
                    }
                    FloatingActionButton(
                        onClick = {
                            val newPatient = Patient()
                            patients.addFirst(newPatient)
                            dbEntityManager.insert(newPatient)
                        },
                    ) {
                        Icon(Icons.Default.Add, null)
                    }
                }
            }
        } else {
            if (selectedPatient != null) {
                Box(modifier) {
                    PatientEditView(selectedPatient!!)
                    Button(
                        onClick = {
                            onEditView = false
                        },
                        Modifier.align(Alignment.BottomEnd)
                    ) {
                        Text("OK")
                    }
                }
            } else {
                onEditView = false
            }
        }

        if (onShowVisitsView) {
            Window(
                onCloseRequest = {
                    onShowVisitsView = false
                }
            ) {
                if (selectedPatient != null) {
                    PatientVisitView(selectedPatient!!, dbEntityManager.getVisits(), Modifier)
                } else {
                    onShowVisitsView = false
                }
            }
        }
    }

    override fun start(dbEntityManager: DBEntityManager) {
        PatientPlugin.dbEntityManager = dbEntityManager
    }
}