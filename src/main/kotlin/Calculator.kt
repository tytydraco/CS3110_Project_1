import kotlin.math.pow

class Calculator {
    enum class Symbol {
        INTEGER,
        SIGN,
        EXPONENT,
        DECIMAL
    }

    enum class Sign {
        POSITIVE,
        NEGATIVE
    }

    class Parse {
        companion object {
            /**
             * Parse anything in range [0-9]
             */
            fun singleInt(input: String) = when (input) {
                "0" -> 0
                "1" -> 1
                "2" -> 2
                "3" -> 3
                "4" -> 4
                "5" -> 5
                "6" -> 6
                "7" -> 7
                "8" -> 8
                "9" -> 9
                else -> throw NumberFormatException()
            }

            fun singleSign(input: String) = when (input) {
                "-" -> Sign.NEGATIVE
                "+" -> Sign.POSITIVE
                else -> throw NumberFormatException()
            }

            fun singleSymbol(input: String) = when (input) {
                "." -> Symbol.DECIMAL
                "E" -> Symbol.EXPONENT
                else -> throw NumberFormatException()
            }

            fun typeOf(input: String) = when (input) {
                "0" -> Symbol.INTEGER
                "1" -> Symbol.INTEGER
                "2" -> Symbol.INTEGER
                "3" -> Symbol.INTEGER
                "4" -> Symbol.INTEGER
                "5" -> Symbol.INTEGER
                "6" -> Symbol.INTEGER
                "7" -> Symbol.INTEGER
                "8" -> Symbol.INTEGER
                "9" -> Symbol.INTEGER
                "-" -> Symbol.SIGN
                "+" -> Symbol.SIGN
                "." -> Symbol.DECIMAL
                "E" -> Symbol.EXPONENT
                else -> throw NumberFormatException()
            }
        }
    }

    private fun intPartsIntoPreDecimal(parts: List<Int>): Int {
        var ret = 0
        parts.reversed().forEachIndexed { i, part ->
            ret += part * 10.0.pow(i).toInt()
        }
        return ret
    }

    private fun intPartsIntoPostDecimal(parts: List<Int>): Double {
        var ret = 0.0
        parts.forEachIndexed { i, part ->
            ret += part * 10.0.pow(-(i + 1))
        }
        return ret
    }

    fun parseDouble(input: String): Double {
        val preDecimalParts = mutableListOf<Int>()
        val postDecimalParts = mutableListOf<Int>()
        val exponentParts = mutableListOf<Int>()

        var preDecimal = 0
        var postDecimal = 0.0
        var exponent = 1

        var signMultiplier = 1
        var exponentSignMultiplier = 1
        var result = 0.0

        /*
         * Must be able to recognize:
         * - Literal int part
         * - Decimal part
         * - Exponent part
         *
         * Example: -334.2E2
         */
        var inExponentMode = false
        var inDecimalMode = false
        for (_char in input) {
            val char = _char.toString()
            when (Parse.typeOf(char)) {
                Symbol.INTEGER -> {
                    val parsedInt = Parse.singleInt(char)
                    if (inExponentMode)
                        exponentParts.add(parsedInt)
                    else if (inDecimalMode)
                        postDecimalParts.add(parsedInt)
                    else
                        preDecimalParts.add(parsedInt)
                }

                Symbol.SIGN -> {
                    val sign = when (Parse.singleSign(char)) {
                        Sign.NEGATIVE -> -1
                        Sign.POSITIVE -> 1
                    }

                    if (inExponentMode)
                        exponentSignMultiplier = sign
                    else
                        signMultiplier = sign
                }

                Symbol.EXPONENT -> {
                    inDecimalMode = false
                    inExponentMode = true
                }

                Symbol.DECIMAL -> {
                    inDecimalMode = true
                    inExponentMode = false
                }
            }
        }

        preDecimal = intPartsIntoPreDecimal(preDecimalParts)
        postDecimal = intPartsIntoPostDecimal(postDecimalParts)

        if (exponentParts.isNotEmpty())
            exponent = intPartsIntoPreDecimal(exponentParts)
        exponent *= exponentSignMultiplier

        result = preDecimal + postDecimal
        result = result.pow(exponent)
        result *= signMultiplier

        return result
    }
}