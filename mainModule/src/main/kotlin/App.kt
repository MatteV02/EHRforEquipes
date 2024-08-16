import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dbManagement.DBEntityManager
import plugin.Plugin


@Composable
@Preview
fun App(plugins : List<Plugin>) {
    var plugin by remember { mutableStateOf(plugins[0]) }

    MaterialTheme {
        Column {
            plugin.view()
            NavigationBar {
                plugins.forEach { p->
                    p.menuEntry(
                        onClick = {
                            plugin = p
                        }
                    )
                }
            }
        }
    }
}

fun main() = application {
    val patients = DBEntityManager.getPatients()
    val places = DBEntityManager.getPlaces()
    val visits = DBEntityManager.getVisits()

    val plugins = loadPlugin()
    plugins.forEach { plugin ->
        plugin.loadData(patients, places, visits)
    }

    Window(onCloseRequest = ::exitApplication) {
        App(loadPlugin())
    }
}

fun loadPlugin() : List<Plugin> {
    return listOf()
}