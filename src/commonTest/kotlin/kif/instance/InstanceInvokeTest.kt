package kif.instance

import kif.InvokeTest
import kif.KifApi
import kif.kif

class InstanceInvokeTest : InvokeTest() {
    override lateinit var subject: KifApi

    override fun initSubject() {
        subject = kif.new()
    }
}
