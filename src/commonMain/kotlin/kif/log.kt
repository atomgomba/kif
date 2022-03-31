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

    operator fun invoke(producer: MessageProducer)
    operator fun invoke(t: Throwable, producer: MessageProducer? = null)
    operator fun invoke(text: String) = invoke { text }
    operator fun invoke(t: Throwable, text: String) = invoke(t) { text }

    fun new(
        level: Level = Level.Default,
        formatter: LineFormatter = LineFormatter.Default,
        output: LineOutput = LineOutput.Default,
    ): Kif

    fun t(producer: MessageProducer)
    fun t(text: String) = t { text }
    fun d(producer: MessageProducer)
    fun d(text: String) = d { text }
    fun i(producer: MessageProducer)
    fun i(text: String) = i { text }

    fun w(producer: MessageProducer)
    fun w(t: Throwable, producer: MessageProducer? = null)
    fun w(text: String) = w { text }
    fun w(t: Throwable, text: String) = w(t) { text }

    fun e(producer: MessageProducer)
    fun e(t: Throwable, producer: MessageProducer? = null)
    fun e(text: String) = e { text }
    fun e(t: Throwable, text: String) = e(t) { text }

    fun wtf(producer: MessageProducer)
    fun wtf(t: Throwable, producer: MessageProducer? = null)
    fun wtf(text: String) = wtf { text }
    fun wtf(t: Throwable, text: String) = wtf(t) { text }
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

    override operator fun invoke(producer: MessageProducer) {
        if (level.isNotOff) {
            output.print(level, producer())
        }
    }

    override fun invoke(t: Throwable, producer: MessageProducer?) =
        invoke { (producer?.let { it() + ": " } ?: "") + t.stackTraceToString() }

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

    override fun t(producer: MessageProducer) = Trace.out(this, producer)
    override fun d(producer: MessageProducer) = Debug.out(this, producer)
    override fun i(producer: MessageProducer) = Info.out(this, producer)

    override fun w(producer: MessageProducer) = Warn.out(this, producer)
    override fun w(t: Throwable, producer: MessageProducer?) = Warn.out(this, t, producer)

    override fun e(producer: MessageProducer) = Error.out(this, producer)
    override fun e(t: Throwable, producer: MessageProducer?) = Error.out(this, t, producer)

    override fun wtf(producer: MessageProducer) = WTF.out(this, producer)
    override fun wtf(t: Throwable, producer: MessageProducer?) = WTF.out(this, t, producer)

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

        override operator fun invoke(producer: MessageProducer) {
            if (level.isNotOff) {
                output.print(level, producer())
            }
        }

        override fun invoke(t: Throwable, producer: MessageProducer?) =
            invoke { (producer?.let { it() + ": " } ?: "") + t.stackTraceToString() }

        override fun new(
            level: Level,
            formatter: LineFormatter,
            output: LineOutput,
        ): Kif = Kif(level, formatter, output)

        override fun t(producer: MessageProducer) = Trace.out(producer)
        override fun d(producer: MessageProducer) = Debug.out(producer)
        override fun i(producer: MessageProducer) = Info.out(producer)

        override fun w(producer: MessageProducer) = Warn.out(producer)
        override fun w(t: Throwable, producer: MessageProducer?) = Warn.out(t, producer)

        override fun e(producer: MessageProducer) = Error.out(producer)
        override fun e(t: Throwable, producer: MessageProducer?) = Error.out(t, producer)

        override fun wtf(producer: MessageProducer) = WTF.out(producer)
        override fun wtf(t: Throwable, producer: MessageProducer?) = WTF.out(t, producer)
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

        internal fun out(producer: MessageProducer) = out(Kif, producer)

        internal fun out(t: Throwable, producer: MessageProducer?) = out(Kif, t, producer)

        internal fun out(kif: KifApi, producer: MessageProducer) {
            if (kif.level.isNotOff && kif.level <= this) {
                kif.output.print(this, kif.formatter.format(this, producer()))
            }
        }

        internal fun out(kif: KifApi, t: Throwable, producer: MessageProducer?) {
            if (producer == null) {
                out(kif) { t.stackTraceToString() }
            } else {
                out(kif) { producer() + ": " + t.stackTraceToString() }
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
        fun print(level: Level, text: String)

        object Default : LineOutput {
            override fun print(level: Level, text: String) = println(text)
        }
    }
}

typealias kif = Kif

typealias MessageProducer = () -> String

inline val <reified T : Any> T.kif
    get() = object : KifApi by Kif {
        override fun t(text: String) = Kif.t("${T::class.simpleName}: $text")
        override fun t(producer: MessageProducer) =
            Kif.t { "${T::class.simpleName}: ${producer()}" }

        override fun d(text: String) = Kif.d("${T::class.simpleName}: $text")
        override fun d(producer: MessageProducer) =
            Kif.d { "${T::class.simpleName}: ${producer()}" }

        override fun i(text: String) = Kif.i("${T::class.simpleName}: $text")
        override fun i(producer: MessageProducer) =
            Kif.i { "${T::class.simpleName}: ${producer()}" }

        override fun w(text: String) = Kif.w("${T::class.simpleName}: $text")
        override fun w(producer: MessageProducer) =
            Kif.w { "${T::class.simpleName}: ${producer()}" }

        override fun w(t: Throwable, text: String) = Kif.w(t) { "${T::class.simpleName}: $text" }
        override fun w(t: Throwable, producer: MessageProducer?) = if (producer == null) {
            Kif.w(t) { "${T::class.simpleName}" }
        } else {
            Kif.w(t) { "${T::class.simpleName}: ${producer()}" }
        }

        override fun e(text: String) = Kif.e("${T::class.simpleName}: $text")
        override fun e(t: Throwable, text: String) = Kif.e(t, "${T::class.simpleName}: $text")
        override fun e(t: Throwable, producer: MessageProducer?) = if (producer == null) {
            Kif.e(t) { "${T::class.simpleName}" }
        } else {
            Kif.e(t) { "${T::class.simpleName}: ${producer()}" }
        }

        override fun wtf(text: String) = Kif.wtf("${T::class.simpleName}: $text")
        override fun wtf(t: Throwable, text: String) = Kif.wtf(t, "${T::class.simpleName}: $text")
        override fun wtf(t: Throwable, producer: MessageProducer?) = if (producer == null) {
            Kif.wtf(t) { "${T::class.simpleName}" }
        } else {
            Kif.wtf(t) { "${T::class.simpleName}: ${producer()}" }
        }
    }

inline fun <reified T : Any> T.kift(text: String) = kif.t(text)
inline fun <reified T : Any> T.kift(noinline producer: MessageProducer) =
    kif.t(producer)

inline fun <reified T : Any> T.kifd(text: String) = kif.d(text)
inline fun <reified T : Any> T.kifd(noinline producer: MessageProducer) =
    kif.d(producer)

inline fun <reified T : Any> T.kifi(text: String) = kif.i(text)
inline fun <reified T : Any> T.kifi(noinline producer: MessageProducer) =
    kif.i(producer)

inline fun <reified T : Any> T.kifw(text: String) = kif.w(text)
inline fun <reified T : Any> T.kifw(noinline producer: MessageProducer) =
    kif.w(producer)

inline fun <reified T : Any> T.kifw(t: Throwable, text: String) =
    kif.w(t) { text }

inline fun <reified T : Any> T.kifw(t: Throwable, noinline producer: MessageProducer) =
    kif.w(t, producer)

inline fun <reified T : Any> T.kifw(t: Throwable) = kif.w(t)

inline fun <reified T : Any> T.kife(text: String) = kif.e(text)
inline fun <reified T : Any> T.kife(noinline producer: MessageProducer) =
    kif.e(producer)

inline fun <reified T : Any> T.kife(t: Throwable, noinline producer: MessageProducer) =
    kif.e(t, producer)

inline fun <reified T : Any> T.kife(t: Throwable, text: String) =
    kif.e(t) { text }

inline fun <reified T : Any> T.kife(t: Throwable) = kif.e(t)

inline fun <reified T : Any> T.kifwtf(text: String) = kif.wtf(text)
inline fun <reified T : Any> T.kifwtf(noinline producer: MessageProducer) =
    kif.wtf(producer)

inline fun <reified T : Any> T.kifwtf(t: Throwable, noinline producer: MessageProducer) =
    kif.wtf(t, producer)

inline fun <reified T : Any> T.kifwtf(t: Throwable, text: String) =
    kif.wtf(t) { text }

inline fun <reified T : Any> T.kifwtf(t: Throwable) = kif.wtf(t)

inline fun <reified T : Any> T.kiff(text: String) = kif.wtf(text)
inline fun <reified T : Any> T.kiff(noinline producer: MessageProducer) =
    kif.wtf(producer)

inline fun <reified T : Any> T.kiff(t: Throwable, noinline producer: MessageProducer) =
    kif.wtf(t, producer)

inline fun <reified T : Any> T.kiff(t: Throwable, text: String) =
    kif.wtf(t) { text }

inline fun <reified T : Any> T.kiff(t: Throwable) = kif.wtf(t)
