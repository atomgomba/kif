package kif

import kif.Kif.Level
import kif.utils.TestLineFormatter
import kif.utils.TestLineOutput
import kif.utils.listHigherLevels
import kif.utils.testMessage
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

abstract class LevelBoundTest {
    abstract var subject: KifApi
    abstract val subjectLevel: Level

    abstract val subjectSimpleMethod: (String) -> Unit
    abstract val subjectProducerMethod: (MessageProducer) -> Unit

    lateinit var formatter: TestLineFormatter
    lateinit var output: TestLineOutput

    abstract fun initSubject()

    @BeforeTest
    fun setUp() {
        initSubject()
        formatter = TestLineFormatter()
        output = TestLineOutput()
        subject.level = subjectLevel
        subject.formatter = formatter
        subject.output = output
    }

    @Test
    fun simpleMessageMustBeDelegated() {
        // when
        subjectSimpleMethod(testMessage)

        // then
        assertTrue(formatter.isInvoked, "Formatter must be invoked")
        assertTrue(output.isInvoked, "Output handler must be invoked")
    }

    @Test
    fun producedMessageMustBeDelegated() {
        // when
        subjectProducerMethod { testMessage }

        // then
        assertTrue(formatter.isInvoked, "Formatter must be invoked")
        assertTrue(output.isInvoked, "Output handler must be invoked")
    }

    @Test
    fun simpleMessageNotDelegatedWhenQuiet() {
        // given
        subject.quiet = true

        // when
        subjectSimpleMethod(testMessage)

        // then
        assertFalse(formatter.isInvoked, "Formatter must not be invoked")
        assertFalse(output.isInvoked, "Output handler must not be invoked")
    }

    @Test
    fun producedMessageNotDelegatedWhenQuiet() {
        // given
        var isInvoked = false
        subject.quiet = true

        // when
        subjectProducerMethod {
            isInvoked = true
            testMessage
        }

        // then
        assertFalse(formatter.isInvoked, "Formatter must not be invoked")
        assertFalse(formatter.isInvoked, "Output handler must not be invoked")
        assertFalse(isInvoked, "Message producer must not be invoked")
    }

    @Test
    fun simpleMessageNotDelegatedWhenLevelIsHigher() {
        // when
        for (level in subjectLevel.listHigherLevels()) {
            subject.level = level
            subjectSimpleMethod(testMessage)
        }

        // then
        assertFalse(formatter.isInvoked, "Formatter must not be invoked")
        assertFalse(formatter.isInvoked, "Output handler must not be invoked")
    }

    @Test
    fun producedMessageNotDelegatedWhenLevelIsHigher() {
        // given
        var isInvoked = false

        // when
        for (level in subjectLevel.listHigherLevels()) {
            subject.level = level
            subjectProducerMethod {
                isInvoked = true
                testMessage
            }
        }

        // then
        assertFalse(formatter.isInvoked, "Formatter must not be invoked")
        assertFalse(formatter.isInvoked, "Output handler must not be invoked")
        assertFalse(isInvoked, "Message producer must not be invoked")
    }
}
