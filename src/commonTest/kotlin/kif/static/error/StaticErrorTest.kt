package kif.static.error

import kif.Kif.Level
import kif.MessageProducer
import kif.static.StaticLevelBoundWithThrowableTest

class StaticErrorTest : StaticLevelBoundWithThrowableTest() {
    override val subjectLevel = Level.Error
    override val subjectSimpleMethod: (String) -> Unit get() = subject::e
    override val subjectProducerMethod: (MessageProducer) -> Unit get() = subject::e
    override val subjectSimpleWithThrowableMethod: (Throwable, String) -> Unit get() = subject::e
    override val subjectProducerWithThrowableMethod: (Throwable, MessageProducer?) -> Unit get() = subject::e
}
