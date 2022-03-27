package kif.static.debug

import kif.Kif.Level
import kif.MessageProducer
import kif.kif
import kif.kifd
import kif.static.StaticLevelBoundExtensionTest

class StaticDebugExtensionTest : StaticLevelBoundExtensionTest() {
    override val subjectLevel = Level.Debug
    override val subjectSimpleMethod: (String) -> Unit get() = subject::d
    override val subjectProducerMethod: (MessageProducer) -> Unit get() = subject::d
    override val subjectShortcutSimpleMethod: (String) -> Unit = { text -> kifd(text) }
    override val subjectShortcutProducerMethod: (MessageProducer) -> Unit =
        { producer -> kifd(producer) }

    override fun initSubject() {
        subject = kif
    }
}
