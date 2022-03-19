# kif logger

kif is like log, but to the left by one. kif has features which even the simplest logging facilities need to have - because let's admit it, kif is really simple. It can be seen as a direct replacement for `println` in Kotlin projects targeting the JVM and/or native platforms. It was designed mainly with the intent to print formatted messages to stdout with a timestamp and have more control over the output.

## Notable features

* Minimalistic design for simple usecases
* Easy to use, no need for initialization or configuration
* Works on both native and the JVM
  * Zero dependency on the JVM
  * Single dependency on native (`kotlinx-datetime`)
* Flexible & extensible

## Example usage

```kotlin
import kif.kif

fun main() {
    kif d "Hello world!"
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
implementation("com.github.atomgomba:kif:0.1.0")
```

## User guide

kif can be used either as a global static or as an instance. Regardless of how its being used, the message formatting policy is the same: 

* Calling the object directly will print the message as-is using the current output handler
* Calling a logging method with an associated level (eg. `Kif::d`, `Kif::e`, etc.) will also run the message through the formatting handler before passing it to the output handler

The reason for this behavior is to let kif serve as a direct replacement for `println`. 

For a description of default formatting and default output handling, please see the rest of this document.

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
        kif i "I was called"
        
        // Would print something like:
        // "I/17:19:39.432708 Room: I was called"
    }
}
```

### Use as an instance

Other usecases may require creating instances of kif, for instance when you chose to pick different logging levels for different software components and would like to avoid the hassle of keeping track of state. `Kif::new` factory method can be used to create instances and initially set the desired level, formatter and output handler.

One important difference between static and non-static usage is that in the non-static manner the simple name of the enclosing class cannot be prepended to the message, hence you have to include the source of the message yourself, should you want to do so.

For example on the JVM:

```kotlin
class Room {
    private val tag = javaClass.simpleName
    private val kif = kif.new() 
        
    fun callMe() {
        kif i "$tag: I was called"

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

The level-bound output methods are all infix, so you can spare some keystrokes by not having to include the parenthesis.

As you may have noticed, setting `Kif::quiet` to `true` is almost the same as setting `Kif::level` to `Off`, but there's a difference: when using the `Kif::quiet` property, kif will keep track of the last "non-Off" level value and restore it when eventually the property is set back to `false` again.

The characters in the _Tag_ column are used by the default message formatter implementation to indicate the level more concisely. You can get this short version by calling `Level::tag`. Please refrain from calling this method on `Off` as that's an exception!

#### Examples

Static:

```kotlin
kif.level = Level.Warn
kif d "You will never see me unless level is set to Debug or lower"
```

Non-static:

```kotlin
val kif = kif.new(level = Level.Warn)
// or `kif.level = Level.Warn` after instantiation
kif d "You will never see me unless level is set to Debug or lower"
```

### Formatters

A custom message formatter can be implemented using the `Kif.LineFormatter` functional interface which receives the logging level and the message and must return the formatted `String`:

```kotlin
fun format(level: Level, text: String): String
```

Please note that the `level` argument is only there to provide extra context, filtering of the messages is done at a higher level.

In order to use your custom formatter you need to set it as the value of the `Kif::formatter` property or the `formatter` argument of `Kif::new`. Note that since `Kif.LineFormatter` is a functional interface, you can use the [lambda syntax](https://kotlinlang.org/docs/fun-interfaces.html#sam-conversions) to define a formatter.

The default formatting as implemented by `Kif.LineFormatter.Default` is:

    $level/$timestamp $message

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

### 0.1.0

* Initial release

## License

kif is released under the GNU GPLv3 License.