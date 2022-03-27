package kif.static.warn

import kif.Kif.Level
import kif.MessageProducer
import kif.kif
import kif.kifw
import kif.static.StaticLevelBoundWithThrowableExtensionTest

class StaticWarnExtensionTest : StaticLevelBoundWithThrowableExtensionTest() {
    override val subjectLevel = Level.Warn
    override val subjectSimpleMethod: (String) -> Unit get() = subject::w
    override val subjectProducerMethod: (MessageProducer) -> Unit get() = subject::w
    override val subjectSimpleWithThrowableMethod: (Throwable, String) -> Unit get() = subject::w
    override val subjectProducerWithThrowableMethod: (Throwable, MessageProducer?) -> Unit
        get() = subject::w
    override val subjectShortcutSimpleWithThrowableMethod: (Throwable, String) -> Unit =
        { t, text -> kifw(t, text) }
    override val subjectShortcutProducerWithThrowableMethod: (Throwable, MessageProducer) -> Unit =
        { t, producer -> kifw(t, producer) }
    override val subjectShortcutSimpleMethod: (String) -> Unit = { text -> kifw(text) }
    override val subjectShortcutProducerMethod: (MessageProducer) -> Unit =
        { producer -> kifw(producer) }
    override val subjectShortcutWithThrowableMethod: (Throwable) -> Unit = { t -> kifw(t) }

    override fun initSubject() {
        subject = kif
    }
}
