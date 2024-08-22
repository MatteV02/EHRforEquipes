import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import coreplugins.patientplugin.PatientPlugin
import dbManagement.HibernateDBEntityManager
import plugin.Plugin

@Composable
fun App(plugins : List<Plugin>) {
    var plugin by remember { mutableStateOf(plugins[0]) }

    MaterialTheme {
        Column {
            plugin.view(modifier = Modifier.weight(1.0f))
            NavigationBar {
                plugins.forEach { p->
                    p.menuEntry(
                        selected = false,
                        onClick = {
                            plugin = p
                        },
                        modifier = Modifier
                    )
                }
            }
        }
    }
}

fun main() = application {

    val plugins = loadPlugins()
    plugins.forEach { plugin ->
        val dbEntityManager = HibernateDBEntityManager
        dbEntityManager.dbConnection()
        plugin.start(dbEntityManager)
    }

    Window(onCloseRequest = ::exitApplication) {
        App(plugins)
    }
}

fun loadPlugins() : List<Plugin> {
    return listOf( PatientPlugin )
}