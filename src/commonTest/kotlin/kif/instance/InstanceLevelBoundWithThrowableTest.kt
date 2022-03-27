package kif.instance

import kif.KifApi
import kif.LevelBoundWithThrowableTest
import kif.kif

abstract class InstanceLevelBoundWithThrowableTest : LevelBoundWithThrowableTest() {
    override lateinit var subject: KifApi

    override fun initSubject() {
        subject = kif.new()
    }
}
