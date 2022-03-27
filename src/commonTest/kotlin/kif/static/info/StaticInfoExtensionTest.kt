package kif.static.info

import kif.Kif.Level
import kif.MessageProducer
import kif.kif
import kif.kifi
import kif.static.StaticLevelBoundExtensionTest

class StaticInfoExtensionTest : StaticLevelBoundExtensionTest() {
    override val subjectLevel = Level.Info
    override val subjectSimpleMethod: (String) -> Unit get() = subject::i
    override val subjectProducerMethod: (MessageProducer) -> Unit get() = subject::i
    override val subjectShortcutSimpleMethod: (String) -> Unit = { text -> kifi(text) }
    override val subjectShortcutProducerMethod: (MessageProducer) -> Unit =
        { producer -> kifi(producer) }

    override fun initSubject() {
        subject = kif
    }
}
