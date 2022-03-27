package kif.instance.trace

import kif.Kif.Level
import kif.MessageProducer
import kif.instance.InstanceLevelBoundTest

class InstanceTraceTest : InstanceLevelBoundTest() {

    override val subjectLevel = Level.Trace
    override val subjectSimpleMethod: (String) -> Unit get() = subject::t
    override val subjectProducerMethod: (MessageProducer) -> Unit get() = subject::t
}
