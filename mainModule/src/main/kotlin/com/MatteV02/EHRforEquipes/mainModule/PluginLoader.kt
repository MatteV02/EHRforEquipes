package com.MatteV02.EHRforEquipes.mainModule

import com.MatteV02.EHRforEquipes.commonModule.plugin.Plugin
import java.io.File
import java.net.URLClassLoader

class PluginLoader {
    val string = /*System.getProperty("user.dir") + FileSystems.getDefault().separator +*/ "plugin"
    val files = File(string).walk()

    fun load(): List<Plugin> {
        val pluginList = mutableListOf<Plugin>()

        for (file in files.filter { it.extension == "jar" }) {
            val loader = URLClassLoader(arrayOf(file.toURI().toURL()))
            val classToLoad = Class.forName("com.EHRforEquipes." + file.nameWithoutExtension + ".PluginImpl", true, loader)
            val plugin = classToLoad.getConstructor().newInstance() as Plugin

            pluginList.add(plugin)
        }

        return pluginList
    }
}