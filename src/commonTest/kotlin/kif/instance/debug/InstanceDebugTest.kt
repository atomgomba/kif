package kif.instance.debug

import kif.Kif.Level
import kif.MessageProducer
import kif.instance.InstanceLevelBoundTest

class InstanceDebugTest : InstanceLevelBoundTest() {
    override val subjectLevel = Level.Debug
    override val subjectSimpleMethod: (String) -> Unit get() = subject::d
    override val subjectProducerMethod: (MessageProducer) -> Unit get() = subject::d
}
