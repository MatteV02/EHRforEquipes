package com.MatteV02.EHRforEquipes.mainModule

import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.MatteV02.EHRforEquipes.commonModule.dbManagement.HibernateDBEntityManager
import com.MatteV02.EHRforEquipes.commonModule.plugin.Plugin

@Composable
fun App(plugins : List<Plugin>) {
    var selectedPlugin by remember { mutableStateOf(plugins[0]) }

    MaterialTheme {
        Column {
            selectedPlugin.view(modifier = Modifier.weight(1.0f))
            NavigationBar {
                plugins.forEach { p->
                    p.menuEntry(
                        selected = p == selectedPlugin,
                        onClick = {
                            selectedPlugin = p
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
    val pluginLoader = PluginLoader()
    return pluginLoader.load()
}