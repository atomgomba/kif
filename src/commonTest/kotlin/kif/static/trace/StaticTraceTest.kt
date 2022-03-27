package kif.static.trace

import kif.Kif.Level
import kif.MessageProducer
import kif.static.StaticLevelBoundTest

class StaticTraceTest : StaticLevelBoundTest() {

    override val subjectLevel = Level.Trace
    override val subjectSimpleMethod: (String) -> Unit get() = subject::t
    override val subjectProducerMethod: (MessageProducer) -> Unit get() = subject::t
}
