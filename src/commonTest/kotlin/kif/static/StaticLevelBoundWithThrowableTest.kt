package kif.static

import kif.Kif
import kif.Kif.LineFormatter
import kif.KifApi
import kif.LevelBoundWithThrowableTest
import kif.utils.makeDefaultFormatterTestPattern
import kif.utils.testMessage
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalStdlibApi::class)
abstract class StaticLevelBoundWithThrowableTest : LevelBoundWithThrowableTest() {
    override lateinit var subject: KifApi

    override fun initSubject() {
        subject = Kif
    }

    @Test
    fun simpleMessageWithThrowableDefaultFormatterTest() {
        // given
        val t = RuntimeException()
        subject.formatter = LineFormatter.Default
        val regex = makeDefaultFormatterTestPattern(subjectLevel, testMessage, t)

        // when
        subjectSimpleWithThrowableMethod(t, testMessage)

        // then
        assertTrue(regex.matchesAt(output.expected, 0),
            "Formatted message must match default pattern")
        assertTrue(output.isInvoked, "Output handler must be invoked")
    }

    @Test
    fun producedMessageWithThrowableDefaultFormatterTest() {
        // given
        val t = RuntimeException()
        subject.formatter = LineFormatter.Default
        val regex = makeDefaultFormatterTestPattern(subjectLevel, testMessage, t)

        // when
        subjectProducerWithThrowableMethod(t) { testMessage }

        // then
        assertTrue(regex.matchesAt(output.expected, 0),
            "Formatted message must match default pattern")
        assertTrue(output.isInvoked, "Output handler must be invoked")
    }
}
