package plugin

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import entities.Patient
import entities.Place
import entities.Visit

interface Plugin {
    @Composable
    fun menuEntry(onClick : () -> Unit, modifier: Modifier = Modifier)

    @Composable
    fun view(modifier: Modifier = Modifier)

    fun loadData(patients : List<Patient>, places : List<Place>, visits : List<Visit>)
}