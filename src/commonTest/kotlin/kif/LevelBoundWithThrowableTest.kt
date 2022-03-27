package kif

import kif.utils.listHigherLevels
import kif.utils.testMessage
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

abstract class LevelBoundWithThrowableTest : LevelBoundTest() {
    abstract val subjectSimpleWithThrowableMethod: (Throwable, String) -> Unit
    abstract val subjectProducerWithThrowableMethod: (Throwable, MessageProducer) -> Unit

    @Test
    fun simpleMessageWithThrowableMustBeDelegated() {
        // given
        val t = RuntimeException()

        // when
        subjectSimpleWithThrowableMethod(t, testMessage)

        // then
        assertTrue(formatter.isInvoked, "Formatter must be invoked")
        assertTrue(output.isInvoked, "Output handler must be invoked")
    }

    @Test
    fun producedMessageWithThrowableMustBeDelegated() {
        // given
        val t = RuntimeException()

        // when
        subjectProducerWithThrowableMethod(t) { testMessage }

        // then
        assertTrue(formatter.isInvoked, "Formatter must be invoked")
        assertTrue(output.isInvoked, "Output handler must be invoked")
    }

    @Test
    fun simpleMessageWithThrowableNotDelegatedWhenQuiet() {
        // given
        val t = RuntimeException()
        subject.quiet = true

        // when
        subjectSimpleWithThrowableMethod(t, testMessage)

        // then
        assertFalse(formatter.isInvoked, "Formatter must not be invoked")
        assertFalse(output.isInvoked, "Output handler must not be invoked")
    }

    @Test
    fun producedMessageWithThrowableNotDelegatedWhenQuiet() {
        // given
        val t = RuntimeException()
        var isInvoked = false
        subject.quiet = true

        // when
        subjectProducerWithThrowableMethod(t) {
            isInvoked = true
            testMessage
        }

        // then
        assertFalse(formatter.isInvoked, "Formatter must not be invoked")
        assertFalse(formatter.isInvoked, "Output handler must not be invoked")
        assertFalse(isInvoked, "Message producer must not be invoked")
    }

    @Test
    fun simpleMessageWithThrowableNotDelegatedWhenLevelIsHigher() {
        // given
        val t = RuntimeException()

        // when
        for (level in subjectLevel.listHigherLevels()) {
            subject.level = level
            subjectSimpleWithThrowableMethod(t, testMessage)
        }

        // then
        assertFalse(formatter.isInvoked, "Formatter must not be invoked")
        assertFalse(formatter.isInvoked, "Output handler must not be invoked")
    }

    @Test
    fun producedMessageWithThrowableNotDelegatedWhenLevelIsHigher() {
        // given
        val t = RuntimeException()
        var isInvoked = false

        // when
        for (level in subjectLevel.listHigherLevels()) {
            subject.level = level
            subjectProducerWithThrowableMethod(t) {
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
