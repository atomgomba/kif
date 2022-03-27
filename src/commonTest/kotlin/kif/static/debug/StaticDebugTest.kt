package kif.static.debug

import kif.Kif.Level
import kif.MessageProducer
import kif.static.StaticLevelBoundTest

class StaticDebugTest : StaticLevelBoundTest() {
    override val subjectLevel = Level.Debug
    override val subjectSimpleMethod: (String) -> Unit get() = subject::d
    override val subjectProducerMethod: (MessageProducer) -> Unit get() = subject::d
}
