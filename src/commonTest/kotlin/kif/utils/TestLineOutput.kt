package kif.utils

import kif.Kif.Level
import kif.Kif.LineOutput

class TestLineOutput : LineOutput {
    lateinit var expected: String

    val isInvoked get() = ::expected.isInitialized

    override fun print(level: Level, text: String) {
        expected = text
    }
}
