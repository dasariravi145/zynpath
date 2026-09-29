package com.zynpath.game.core.puzzle.catalog

/**
 * Lightweight, zero-dependency JSON abstract syntax tree.
 */
sealed interface JsonElement {
    data class JsonObject(val entries: Map<String, JsonElement>) : JsonElement {
        fun getString(key: String): String? = (entries[key] as? JsonString)?.value
        fun getInt(key: String): Int? = (entries[key] as? JsonNumber)?.value?.toInt()
        fun getLong(key: String): Long? = (entries[key] as? JsonNumber)?.value?.toLong()
        fun getDouble(key: String): Double? = (entries[key] as? JsonNumber)?.value
        fun getBoolean(key: String): Boolean? = (entries[key] as? JsonBoolean)?.value
        fun getObject(key: String): JsonObject? = entries[key] as? JsonObject
        fun getArray(key: String): JsonArray? = entries[key] as? JsonArray
    }

    data class JsonArray(val elements: List<JsonElement>) : JsonElement
    data class JsonString(val value: String) : JsonElement
    data class JsonNumber(val value: Double) : JsonElement
    data class JsonBoolean(val value: Boolean) : JsonElement
    object JsonNull : JsonElement
}

/**
 * Pure Kotlin recursive descent JSON parser.
 *
 * Provides completely independent, deterministic JSON parsing for puzzle assets and manifests
 * without relying on Android framework stubs or external libraries.
 */
object SimpleJsonParser {

    fun parse(json: String): JsonElement {
        val reader = JsonReader(json.trim())
        val result = reader.parseValue()
        reader.skipWhitespace()
        if (reader.hasMore()) {
            throw IllegalArgumentException("Unexpected trailing characters in JSON: '${reader.peek()}'")
        }
        return result
    }

    private class JsonReader(private val src: String) {
        private var idx = 0

        fun hasMore(): Boolean = idx < src.length
        fun peek(): Char = src[idx]

        fun skipWhitespace() {
            while (hasMore() && src[idx].isWhitespace()) {
                idx++
            }
        }

        fun parseValue(): JsonElement {
            skipWhitespace()
            if (!hasMore()) throw IllegalArgumentException("Unexpected end of JSON input")
            return when (val c = peek()) {
                '{' -> parseObject()
                '[' -> parseArray()
                '"' -> parseString()
                't', 'f' -> parseBoolean()
                'n' -> parseNull()
                else -> {
                    if (c == '-' || c.isDigit()) {
                        parseNumber()
                    } else {
                        throw IllegalArgumentException("Unexpected character '$c' at position $idx")
                    }
                }
            }
        }

        private fun parseObject(): JsonElement.JsonObject {
            expect('{')
            skipWhitespace()
            val map = LinkedHashMap<String, JsonElement>()
            if (hasMore() && peek() == '}') {
                expect('}')
                return JsonElement.JsonObject(map)
            }

            while (hasMore()) {
                skipWhitespace()
                val key = parseRawString()
                skipWhitespace()
                expect(':')
                val value = parseValue()
                map[key] = value
                skipWhitespace()
                if (hasMore() && peek() == ',') {
                    idx++
                    continue
                } else if (hasMore() && peek() == '}') {
                    expect('}')
                    break
                } else {
                    throw IllegalArgumentException("Expected ',' or '}' in object at position $idx")
                }
            }
            return JsonElement.JsonObject(map)
        }

        private fun parseArray(): JsonElement.JsonArray {
            expect('[')
            skipWhitespace()
            val list = ArrayList<JsonElement>()
            if (hasMore() && peek() == ']') {
                expect(']')
                return JsonElement.JsonArray(list)
            }

            while (hasMore()) {
                val element = parseValue()
                list.add(element)
                skipWhitespace()
                if (hasMore() && peek() == ',') {
                    idx++
                    continue
                } else if (hasMore() && peek() == ']') {
                    expect(']')
                    break
                } else {
                    throw IllegalArgumentException("Expected ',' or ']' in array at position $idx")
                }
            }
            return JsonElement.JsonArray(list)
        }

        private fun parseString(): JsonElement.JsonString {
            return JsonElement.JsonString(parseRawString())
        }

        private fun parseRawString(): String {
            expect('"')
            val sb = StringBuilder()
            var escaped = false
            while (hasMore()) {
                val c = src[idx++]
                if (escaped) {
                    when (c) {
                        '"' -> sb.append('"')
                        '\\' -> sb.append('\\')
                        '/' -> sb.append('/')
                        'b' -> sb.append('\b')
                        'f' -> sb.append('\u000C')
                        'n' -> sb.append('\n')
                        'r' -> sb.append('\r')
                        't' -> sb.append('\t')
                        'u' -> {
                            if (idx + 4 <= src.length) {
                                val hex = src.substring(idx, idx + 4)
                                idx += 4
                                sb.append(hex.toInt(16).toChar())
                            } else {
                                throw IllegalArgumentException("Incomplete unicode escape at $idx")
                            }
                        }
                        else -> sb.append(c)
                    }
                    escaped = false
                } else if (c == '\\') {
                    escaped = true
                } else if (c == '"') {
                    return sb.toString()
                } else {
                    sb.append(c)
                }
            }
            throw IllegalArgumentException("Unterminated string in JSON")
        }

        private fun parseNumber(): JsonElement.JsonNumber {
            val start = idx
            if (peek() == '-') idx++
            while (hasMore() && peek().isDigit()) idx++
            if (hasMore() && peek() == '.') {
                idx++
                while (hasMore() && peek().isDigit()) idx++
            }
            if (hasMore() && (peek() == 'e' || peek() == 'E')) {
                idx++
                if (hasMore() && (peek() == '+' || peek() == '-')) idx++
                while (hasMore() && peek().isDigit()) idx++
            }
            val numStr = src.substring(start, idx)
            val num = numStr.toDoubleOrNull() ?: throw IllegalArgumentException("Invalid number '$numStr'")
            return JsonElement.JsonNumber(num)
        }

        private fun parseBoolean(): JsonElement.JsonBoolean {
            return if (src.startsWith("true", idx)) {
                idx += 4
                JsonElement.JsonBoolean(true)
            } else if (src.startsWith("false", idx)) {
                idx += 5
                JsonElement.JsonBoolean(false)
            } else {
                throw IllegalArgumentException("Expected boolean at position $idx")
            }
        }

        private fun parseNull(): JsonElement {
            if (src.startsWith("null", idx)) {
                idx += 4
                return JsonElement.JsonNull
            }
            throw IllegalArgumentException("Expected null at position $idx")
        }

        private fun expect(expected: Char) {
            skipWhitespace()
            if (!hasMore() || src[idx] != expected) {
                val actual = if (hasMore()) "'${src[idx]}'" else "EOF"
                throw IllegalArgumentException("Expected '$expected' but found $actual at position $idx")
            }
            idx++
        }
    }
}
