fun main(args: Array<String>) {
    val calc = Calculator()

    /*println("Start typing!")
    while (true) {
        val txt = readLine() ?: continue
        println(calc.parseDouble(txt))
    }*/
    //println(calc.evaluateExpression("5 + 3"))
    //println(calc.evaluateExpression("5 + (-1 * (4 * 2))"))
    //println(calc.evaluateExpression("(-1 * 5) - (-10)"))
    println(calc.evaluateExpression("3-3*4/3*(32-3)*(30-2)"))
}