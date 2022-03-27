package kif.utils

import kif.Kif.Level

const val testMessage = "This is only a test!"

fun makeDefaultFormatterTestPattern(level: Level, message: String, t: Throwable? = null) =
    if (t == null) {
        Regex("${level.tag}/[0-9]{2}:[0-9]{2}:[0-9]{2}\\.[0-9]+[ ]*$message")
    } else {
        Regex("${level.tag}/[0-9]{2}:[0-9]{2}:[0-9]{2}\\.[0-9]+[ ]*$message: ${t::class.qualifiedName}")
    }
