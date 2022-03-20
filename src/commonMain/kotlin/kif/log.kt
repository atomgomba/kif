/*
    kif - Multiplatform logging library
    Copyright (C) 2022  Károly Kiripolszky

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package kif

import kif.Kif.Level
import kif.Kif.Level.Debug
import kif.Kif.Level.Error
import kif.Kif.Level.Info
import kif.Kif.Level.Off
import kif.Kif.Level.Trace
import kif.Kif.Level.WTF
import kif.Kif.Level.Warn
import kif.Kif.LineFormatter
import kif.Kif.LineOutput
import kotlin.native.concurrent.ThreadLocal

interface KifApi {
    var level: Level
    var quiet: Boolean
    var formatter: LineFormatter
    var output: LineOutput

    operator fun invoke(text: String)

    fun new(
        level: Level = Level.Default,
        formatter: LineFormatter = LineFormatter.Default,
        output: LineOutput = LineOutput.Default,
    ): Kif

    infix fun t(text: String)
    infix fun d(text: String)
    infix fun i(text: String)
    infix fun w(text: String)
    infix fun e(text: String)
    infix fun wtf(text: String)
}

class Kif private constructor(
    override var level: Level,
    override var formatter: LineFormatter,
    override var output: LineOutput,
) : KifApi {

    private lateinit var origLevel: Level
    override var quiet: Boolean
        get() = level.isOff
        set(isQuiet) {
            if (isQuiet && level.isNotOff) {
                origLevel = level
                level = Off
            } else if (!isQuiet && level.isOff) {
                level = origLevel
            }
        }

    override operator fun invoke(text: String) = output.print(text)

    override fun new(
        level: Level,
        formatter: LineFormatter,
        output: LineOutput,
    ): Kif = Kif(level, formatter, output)

    fun copy(
        level: Level? = null,
        formatter: LineFormatter? = null,
        output: LineOutput? = null,
    ): Kif = Kif(
        level ?: this.level,
        formatter ?: this.formatter,
        output ?: this.output
    )

    override infix fun t(text: String) = Trace.out(this, text)
    override infix fun d(text: String) = Debug.out(this, text)
    override infix fun i(text: String) = Info.out(this, text)
    override infix fun w(text: String) = Warn.out(this, text)
    override infix fun e(text: String) = Error.out(this, text)
    override infix fun wtf(text: String) = WTF.out(this, text)

    @ThreadLocal
    companion object : KifApi {

        override var level = Level.Default

        private lateinit var origLevel: Level
        override var quiet: Boolean
            get() = level.isOff
            set(isQuiet) {
                if (isQuiet && level.isNotOff) {
                    origLevel = level
                    level = Off
                } else if (!isQuiet && level.isOff) {
                    level = origLevel
                }
            }

        override var formatter: LineFormatter = LineFormatter.Default
        override var output: LineOutput = LineOutput.Default

        override operator fun invoke(text: String) = output.print(text)

        override fun new(
            level: Level,
            formatter: LineFormatter,
            output: LineOutput,
        ): Kif = Kif(level, formatter, output)

        override infix fun t(text: String) = Trace.out(text)
        override infix fun d(text: String) = Debug.out(text)
        override infix fun i(text: String) = Info.out(text)
        override infix fun w(text: String) = Warn.out(text)
        override infix fun e(text: String) = Error.out(text)
        override infix fun wtf(text: String) = WTF.out(text)
    }

    enum class Level {
        Trace,
        Debug,
        Info,
        Warn,
        Error,
        WTF,
        Off;

        internal val isOff: Boolean get() = this == Off

        internal val isNotOff: Boolean get() = isOff.not()

        val tag: Char
            get() = when (this) {
                Trace -> 'T'
                Debug -> 'D'
                Info -> 'I'
                Warn -> 'W'
                Error -> 'E'
                WTF -> 'F'
                Off -> error("Ooops, I've failed to remain silent!")
            }

        internal fun out(text: String) = out(Kif, text)

        internal fun out(kif: KifApi, text: String) {
            if (kif.level != Off && this <= kif.level) {
                kif.output.print(kif.formatter.format(this, text))
            }
        }

        companion object {
            val Default get(): Level = Warn
        }
    }

    fun interface LineFormatter {
        fun format(level: Level, text: String): String

        object Default : LineFormatter {
            override fun format(level: Level, text: String): String =
                "${level.tag}/${generateTimestamp()} $text"
        }
    }

    fun interface LineOutput {
        fun print(text: String)

        object Default : LineOutput {
            override fun print(text: String) = println(text)
        }
    }
}

typealias kif = Kif

inline val <reified T : Any> T.kif
    get() = object : KifApi by Kif {
        override infix fun t(text: String) = Kif.t("${T::class.simpleName}: $text")
        override infix fun d(text: String) = Kif.d("${T::class.simpleName}: $text")
        override infix fun i(text: String) = Kif.i("${T::class.simpleName}: $text")
        override infix fun w(text: String) = Kif.w("${T::class.simpleName}: $text")
        override infix fun e(text: String) = Kif.e("${T::class.simpleName}: $text")
        override infix fun wtf(text: String) = Kif.wtf("${T::class.simpleName}: $text")
    }

inline fun <reified T : Any> T.kift(text: String) = Kif.t("${T::class.simpleName}: $text")
inline fun <reified T : Any> T.kifd(text: String) = Kif.d("${T::class.simpleName}: $text")
inline fun <reified T : Any> T.kifi(text: String) = Kif.i("${T::class.simpleName}: $text")
inline fun <reified T : Any> T.kifw(text: String) = Kif.w("${T::class.simpleName}: $text")
inline fun <reified T : Any> T.kife(text: String) = Kif.e("${T::class.simpleName}: $text")
inline fun <reified T : Any> T.kiff(text: String) = Kif.wtf("${T::class.simpleName}: $text")
