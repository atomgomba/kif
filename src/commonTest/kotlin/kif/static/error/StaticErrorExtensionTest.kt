package kif.static.error

import kif.Kif.Level
import kif.MessageProducer
import kif.kif
import kif.kife
import kif.static.StaticLevelBoundWithThrowableExtensionTest

class StaticErrorExtensionTest : StaticLevelBoundWithThrowableExtensionTest() {
    override val subjectLevel = Level.Error
    override val subjectSimpleMethod: (String) -> Unit get() = subject::e
    override val subjectProducerMethod: (MessageProducer) -> Unit get() = subject::e
    override val subjectSimpleWithThrowableMethod: (Throwable, String) -> Unit get() = subject::e
    override val subjectProducerWithThrowableMethod: (Throwable, MessageProducer?) -> Unit
        get() = subject::e
    override val subjectShortcutSimpleWithThrowableMethod: (Throwable, String) -> Unit =
        { t, text -> kife(t, text) }
    override val subjectShortcutProducerWithThrowableMethod: (Throwable, MessageProducer) -> Unit =
        { t, producer -> kife(t, producer) }
    override val subjectShortcutSimpleMethod: (String) -> Unit = { text -> kife(text) }
    override val subjectShortcutProducerMethod: (MessageProducer) -> Unit =
        { producer -> kife(producer) }
    override val subjectShortcutWithThrowableMethod: (Throwable) -> Unit = { t -> kife(t) }

    override fun initSubject() {
        subject = kif
    }
}
