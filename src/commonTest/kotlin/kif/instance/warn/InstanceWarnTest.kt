package kif.instance.warn

import kif.Kif.Level
import kif.MessageProducer
import kif.instance.InstanceLevelBoundWithThrowableTest

class InstanceWarnTest : InstanceLevelBoundWithThrowableTest() {
    override val subjectLevel = Level.Warn
    override val subjectSimpleMethod: (String) -> Unit get() = subject::w
    override val subjectProducerMethod: (MessageProducer) -> Unit get() = subject::w
    override val subjectSimpleWithThrowableMethod: (Throwable, String) -> Unit
        get() = subject::w
    override val subjectProducerWithThrowableMethod: (Throwable, MessageProducer) -> Unit
        get() = subject::w
}
