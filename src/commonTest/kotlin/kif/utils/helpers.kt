package kif.utils

import kif.Kif.Level

fun Level.listHigherLevels(): List<Level> =
    ordinal.plus(1).until(Level.values().size).map { value -> Level.values()[value] }
