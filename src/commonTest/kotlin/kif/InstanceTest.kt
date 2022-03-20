package kif

import kif.Kif.Level
import kif.helpers.TestLineFormatter
import kif.helpers.TestLineOutput
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

internal class InstanceTest {
    private lateinit var output: TestLineOutput

    @BeforeTest
    fun setUp() {
        output = TestLineOutput()
    }

    @Test
    fun simpleOutputNotFormatted() {
        // given
        val kif = kif.new(output = output)

        // when
        kif(testText)

        // then
        assertEquals(testText, output.expected)
    }

    @Test
    fun simpleProducedOutputNotFormatted() {
        // given
        val kif = kif.new(output = output)

        // when
        kif { testText }

        // then
        assertEquals(testText, output.expected)
    }

    @Test
    fun simpleOutputNotDelegatedWhenQuiet() {
        // given
        val kif = kif.new(output = output)
        kif.quiet = true

        // when
        kif(testText)

        // then
        assertFalse(output.isInitialized)
    }

    @Test
    fun simpleProducerLambdaNotInvokedWhenQuiet() {
        // given
        val kif = kif.new(output = output)
        var invoked = false
        kif.quiet = true

        // when
        kif {
            invoked = true
            testText
        }

        // then
        assertFalse(output.isInitialized)
        assertFalse(invoked)
    }

    @Test
    fun formattedProducerLambdaNotInvokedWhenQuiet() {
        // given
        val kif = kif.new(output = output)
        var invoked = false
        kif.quiet = true

        // when
        kif.w {
            invoked = true
            testText
        }

        // then
        assertFalse(output.isInitialized)
        assertFalse(invoked)
    }

    @Test
    fun formattedProducerLambdaNotInvokedWhenLevelIsLower() {
        // given
        val kif = kif.new(output = output)
        var invoked = false
        kif.quiet = true

        // when
        kif.d {
            invoked = true
            testText
        }

        // then
        assertFalse(output.isInitialized)
        assertFalse(invoked)
    }

    @Test
    fun togglingQuietModeResetsLevel() {
        // given
        val expected = Level.Trace
        val kif = kif.new(output = output)
        kif.level = expected

        // when
        kif.quiet = true
        kif.quiet = false

        // then
        assertEquals(kif.level, expected)
    }

    @Test
    fun changingLevelQuietlyKeepsLevel() {
        // given
        val expected = Level.Warn
        val kif = kif.new(output = output)
        kif.level = Level.Trace

        // when
        kif.quiet = true
        kif.level = expected
        kif.quiet = false

        // then
        assertEquals(kif.level, expected)
    }

    @Test
    fun settingLevelResetsQuietMode() {
        // given
        val kif = kif.new(output = output)
        kif.quiet = true

        // when
        kif.level = Level.Warn

        // then
        assertFalse(kif.quiet)
    }

    @Test
    fun debugOutputNotDelegatedWhenErrorLevelSet() {
        // given
        val kif = kif.new(
            output = output,
            level = Level.Error,
        )

        // when
        kif d testText

        // then
        assertFalse(output.isInitialized)
    }

    @Test
    fun debugProducedOutputNotDelegatedWhenErrorLevelSet() {
        // given
        val kif = kif.new(
            output = output,
            level = Level.Error,
        )

        // when
        kif d { testText }

        // then
        assertFalse(output.isInitialized)
    }

    @Test
    fun customFormatterOmittedWithoutLevel() {
        // given
        val kif = kif.new(
            output = output,
            formatter = TestLineFormatter(),
        )

        // when
        kif(testText)

        // then
        assertEquals(testText, output.expected)
    }

    @Test
    fun customFormatterWorksWithLevel() {
        // given
        val formatter = TestLineFormatter()
        val kif = kif.new(
            output = output,
            formatter = formatter,
        )

        // when
        kif.w(testText)

        // then
        assertEquals(formatter.expected, output.expected)
    }

    // 8<-
    // Instance-only tests start here
    // ---

    @Test
    fun copyShouldKeepOmittedProperties() {
        // given
        val kif = kif.new()

        // when
        val new = kif.copy()

        // then
        assertEquals(kif.level, new.level)
        assertEquals(kif.formatter, new.formatter)
        assertEquals(kif.output, new.output)
    }

    @Test
    fun copyShouldChangeSetProperties() {
        // given
        val formatter = TestLineFormatter()
        val output = TestLineOutput()
        val kif = kif.new()

        // when
        val new = kif.copy(
            formatter = formatter,
            output = output,
        )

        // then
        assertEquals(new.level, kif.level)
        assertEquals(new.formatter, formatter)
        assertEquals(new.output, output)
    }

    companion object {
        private const val testText = "This is only a test"
    }
}
