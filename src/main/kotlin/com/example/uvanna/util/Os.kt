package com.example.uvanna.util

import java.util.*

enum class OS {
    WINDOWS, LINUX, MAC
}

fun getOSU(): OS? {
    val os = System.getProperty("os.name").lowercase(Locale.getDefault())
    return when {
        os.contains("win") -> {
            OS.WINDOWS
        }
        os.contains("nix") || os.contains("nux") || os.contains("aix") -> {
            OS.LINUX
        }
        os.contains("mac") -> {
            OS.MAC
        }
        else -> null
    }
}