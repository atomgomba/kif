[![](https://jitpack.io/v/atomgomba/kif.svg)](https://jitpack.io/#atomgomba/kif)

# kif - println on steroids

kif is like log, but to the left by one. It can be seen as a direct replacement for `println` in Kotlin projects targeting the JVM and/or native platforms. It was designed mainly with the intent to print formatted messages to stdout with a timestamp and have more control over the output. kif has features which even the simplest logging facilities need to have - because let's admit it, kif is really simple.

## Notable features

* Minimalistic design for simple usecases
* Easy to use
  * No need for initialization or configuration
  * Less typing
  * Quicker auto-import (not many things are called kif)
* Works on both native and the JVM
  * Zero dependency on the JVM
  * Single dependency on native (`kotlinx-datetime`)
* Flexible & extensible

## Example usage

```kotlin
import kif.kif

fun main() {
    kif("Hello world!")
}
```

## Including it in your project

If using Gradle with Kotlin, please follow these steps:
 
1. Add the repository
```kotlin
repositories {
    maven { url = uri("https://jitpack.io") }
}
```

2. Add the dependency
```kotlin
dependencies {
    implementation("com.github.atomgomba.kif:kif:0.1.2")
}
```

## User guide

kif can be used either as a global static or as an instance. Regardless of how it's being used, the message formatting policy is the same: 

* Calling the object directly will print the message as-is using the current output handler
* Calling a logging method with an associated level (eg. `Kif::d`, `Kif::e`, etc.) will also run the message through the formatting handler before passing it to the output handler

The reason for this behavior is to let kif serve as a direct replacement for `println`. 

For a description of default formatting and default output handling, please see the rest of this document.

### Examples

```kotlin
// These will be printed as-is:

kif("I will not do anything bad ever again.")
kif { "I will not do anything bad ever again." }

val log = kif.new()
log("I will not do anything bad ever again.")
log { "I will not do anything bad ever again." }

// These messages will be formatted:

kifi("I will not do anything bad ever again.")
kifi { "I will not do anything bad ever again." }
kif.i("I will not do anything bad ever again.")
kif.i { "I will not do anything bad ever again." }

val log = kif.new()
log.i("I will not do anything bad ever again.")
log.i { "I will not do anything bad ever again." }

```

### Use as a global static

In the simplest case you can just type `kif` instead of `println` because it's less typing but will otherwise behave the same by default. The following is just going to print "Dick Laurent is dead" without any extra formatting:

```kotlin
kif("Dick Laurent is dead")
```

It is then possible to disable or enable output globally:

```kotlin
kif.quiet = true
```

If you're using a level-bound method to log a message from inside a class, the simple name of the class will be prepended to the message:

```kotlin
class Room {
    fun callMe() {
        kif.i("I was called")
        
        // Would print something like:
        // "I/17:19:39.432708 Room: I was called"
    }
}
```

The forms of e.g. `kif.w` and `kifw` can be used interchangeably when used as static. For brevity the shortcut for level `WTF` can be written as `kiff` instead of `kifwtf`.

### Use as an instance

Other usecases may require creating instances of kif, for instance when you chose to pick different formatters for different software components and would like to avoid the hassle of keeping track of state. `Kif::new` factory method can be used to create instances and initially set the desired level, formatter and output handler.

One important difference between static and non-static usage is that in the non-static manner the simple name of the enclosing class cannot be prepended to the message, hence you have to include the source of the message yourself, should you want to do so.

For example on the JVM:

```kotlin
class Room {
    private val tag = javaClass.simpleName
    private val kif = kif.new() 
        
    fun callMe() {
        kif.i("$tag: I was called")

        // Would print something like:
        // "I/17:19:39.432708 Room: I was called"
    }
}
```

A kif instance can be copied just like a Kotlin data class with the `Kif::copy` method. Let's say you already have a kif instance and would like to create a new one with a different formatter, you could write:

```kotlin
val kif = kif.new()
val chatty = kif.copy(formatter = ChatterBoxLineFormatter())
```

### Message producers

Every logging method can be passed a lambda which returns a `String` instead of a simple `String` argument. The advantage of this is that you can use the lambda to build a more complex message and at the same time invoke it lazily only when the specified logging level condition is met. For example:

```kotlin
kif.level = Level.Error
kif.w { 
    // ...relatively costly operation to build a message...
    // but will never be invoked because of the current logging level
}
```

### Levels & methods

There are several logging levels to control the output the same way as with other logging tools. The `Kif::level` property can be used to set the main logging level. The default level is `Warn`.

The following levels and methods are available in ascending order:

| Name     | Intention                                | Method     | Tag |
|----------|------------------------------------------|------------|-----|
| `Trace`  | Level for the most detailed output       | `Kif::t`   | T   |  
| `Debug`  | Information that help debugging          | `Kif::d`   | D   |
| `Info`   | Information on normal behavior           | `Kif::i`   | I   |
| `Warn`   | Something is not completely right        | `Kif::w`   | W   |
| `Error`  | Something does not work                  | `Kif::e`   | E   |
| `WTF`    | Something unexpected/should never happen | `Kif::wtf` | F   |
| `Off`    | Used internally to represent quiet mode  | -          | -   |

**Note:** WTF stands for What a Terrible Failure

As you may have noticed, setting `Kif::quiet` to `true` is almost the same as setting `Kif::level` to `Off`, but there's a difference: when using the `Kif::quiet` property, kif will keep track of the last "non-Off" level value and restore it when eventually the property is set back to `false` again.

The characters in the _Tag_ column are used by the default message formatter implementation to indicate the level more concisely. You can get this short version by calling `Kif.Level::tag`. Please refrain from calling this method on `Off` as that's an exception!

#### Examples

Static:

```kotlin
kif.level = Level.Info
kifd("You will never see me unless level is set to Debug or lower")
```

Non-static:

```kotlin
val kif = kif.new(level = Level.Info)
// or `kif.level = Level.Info` after instantiation
kif.d("You will never see me unless level is set to Debug or lower")
```

### Printing stack traces

Methods that indicate misbehavior (`Kif::w`, `Kif::e` and `Kif::wtf`) can be given a `Throwable` as first argument in which case the stack trace will be appended to the message text using [`stackTraceToString`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/stack-trace-to-string.html).

Including stack trace with the message:

```kotlin
class Room {
  fun callMe() {
    try {
      // ...this may fail, but at least I tried...
    } catch (e: RuntimeException) {
      kifw(e) { "Ooops: ${e.localizedMessage}" }

      // Would print something like:
      // W/20:33:12.942285 Room: Ooops: null: java.lang.RuntimeException
      // 	at ...
      // 	at ...
      // 	at ...
      // ...
    }
  }
}
```

### Formatters

A custom message formatter can be implemented using the `Kif.LineFormatter` functional interface which receives the logging level and the message and must return the formatted `String`:

```kotlin
fun format(level: Level, text: String): String
```

Please note that the `level` argument is only there to provide extra context, filtering of the messages is done at a higher level.

In order to use your custom formatter you need to set it as the value of the `Kif::formatter` property or the `formatter` argument of `Kif::new`. Note that since `Kif.LineFormatter` is a functional interface, you can use the [lambda syntax](https://kotlinlang.org/docs/fun-interfaces.html#sam-conversions) to define a formatter. This makes it easy to define a formatter in-place. For example:

```kotlin
kif.formatter = LineFormatter { _, text -> "Let's say, $text" }
```

The default formatting as implemented by `Kif.LineFormatter.Default` is:

    ${level.tag}/$timestamp $message

So the default formatting is similar to the following:

    D/17:19:39.432708 Room: I was called    

### Output

How the generated  output is handled can be customized as well. Output handlers in kif implement the `Kif.LineOutput` functional interface whose method has the following quite straightforward signature:

```kotlin
fun print(text: String)
```

Implementing an output handler is the appropriate way to redirect log messages to the desired output, e.g. to a web service, fax it to somebody or whatever. kif does not support the use of multiple output handlers out of the box, but you could always turn to [composition](https://en.wikipedia.org/wiki/Object_composition) and a few lines of code to achieve this - or - switch to a more complicated logging tool.

Custom output handlers can also be set either via the `Kif::output` property or the `output` argument of `Kif::new`. Note that since `Kif.LineOutput` is a functional interface too, you can use the [lambda syntax](https://kotlinlang.org/docs/fun-interfaces.html#sam-conversions) to define an output handler.

The default output handler, as implemented by `Kif.LineOutput.Default`, simply delegates its string argument to `println`. Since this implementation is admittedly very naive, you will need to implement your own output handler should you need more fancy stuff, say buffering or writing to disk and so forth.

## Changes

### 1.0.0

* Added option to include stack traces with the printed message

### 0.1.2

* Added the ability to pass the log message as a producer lambda
* Fixed stupid bug related to level testing

### 0.1.1

* Added alternative static functions (e.g. `kifi` is now a shorter alternative to `kif.i`)
* Changed license from GPLv3 to Apache 2.0 to be more permissive

### 0.1.0

* Initial release

## License

kif is released under the Apache 2.0 License.
