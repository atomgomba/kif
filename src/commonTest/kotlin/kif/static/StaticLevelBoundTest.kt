package kif.static

import kif.Kif
import kif.Kif.LineFormatter
import kif.KifApi
import kif.LevelBoundTest
import kif.utils.makeDefaultFormatterTestPattern
import kif.utils.testMessage
import kotlin.test.Test
import kotlin.test.assertTrue

abstract class StaticLevelBoundTest : LevelBoundTest() {
    override lateinit var subject: KifApi

    override fun initSubject() {
        subject = Kif
    }

    @Test
    fun simpleMessageDefaultFormatterTest() {
        // given
        subject.formatter = LineFormatter.Default
        val regex = makeDefaultFormatterTestPattern(subjectLevel, testMessage)

        // when
        subjectSimpleMethod(testMessage)

        // then
        assertTrue(regex.matches(output.expected), "Formatted message must match default pattern")
        assertTrue(output.isInvoked, "Output handler must be invoked")
    }

    @Test
    fun producedMessageDefaultFormatterTest() {
        // given
        subject.formatter = LineFormatter.Default
        val regex = makeDefaultFormatterTestPattern(subjectLevel, testMessage)

        // when
        subjectProducerMethod { testMessage }

        // then
        assertTrue(regex.matches(output.expected), "Formatted message must match default pattern")
        assertTrue(output.isInvoked, "Output handler must be invoked")
    }
}
