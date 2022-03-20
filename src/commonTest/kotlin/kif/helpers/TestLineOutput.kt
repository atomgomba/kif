package kif.helpers

import kif.Kif.LineOutput

internal class TestLineOutput : LineOutput {
    lateinit var expected: String

    val isInitialized get() = ::expected.isInitialized

    override fun print(text: String) {
        expected = text
    }
}
