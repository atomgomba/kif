package kif.static.wtf

import kif.Kif.Level
import kif.MessageProducer
import kif.kif
import kif.kifwtf
import kif.static.StaticLevelBoundWithThrowableExtensionTest

open class StaticWtfExtensionTest : StaticLevelBoundWithThrowableExtensionTest() {
    override val subjectLevel = Level.WTF
    override val subjectSimpleMethod: (String) -> Unit get() = subject::wtf
    override val subjectProducerMethod: (MessageProducer) -> Unit get() = subject::wtf
    override val subjectSimpleWithThrowableMethod: (Throwable, String) -> Unit get() = subject::wtf
    override val subjectProducerWithThrowableMethod: (Throwable, MessageProducer?) -> Unit
        get() = subject::wtf
    override val subjectShortcutSimpleWithThrowableMethod: (Throwable, String) -> Unit =
        { t, text -> kifwtf(t, text) }
    override val subjectShortcutProducerWithThrowableMethod: (Throwable, MessageProducer) -> Unit =
        { t, producer -> kifwtf(t, producer) }
    override val subjectShortcutSimpleMethod: (String) -> Unit = { text -> kifwtf(text) }
    override val subjectShortcutProducerMethod: (MessageProducer) -> Unit =
        { producer -> kifwtf(producer) }
    override val subjectShortcutWithThrowableMethod: (Throwable) -> Unit = { t -> kifwtf(t) }

    override fun initSubject() {
        subject = kif
    }
}
