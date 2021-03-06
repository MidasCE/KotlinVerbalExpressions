package co.zsmb.verbalexpressions

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RealExampleTests {

    @Test
    fun urls() {
        val verex = VerEx()
                .startOfLine()
                .then("http")
                .maybe("s")
                .then("://")
                .maybe("www")
                .anythingBut(" ")
                .endOfLine()

        assertTrue("https://www.google.com" matches verex)
        assertTrue("http://zsmb.co" matches verex)
        assertTrue("https://www.wikipedia.org/" matches verex)
    }

    @Test
    fun emails() {
        val verex = VerEx()
                .startOfLine()
                .anything().oneOrMore()
                .then("@")
                .anything().oneOrMore()
                .then(".")
                .anything().oneOrMore()
                .endOfLine()

        assertTrue("jenny@gmail.com" matches verex)
        assertTrue("steve@gmail.com" matches verex)
        assertTrue("jimmy@yahoo.org" matches verex)
        assertTrue("foo@bar.io" matches verex)

        assertFalse("1234@5678" matches verex)
    }

    @Test
    fun phoneNumbers() {
        val verex = VerEx()
                .startOfLine()
                .beginCapture()
                .range(0 to 9)
                .or("-")
                .endCapture()
                .times(7, 14)
                .endOfLine()

        assertTrue("12345678" matches verex)
        assertTrue("123-4567" matches verex)
        assertTrue("1-800-1234-567" matches verex)
        assertTrue("123-456" matches verex)

        assertFalse("phone number" matches verex)
        assertFalse("123456" matches verex)
        assertFalse("123456789012345" matches verex)
        assertFalse("123-5-789-12345" matches verex)
    }

    @Test
    fun dates() {
        val verex = VerEx()
                .startOfLine()
                .range(1 to 2)
                .range(0 to 9).exactly(3)
                .then("-")
                .range(0 to 1)
                .range(0 to 9)
                .then("-")
                .range(0 to 3)
                .range(0 to 9)
                .endOfLine()

        assertTrue("1234-12-12" matches verex)
        assertTrue("1995-04-25" matches verex)
        assertTrue("1995-06-27" matches verex)
        assertTrue("1999-12-31" matches verex)
        assertTrue("2017-04-04" matches verex)
        assertTrue("2017-04-04" matches verex)

        assertFalse("123-12-12" matches verex)
        assertFalse("1234-20-20" matches verex)
        assertFalse("1000-10-40" matches verex)
        assertFalse("3333-12-12" matches verex)
    }

}
