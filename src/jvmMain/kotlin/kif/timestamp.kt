package kif

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

actual fun generateTimestamp(): String =
    LocalDateTime.now()
        .format(DateTimeFormatter.ISO_LOCAL_TIME)
        .padEnd(15, ' ')

