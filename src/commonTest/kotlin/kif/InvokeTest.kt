package kif

import kif.utils.TestLineFormatter
import kif.utils.TestLineOutput
import kif.utils.testMessage
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

abstract class InvokeTest {
    private lateinit var formatter: TestLineFormatter
    private lateinit var output: TestLineOutput

    abstract var subject: KifApi

    abstract fun initSubject()

    @BeforeTest
    fun setUp() {
        initSubject()
        formatter = TestLineFormatter()
        output = TestLineOutput()
        subject.formatter = formatter
        subject.output = output
    }

    @Test
    fun invokingObjectWithMessageOmitsFormatting() {
        // when
        subject(testMessage)

        // then
        assertEquals(testMessage, output.expected, "Message must match expected output")
        assertFalse(formatter.isInvoked, "Formatter must not be invoked")
    }

    @Test
    fun invokingObjectWithProducerOmitsFormatting() {
        // when
        subject { testMessage }

        // then
        assertEquals(testMessage, output.expected, "Message must match expected output")
        assertFalse(formatter.isInvoked, "Formatter must not be invoked")
    }

    @Test
    fun invokingObjectWithThrowableOmitsFormatting() {
        // given
        val t = RuntimeException()
        val expectedOutput = t.stackTraceToString()

        // when
        subject(t)

        // then
        assertEquals(expectedOutput, output.expected, "Message must match expected output")
        assertFalse(formatter.isInvoked, "Formatter must not be invoked")
    }

    @Test
    fun invokingObjectWithThrowableAndMessageHasBoth() {
        // given
        val t = RuntimeException()
        val expectedOutput = testMessage + ": " + t.stackTraceToString()

        // when
        subject(t, testMessage)

        // then
        assertEquals(expectedOutput, output.expected, "Message must match expected output")
        assertFalse(formatter.isInvoked, "Formatter must not be invoked")
    }

    @Test
    fun invokingObjectWithThrowableAndProducerHasBoth() {
        // given
        val t = RuntimeException()
        val expectedOutput = testMessage + ": " + t.stackTraceToString()

        // when
        subject(t) { testMessage }

        // then
        assertEquals(expectedOutput, output.expected, "Message must match expected output")
        assertFalse(formatter.isInvoked, "Formatter must not be invoked")
    }

    @Test
    fun invokingObjectWithMessageHasNoOutputWhenQuiet() {
        // given
        subject.quiet = true

        // when
        subject(testMessage)

        // then
        assertFalse(output.isInvoked, "Output handler must not be invoked")
        assertFalse(formatter.isInvoked, "Formatter must not be invoked")
    }

    @Test
    fun invokingObjectWithProducerHasNoOutputWhenQuiet() {
        // given
        subject.quiet = true

        // when
        subject { testMessage }

        // then
        assertFalse(output.isInvoked, "Output handler must not be invoked")
        assertFalse(formatter.isInvoked, "Formatter must not be invoked")
    }

    @Test
    fun invokingObjectWithThrowableAndMessageHasNoOutputWhenQuiet() {
        // given
        val t = RuntimeException()
        subject.quiet = true

        // when
        subject(t, testMessage)

        // then
        assertFalse(output.isInvoked, "Output handler must not be invoked")
        assertFalse(formatter.isInvoked, "Formatter must not be invoked")
    }
}
