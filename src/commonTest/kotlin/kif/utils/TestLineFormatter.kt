package kif.utils

import kif.Kif.Level
import kif.Kif.LineFormatter

class TestLineFormatter : LineFormatter {
    lateinit var expected: String

    val isInvoked get() = ::expected.isInitialized

    override fun format(level: Level, text: String): String {
        expected = "$Prefix $text"
        return expected
    }

    companion object {
        const val Prefix = "TEST"
    }
}
