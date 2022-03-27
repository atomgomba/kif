package kif.static.warn

import kif.Kif.Level
import kif.MessageProducer
import kif.static.StaticLevelBoundWithThrowableTest

class StaticWarnTest : StaticLevelBoundWithThrowableTest() {
    override val subjectLevel = Level.Warn
    override val subjectSimpleMethod: (String) -> Unit get() = subject::w
    override val subjectProducerMethod: (MessageProducer) -> Unit get() = subject::w
    override val subjectSimpleWithThrowableMethod: (Throwable, String) -> Unit get() = subject::w
    override val subjectProducerWithThrowableMethod: (Throwable, MessageProducer?) -> Unit get() = subject::w
}
