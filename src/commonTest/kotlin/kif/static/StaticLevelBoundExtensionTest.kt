package kif.static

import kif.Kif.LineFormatter
import kif.KifApi
import kif.LevelBoundTest
import kif.MessageProducer
import kif.utils.TestLineFormatter
import kif.utils.makeDefaultFormatterTestPattern
import kif.utils.testMessage
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

abstract class StaticLevelBoundExtensionTest : LevelBoundTest() {
    override lateinit var subject: KifApi

    abstract val subjectShortcutSimpleMethod: (String) -> Unit
    abstract val subjectShortcutProducerMethod: (MessageProducer) -> Unit

    @Test
    fun simpleMessageIsFormattedAndEnclosingClassPrepended() {
        // given
        val endsWithExpected = "${this::class.simpleName}: $testMessage"

        // when
        subjectSimpleMethod(testMessage)

        // then
        assertTrue(output.expected.endsWith(endsWithExpected),
            "Message must end with expected suffix")
        assertTrue(output.expected.contains(TestLineFormatter.Prefix),
            "Message must have expected prefix")
    }

    @Test
    fun producedMessageIsFormattedAndEnclosingClassPrepended() {
        // given
        val endsWithExpected = "${this::class.simpleName}: $testMessage"

        // when
        subjectProducerMethod { testMessage }

        // then
        assertTrue(output.expected.endsWith(endsWithExpected),
            "Message must end with expected suffix")
        assertTrue(output.expected.contains(TestLineFormatter.Prefix),
            "Message must have expected prefix")
    }

    @Test
    fun shortcutSimpleMessageIsFormattedAndEnclosingClassPrepended() {
        // given
        val className = this::class.simpleName!!
        val endsWithExpected = "$className: $testMessage"

        // when
        subjectShortcutSimpleMethod(testMessage)

        // then
        assertTrue(output.expected.endsWith(endsWithExpected),
            "Message must end with expected suffix")
        assertTrue(output.expected.contains(TestLineFormatter.Prefix),
            "Message must have expected prefix")
        assertEquals(2, output.expected.split(className).size, "Class name must be included once")
    }

    @Test
    fun shortcutProducedMessageIsFormattedAndEnclosingClassPrepended() {
        // given
        val className = this::class.simpleName!!
        val endsWithExpected = "$className: $testMessage"

        // when
        subjectShortcutProducerMethod { testMessage }

        // then
        assertTrue(output.expected.endsWith(endsWithExpected),
            "Message must end with expected suffix")
        assertTrue(output.expected.contains(TestLineFormatter.Prefix),
            "Message must have expected prefix")
        assertEquals(2, output.expected.split(className).size, "Class name must be included once")
    }


    @Test
    fun simpleMessageDefaultFormatterTest() {
        // given
        subject.formatter = LineFormatter.Default
        val expectedMessage = "${this::class.simpleName}: $testMessage"
        val regex = makeDefaultFormatterTestPattern(subjectLevel, expectedMessage)

        // when
        subjectSimpleMethod(testMessage)

        // then
        assertTrue(regex.matches(output.expected), "Formatted message must match default pattern")
    }

    @Test
    fun producedMessageDefaultFormatterTest() {
        // given
        subject.formatter = LineFormatter.Default
        val expectedMessage = "${this::class.simpleName}: $testMessage"
        val regex = makeDefaultFormatterTestPattern(subjectLevel, expectedMessage)

        // when
        subjectProducerMethod { testMessage }

        // then
        assertTrue(regex.matches(output.expected), "Formatted message must match default pattern")
    }
}
