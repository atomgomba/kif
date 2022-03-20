package kif

import kif.Kif.Level
import kif.Kif.LineFormatter
import kif.helpers.TestLineFormatter
import kif.helpers.TestLineOutput
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

internal class StaticTest {
    private lateinit var output: TestLineOutput

    @BeforeTest
    fun setUp() {
        // reset things to defaults as we are static
        output = TestLineOutput()
        kif.output = output
        kif.level = Level.Warn
        kif.formatter = LineFormatter.Default
    }

    @Test
    fun simpleOutputNotFormatted() {
        // when
        kif(testText)

        // then
        assertEquals(testText, output.expected)
    }

    @Test
    fun simpleProducedOutputNotFormatted() {
        // when
        kif { testText }

        // then
        assertEquals(testText, output.expected)
    }

    @Test
    fun simpleOutputNotDelegatedWhenQuiet() {
        // given
        kif.quiet = true

        // when
        kif(testText)

        // then
        assertFalse(output.isInitialized)
    }

    @Test
    fun simpleProducedOutputNotDelegatedWhenQuiet() {
        // given
        kif.quiet = true

        // when
        kif { testText }

        // then
        assertFalse(output.isInitialized)
    }

    @Test
    fun simpleProducerLambdaNotInvokedWhenQuiet() {
        // given
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
        kif.quiet = true

        // when
        kif.level = Level.Warn

        // then
        assertFalse(kif.quiet)
    }

    @Test
    fun debugOutputNotDelegatedWhenErrorLevelSet() {
        // given
        kif.level = Level.Error

        // when
        kif d testText

        // then
        assertFalse(output.isInitialized)
    }

    @Test
    fun debugProducedOutputNotDelegatedWhenErrorLevelSet() {
        // given
        kif.level = Level.Error

        // when
        kif d { testText }

        // then
        assertFalse(output.isInitialized)
    }

    @Test
    fun customFormatterOmittedWithoutLevel() {
        // given
        kif.formatter = TestLineFormatter()

        // when
        kif(testText)

        // then
        assertEquals(testText, output.expected)
    }

    @Test
    fun customFormatterWorksWithLevel() {
        // given
        val formatter = TestLineFormatter()
        kif.formatter = formatter

        // when
        kif.w(testText)

        // then
        assertEquals(formatter.expected, output.expected)
    }

    // 8<-
    // Static-only tests start here
    // ---

    @Test
    fun shortcutFunTraceDelegatesToObject() {
        // given
        val formatter = TestLineFormatter()
        kif.formatter = formatter
        kif.level = Level.Trace

        // when
        kift(testText)

        // then
        assertEquals(formatter.expected, output.expected)
    }

    @Test
    fun shortcutFunTraceProducerDelegatesToObject() {
        // given
        val formatter = TestLineFormatter()
        kif.formatter = formatter
        kif.level = Level.Trace

        // when
        kift { testText }

        // then
        assertEquals(formatter.expected, output.expected)
    }

    @Test
    fun shortcutFunDebugDelegatesToObject() {
        // given
        val formatter = TestLineFormatter()
        kif.formatter = formatter
        kif.level = Level.Debug

        // when
        kifd(testText)

        // then
        assertEquals(formatter.expected, output.expected)
    }

    @Test
    fun shortcutFunDebugProducerDelegatesToObject() {
        // given
        val formatter = TestLineFormatter()
        kif.formatter = formatter
        kif.level = Level.Debug

        // when
        kifd { testText }

        // then
        assertEquals(formatter.expected, output.expected)
    }

    @Test
    fun shortcutFunInfoDelegatesToObject() {
        // given
        val formatter = TestLineFormatter()
        kif.formatter = formatter
        kif.level = Level.Info

        // when
        kifi(testText)

        // then
        assertEquals(formatter.expected, output.expected)
    }

    @Test
    fun shortcutFunInfoProducerDelegatesToObject() {
        // given
        val formatter = TestLineFormatter()
        kif.formatter = formatter
        kif.level = Level.Info

        // when
        kifi { testText }

        // then
        assertEquals(formatter.expected, output.expected)
    }

    @Test
    fun shortcutFunWarnDelegatesToObject() {
        // given
        val formatter = TestLineFormatter()
        kif.formatter = formatter

        // when
        kifw(testText)

        // then
        assertEquals(formatter.expected, output.expected)
    }

    @Test
    fun shortcutFunWarnProducerDelegatesToObject() {
        // given
        val formatter = TestLineFormatter()
        kif.formatter = formatter

        // when
        kifw { testText }

        // then
        assertEquals(formatter.expected, output.expected)
    }

    @Test
    fun shortcutFunErrorDelegatesToObject() {
        // given
        val formatter = TestLineFormatter()
        kif.formatter = formatter
        kif.level = Level.Error

        // when
        kife(testText)

        // then
        assertEquals(formatter.expected, output.expected)
    }

    @Test
    fun shortcutFunErrorProducerDelegatesToObject() {
        // given
        val formatter = TestLineFormatter()
        kif.formatter = formatter
        kif.level = Level.Error

        // when
        kife { testText }

        // then
        assertEquals(formatter.expected, output.expected)
    }

    @Test
    fun shortcutFunFailureDelegatesToObject2() {
        // given
        val formatter = TestLineFormatter()
        kif.formatter = formatter
        kif.level = Level.WTF

        // when
        kifwtf(testText)

        // then
        assertEquals(formatter.expected, output.expected)
    }

    @Test
    fun shortcutFunFailureDelegatesToObject() {
        // given
        val formatter = TestLineFormatter()
        kif.formatter = formatter
        kif.level = Level.WTF

        // when
        kiff(testText)

        // then
        assertEquals(formatter.expected, output.expected)
    }

    @Test
    fun shortcutFunFailureProducerDelegatesToObject() {
        // given
        val formatter = TestLineFormatter()
        kif.formatter = formatter
        kif.level = Level.WTF

        // when
        kifwtf { testText }

        // then
        assertEquals(formatter.expected, output.expected)
    }

    @Test
    fun shortcutFunFailureProducerDelegatesToObject2() {
        // given
        val formatter = TestLineFormatter()
        kif.formatter = formatter
        kif.level = Level.WTF

        // when
        kiff { testText }

        // then
        assertEquals(formatter.expected, output.expected)
    }

    companion object {
        private const val testText = "This is only a test"
    }
}
