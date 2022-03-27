package kif.static

import kif.InvokeTest
import kif.Kif
import kif.KifApi

class StaticInvokeTest : InvokeTest() {
    override lateinit var subject: KifApi

    override fun initSubject() {
        subject = Kif
    }
}
