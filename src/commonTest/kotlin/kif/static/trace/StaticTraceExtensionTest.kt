package kif.static.trace

import kif.Kif.Level
import kif.MessageProducer
import kif.kif
import kif.kift
import kif.static.StaticLevelBoundExtensionTest

class StaticTraceExtensionTest : StaticLevelBoundExtensionTest() {
    override val subjectLevel = Level.Trace
    override val subjectSimpleMethod: (String) -> Unit get() = subject::t
    override val subjectProducerMethod: (MessageProducer) -> Unit get() = subject::t
    override val subjectShortcutSimpleMethod: (String) -> Unit = { text -> kift(text) }
    override val subjectShortcutProducerMethod: (MessageProducer) -> Unit =
        { producer -> kift(producer) }

    override fun initSubject() {
        subject = kif
    }
}
