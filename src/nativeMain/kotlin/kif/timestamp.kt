package kif

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

actual fun generateTimestamp(): String =
    Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .toString()
        .replaceBefore('T', "")
        .replaceFirst("T", "")
        .padEnd(15, ' ')
