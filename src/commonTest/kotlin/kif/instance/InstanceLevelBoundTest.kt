package kif.instance

import kif.KifApi
import kif.LevelBoundTest
import kif.kif

abstract class InstanceLevelBoundTest : LevelBoundTest() {
    override lateinit var subject: KifApi

    override fun initSubject() {
        subject = kif.new()
    }
}
