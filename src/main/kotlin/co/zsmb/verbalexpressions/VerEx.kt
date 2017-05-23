package co.zsmb.verbalexpressions

class VerEx {

    companion object {

        private val symbols = mapOf(
                'd' to java.util.regex.Pattern.UNIX_LINES,
                'i' to java.util.regex.Pattern.CASE_INSENSITIVE,
                'x' to java.util.regex.Pattern.COMMENTS,
                'm' to java.util.regex.Pattern.MULTILINE,
                's' to java.util.regex.Pattern.DOTALL,
                'u' to java.util.regex.Pattern.UNICODE_CASE,
                'U' to java.util.regex.Pattern.UNICODE_CHARACTER_CLASS
        )

    }

    private val pattern: java.util.regex.Pattern
        get() {
            val compile = java.util.regex.Pattern.compile("$prefixes$source$suffixes", modifiers)
            println(compile)
            return compile
        }

    private var prefixes = StringBuilder()
    private var source = StringBuilder()
    private var suffixes = StringBuilder()
    private var modifiers = java.util.regex.Pattern.MULTILINE

    //// TESTS ////

    fun testExact(toTest: String?) = if(toTest == null) false else pattern.matcher(toTest).find()

    //// COMPOSITION ////

    fun add(str: String): co.zsmb.verbalexpressions.VerEx {
        source.append(str)
        return this
    }

    fun startOfLine(enabled: Boolean = true): co.zsmb.verbalexpressions.VerEx {
        prefixes = StringBuilder(if (enabled) "^" else "")
        return this
    }

    fun endOfLine(enabled: Boolean = true): co.zsmb.verbalexpressions.VerEx {
        suffixes = StringBuilder(if (enabled) "$" else "")
        return this
    }

    fun find(str: String) = then(str)

    fun then(str: String) = add("(?:${sanitize(str)})")

    fun maybe(str: String) = add("(?:${sanitize(str)})?")

    fun anything() = add("(?:.*)")

    fun anythingBut(str: String) = add("(?:[^${sanitize(str)}]*)")

    fun something() = add("(?:.+)")

    fun somethingBut(str: String) = add("(?:[^${sanitize(str)}]+)")

    fun lineBreak() = add("""(?:(?:\n)|(?:\r\n)""")

    fun br() = lineBreak()

    fun tab() = add("""\t""")

    fun word() = add("""\w+""")

    fun anyOf(str: String) = add("(?:[${sanitize(str)}])")

    fun any(str: String) = anyOf(str)

    fun withAnyCase(enabled: Boolean = true) = updateModifier('i', enabled)

    fun searchOneLine(enabled: Boolean = true) = updateModifier('m', !enabled)

    fun or(str: String): co.zsmb.verbalexpressions.VerEx {
        prefixes = StringBuilder().append("(").append(prefixes)
        source.append(")|(").append(str).append(")").append(suffixes)
        suffixes = StringBuilder()

        return this
    }

    fun multiple(str: String, min: Int? = null, max: Int? = null): co.zsmb.verbalexpressions.VerEx {
        then(str)
        return count(min, max)
    }

    fun count(min: Int? = null, max: Int? = null): co.zsmb.verbalexpressions.VerEx {
        if(min != null && max != null && min > max) {
            throw IllegalArgumentException("Min count ($min) can't be less than max count ($max).")
        }
        return add("{${min ?: "1"},${max ?: ""}}")
    }

    fun atLeast(min: Int) = count(min)

    fun replace(source: String, replacement: String): String {
        return pattern.matcher(source).replaceAll(replacement)
    }

    fun range(vararg args: Pair<Any, Any>): co.zsmb.verbalexpressions.VerEx {
        return add(args.joinToString(prefix = "[", postfix = "]", separator = "") { "${it.first}-${it.second}" })
    }

    fun beginCapture() = add("(")

    fun endCapture() = add(")")

    fun whiteSpace() = add("""\s""")

    fun oneOrMore() = add("+")

    fun zeroOrMore() = add("*")

    //// HELPERS ////

    private fun sanitize(str: String): String {
        return str.replace("[\\W]".toRegex(), "\\\\$0")
    }

    private fun updateModifier(modifier: Char, enabled: Boolean) =
            if (enabled) addModifier(modifier)
            else removeModifier(modifier)

    private fun addModifier(modifier: Char): co.zsmb.verbalexpressions.VerEx {
        co.zsmb.verbalexpressions.VerEx.Companion.symbols[modifier]?.let {
            modifiers = modifiers or it
        }
        return this
    }

    private fun removeModifier(modifier: Char): co.zsmb.verbalexpressions.VerEx {
        co.zsmb.verbalexpressions.VerEx.Companion.symbols[modifier]?.let {
            modifiers = modifiers and it.inv()
        }
        return this
    }


}