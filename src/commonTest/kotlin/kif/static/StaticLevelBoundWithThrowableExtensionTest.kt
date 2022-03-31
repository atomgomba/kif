package kif.static

import kif.Kif.LineFormatter
import kif.KifApi
import kif.LevelBoundWithThrowableTest
import kif.MessageProducer
import kif.utils.TestLineFormatter
import kif.utils.makeDefaultFormatterTestPattern
import kif.utils.testMessage
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalStdlibApi::class)
abstract class StaticLevelBoundWithThrowableExtensionTest : LevelBoundWithThrowableTest() {
    override lateinit var subject: KifApi

    abstract val subjectShortcutSimpleMethod: (String) -> Unit
    abstract val subjectShortcutProducerMethod: (MessageProducer) -> Unit
    abstract val subjectShortcutWithThrowableMethod: (Throwable) -> Unit
    abstract val subjectShortcutSimpleWithThrowableMethod: (Throwable, String) -> Unit
    abstract val subjectShortcutProducerWithThrowableMethod: (Throwable, MessageProducer) -> Unit

    @Test
    fun simpleMessageWithThrowableIsFormattedAndEnclosingClassPrepended() {
        // given
        val t = RuntimeException()
        val containsExpected = "${this::class.simpleName}: $testMessage: ${t.stackTraceToString()}"

        // when
        subjectSimpleWithThrowableMethod(t, testMessage)

        // then
        assertTrue(output.expected.contains(containsExpected),
            "Message must contain expected substring")
        assertTrue(output.expected.contains(TestLineFormatter.Prefix),
            "Message must have expected prefix")
    }

    @Test
    fun producedMessageWithThrowableIsFormattedAndEnclosingClassPrepended() {
        // given
        val t = RuntimeException()
        val containsExpected = "${this::class.simpleName}: $testMessage: ${t.stackTraceToString()}"

        // when
        subjectProducerWithThrowableMethod(t) { testMessage }

        // then
        assertTrue(output.expected.contains(containsExpected),
            "Message must contain expected substring")
        assertTrue(output.expected.contains(TestLineFormatter.Prefix),
            "Message must have expected prefix")
    }

    @Test
    fun shortcutSimpleMessageWithThrowableIsFormattedAndEnclosingClassPrepended() {
        // given
        val t = RuntimeException()
        val className = this::class.simpleName!!
        val containsExpected = "$className: $testMessage: ${t.stackTraceToString()}"

        // when
        subjectShortcutSimpleWithThrowableMethod(t, testMessage)

        // then
        assertTrue(output.expected.contains(containsExpected),
            "Message must contain expected substring")
        assertTrue(output.expected.contains(TestLineFormatter.Prefix),
            "Message must have expected prefix")
        assertEquals(2, output.expected.split("$className:").size, "Class name must be included once")
    }

    @Test
    fun shortcutProducedMessageWithThrowableIsFormattedAndEnclosingClassPrepended() {
        // given
        val t = RuntimeException()
        val className = this::class.simpleName!!
        val containsExpected = "$className: $testMessage: ${t.stackTraceToString()}"

        // when
        subjectShortcutProducerWithThrowableMethod(t) { testMessage }

        // then
        assertTrue(output.expected.contains(containsExpected),
            "Message must contain expected substring")
        assertTrue(output.expected.contains(TestLineFormatter.Prefix),
            "Message must have expected prefix")
        assertEquals(2, output.expected.split("$className:").size, "Class name must be included once")
    }

    @Test
    fun shortcutWithThrowableIsFormattedAndEnclosingClassPrepended() {
        // given
        val t = RuntimeException()
        val className = this::class.simpleName!!
        val containsExpected = "$className: ${t.stackTraceToString()}"

        // when
        subjectShortcutWithThrowableMethod(t)

        // then
        assertTrue(output.expected.contains(containsExpected),
            "Message must contain expected substring")
        assertTrue(output.expected.contains(TestLineFormatter.Prefix),
            "Message must have expected prefix")
        assertEquals(2, output.expected.split("$className:").size, "Class name must be included once")
    }

    @Test
    fun simpleMessageWithThrowableDefaultFormatterTest() {
        // given
        val t = RuntimeException()
        subject.formatter = LineFormatter.Default
        val expectedMessage = "${this::class.simpleName}: $testMessage"
        val regex = makeDefaultFormatterTestPattern(subjectLevel, expectedMessage, t)

        // when
        subjectSimpleWithThrowableMethod(t, testMessage)

        // then
        assertTrue(regex.matchesAt(output.expected, 0),
            "Formatted message must match default pattern")
    }

    @Test
    fun producedMessageWithThrowableDefaultFormatterTest() {
        // given
        val t = RuntimeException()
        subject.formatter = LineFormatter.Default
        val expectedMessage = "${this::class.simpleName}: $testMessage"
        val regex = makeDefaultFormatterTestPattern(subjectLevel, expectedMessage, t)

        // when
        subjectProducerWithThrowableMethod(t) { testMessage }

        // then
        assertTrue(regex.matchesAt(output.expected, 0),
            "Formatted message must match default pattern")
    }
}
