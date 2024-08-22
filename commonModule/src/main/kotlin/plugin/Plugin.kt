package plugin

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dbManagement.DBEntityManager

interface Plugin {
    @Composable
    fun menuEntry(selected : Boolean, onClick : () -> Unit, modifier: Modifier)

    @Composable
    fun view(modifier: Modifier)

    fun start(dbEntityManager: DBEntityManager)
}