package kif.instance.info

import kif.Kif.Level
import kif.MessageProducer
import kif.instance.InstanceLevelBoundTest

class InstanceInfoTest : InstanceLevelBoundTest() {
    override val subjectLevel = Level.Info
    override val subjectSimpleMethod: (String) -> Unit get() = subject::i
    override val subjectProducerMethod: (MessageProducer) -> Unit get() = subject::i
}
