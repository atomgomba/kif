package kif.instance.wtf

import kif.Kif.Level
import kif.MessageProducer
import kif.instance.InstanceLevelBoundWithThrowableTest

class InstanceWtfTest : InstanceLevelBoundWithThrowableTest() {
    override val subjectLevel = Level.WTF
    override val subjectSimpleMethod: (String) -> Unit get() = subject::wtf
    override val subjectProducerMethod: (MessageProducer) -> Unit get() = subject::wtf
    override val subjectSimpleWithThrowableMethod: (Throwable, String) -> Unit
        get() = subject::wtf
    override val subjectProducerWithThrowableMethod: (Throwable, MessageProducer) -> Unit
        get() = subject::wtf
}
