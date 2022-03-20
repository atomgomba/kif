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

    operator fun invoke(text: String) = invoke { text }
    operator fun invoke(producer: () -> String)

    fun new(
        level: Level = Level.Default,
        formatter: LineFormatter = LineFormatter.Default,
        output: LineOutput = LineOutput.Default,
    ): Kif

    infix fun t(producer: () -> String)
    infix fun t(text: String) = t { text }
    infix fun d(producer: () -> String)
    infix fun d(text: String) = d { text }
    infix fun i(producer: () -> String)
    infix fun i(text: String) = i { text }
    infix fun w(producer: () -> String)
    infix fun w(text: String) = w { text }
    infix fun e(producer: () -> String)
    infix fun e(text: String) = e { text }
    infix fun wtf(producer: () -> String)
    infix fun wtf(text: String) = wtf { text }
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

    override operator fun invoke(producer: () -> String) {
        if (level.isNotOff) {
            output.print(producer())
        }
    }

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

    override infix fun t(producer: () -> String) = Trace.out(this, producer)
    override infix fun d(producer: () -> String) = Debug.out(this, producer)
    override infix fun i(producer: () -> String) = Info.out(this, producer)
    override infix fun w(producer: () -> String) = Warn.out(this, producer)
    override infix fun e(producer: () -> String) = Error.out(this, producer)
    override infix fun wtf(producer: () -> String) = WTF.out(this, producer)

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

        override operator fun invoke(producer: () -> String) {
            if (level.isNotOff) {
                output.print(producer())
            }
        }

        override fun new(
            level: Level,
            formatter: LineFormatter,
            output: LineOutput,
        ): Kif = Kif(level, formatter, output)

        override infix fun t(producer: () -> String) = Trace.out(producer)
        override infix fun d(producer: () -> String) = Debug.out(producer)
        override infix fun i(producer: () -> String) = Info.out(producer)
        override infix fun w(producer: () -> String) = Warn.out(producer)
        override infix fun e(producer: () -> String) = Error.out(producer)
        override infix fun wtf(producer: () -> String) = WTF.out(producer)
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

        internal fun out(producer: () -> String) = out(Kif, producer)

        internal fun out(kif: KifApi, producer: () -> String) {
            if (kif.level != Off && kif.level <= this) {
                kif.output.print(kif.formatter.format(this, producer()))
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
inline fun <reified T : Any> T.kift(crossinline producer: () -> String) =
    Kif.t { "${T::class.simpleName}: " + producer() }

inline fun <reified T : Any> T.kifd(text: String) = Kif.d("${T::class.simpleName}: $text")
inline fun <reified T : Any> T.kifd(crossinline producer: () -> String) =
    Kif.d { "${T::class.simpleName}: " + producer() }

inline fun <reified T : Any> T.kifi(text: String) = Kif.i("${T::class.simpleName}: $text")
inline fun <reified T : Any> T.kifi(crossinline producer: () -> String) =
    Kif.i { "${T::class.simpleName}: " + producer() }

inline fun <reified T : Any> T.kifw(text: String) = Kif.w("${T::class.simpleName}: $text")
inline fun <reified T : Any> T.kifw(crossinline producer: () -> String) =
    Kif.w { "${T::class.simpleName}: " + producer() }

inline fun <reified T : Any> T.kife(text: String) = Kif.e("${T::class.simpleName}: $text")
inline fun <reified T : Any> T.kife(crossinline producer: () -> String) =
    Kif.e { "${T::class.simpleName}: " + producer() }

inline fun <reified T : Any> T.kifwtf(text: String) = Kif.wtf("${T::class.simpleName}: $text")
inline fun <reified T : Any> T.kiff(text: String) = Kif.wtf("${T::class.simpleName}: $text")
inline fun <reified T : Any> T.kifwtf(crossinline producer: () -> String) =
    Kif.wtf { "${T::class.simpleName}: " + producer() }

inline fun <reified T : Any> T.kiff(crossinline producer: () -> String) =
    Kif.wtf { "${T::class.simpleName}: " + producer() }
