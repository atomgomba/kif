package kif.static.wtf

import kif.MessageProducer
import kif.kif
import kif.kiff

class StaticWtfExtension2Test : StaticWtfExtensionTest() {
    override val subjectShortcutSimpleWithThrowableMethod: (Throwable, String) -> Unit =
        { t, text -> kiff(t, text) }
    override val subjectShortcutProducerWithThrowableMethod: (Throwable, MessageProducer) -> Unit =
        { t, producer -> kiff(t, producer) }
    override val subjectShortcutSimpleMethod: (String) -> Unit = { text -> kiff(text) }
    override val subjectShortcutProducerMethod: (MessageProducer) -> Unit =
        { producer -> kiff(producer) }
    override val subjectShortcutWithThrowableMethod: (Throwable) -> Unit = { t -> kiff(t) }

    override fun initSubject() {
        subject = kif
    }
}
