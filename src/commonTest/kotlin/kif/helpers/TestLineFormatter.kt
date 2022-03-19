package kif.helpers

import kif.Kif.Level
import kif.Kif.LineFormatter

internal class TestLineFormatter : LineFormatter {
    lateinit var expected: String

    override fun format(level: Level, text: String): String {
        expected = "$frag$text$frag"
        return expected
    }

    companion object {
        private const val frag: String = "#"
    }
}
