package kif.instance

import kif.kif
import kif.utils.TestLineFormatter
import kif.utils.TestLineOutput
import kotlin.test.Test
import kotlin.test.assertEquals

class InstanceSpecificTest {
    @Test
    fun copyShouldKeepOmittedProperties() {
        // given
        val kif = kif.new()

        // when
        val new = kif.copy()

        // then
        assertEquals(kif.level, new.level)
        assertEquals(kif.formatter, new.formatter)
        assertEquals(kif.output, new.output)
    }

    @Test
    fun copyShouldChangeSetProperties() {
        // given
        val formatter = TestLineFormatter()
        val output = TestLineOutput()
        val kif = kif.new()

        // when
        val new = kif.copy(
            formatter = formatter,
            output = output,
        )

        // then
        assertEquals(new.level, kif.level)
        assertEquals(new.formatter, formatter)
        assertEquals(new.output, output)
    }
}
