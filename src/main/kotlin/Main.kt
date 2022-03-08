fun main(args: Array<String>) {
    Calculator().also {
        it.parseDouble("123")
        it.parseDouble("-123")
        it.parseDouble("123.456")
        it.parseDouble("123.456E2")
        it.parseDouble("-123.456E2")
        it.parseDouble("5E-2")
    }
}