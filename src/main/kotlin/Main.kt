fun main(args: Array<String>) {
    val calc = Calculator()

    println("Start typing!")
    while (true) {
        val txt = readLine() ?: continue
        println(calc.parseDouble(txt))
    }
}