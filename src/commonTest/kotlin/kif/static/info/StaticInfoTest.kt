package kif.static.info

import kif.Kif.Level
import kif.MessageProducer
import kif.static.StaticLevelBoundTest

class StaticInfoTest : StaticLevelBoundTest() {

    override val subjectLevel = Level.Info
    override val subjectSimpleMethod: (String) -> Unit get() = subject::i
    override val subjectProducerMethod: (MessageProducer) -> Unit get() = subject::i
}
