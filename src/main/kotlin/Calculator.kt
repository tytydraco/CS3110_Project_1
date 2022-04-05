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

    enum class Operation {
        ADD,
        SUBTRACT,
        MULTIPLY,
        DIVIDE
    }

    private fun operationString(operation: Operation): String {
        return when (operation) {
            Operation.ADD -> "+"
            Operation.SUBTRACT -> "-"
            Operation.MULTIPLY -> "*"
            Operation.DIVIDE -> "/"
        }
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

            fun singleOperation(input: String) = when (input) {
                "+" -> Operation.ADD
                "-" -> Operation.SUBTRACT
                "*" -> Operation.MULTIPLY
                "/" -> Operation.DIVIDE
                else -> throw NumberFormatException()
            }

            fun singleSymbol(input: String) = when (input) {
                "." -> Symbol.DECIMAL
                "E" -> Symbol.EXPONENT
                "e" -> Symbol.EXPONENT
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
                "e" -> Symbol.EXPONENT
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

    private fun compute(left: Double, right: Double, operation: Operation): Double {
        return when (operation) {
            Operation.ADD -> left + right
            Operation.SUBTRACT -> left - right
            Operation.MULTIPLY -> left * right
            Operation.DIVIDE -> left / right
        }
    }

    private fun evaluateOperationPart(input: String, operation: Operation): String? {
        // TODO: only pick one term to the left and one term to the right, not entire lhs and rhs
        val opStr = operationString(operation)
        val idx = input.indexOf(opStr)
        if (idx != -1) {
            val lhs = input.substring(0, idx).trim()
            val rhs = input.substring(idx + 1).trim()

            /* No other side to operate on */
            if (lhs.isEmpty() || rhs.isEmpty())
                return null

            val lastLhsTerm = lhs.split("*", "/", "+", "-").last()
            val firstRhsTerm = rhs.split("*", "/", "+", "-").first()

            val lhsEval = evaluateExpression(lastLhsTerm)
            val rhsEval = evaluateExpression(firstRhsTerm)

            val result = compute(lhsEval, rhsEval, operation)
            return input.replace("$lastLhsTerm$opStr$firstRhsTerm", result.toString())
        }

        return null
    }

    private fun getBestParentheses(input: String): String? {
        val lastOpeningBracketIdx = input.indexOfLast { it == '(' }
        if (lastOpeningBracketIdx != -1) {
            val firstClosingBracketIdx = input.indexOf(')', lastOpeningBracketIdx + 1)
            return input.substring(lastOpeningBracketIdx, firstClosingBracketIdx + 1)
        }

        return null
    }

    fun evaluateExpression(input: String): Double {
        var working = input.trim()
        println(" ---> $working")

        /*
         * Get the innermost parentheses' substring
         * Evaluate the expression within the parentheses
         * Replace the substring (including parentheses) with the result of expression
         * Evaluate again
         */
        getBestParentheses(working)?.let {
            val subWithoutBrackets = it.substring(1, it.length - 1)

            val evaluated = evaluateExpression(subWithoutBrackets)
            working = working.replace(it, evaluated.toString())

            return evaluateExpression(working)
        }

        /*
         * No parentheses
         * - Evaluate mult/div and add/sub separately
         * - If operation exists, process it, else continue trying the others
         * - If no operation exists, treat it as single digit
         * Evaluate again
         */

        val nextMultDivIdx = working.indexOfAny(charArrayOf('*', '/'))
        if (nextMultDivIdx != -1) {
            when (working[nextMultDivIdx]) {
                '*' -> evaluateOperationPart(working, Operation.MULTIPLY)?.let { return evaluateExpression(it) }
                '/' -> evaluateOperationPart(working, Operation.DIVIDE)?.let { return evaluateExpression(it) }
            }
        }

        val nextAddSubIdx = working.indexOfAny(charArrayOf('+', '-'))
        if (nextAddSubIdx != -1) {
            when (working[nextAddSubIdx]) {
                '+' -> evaluateOperationPart(working, Operation.ADD)?.let { return evaluateExpression(it) }
                '-' -> evaluateOperationPart(working, Operation.SUBTRACT)?.let { return evaluateExpression(it) }
            }
        }

        return parseDouble(working)
    }
}