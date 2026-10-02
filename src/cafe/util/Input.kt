package cafe.util

fun readNumber(prompt: String): Int? {
    print(prompt)
    return readln().trim().toIntOrNull()
}

fun readText(prompt: String): String {
    print(prompt)
    return readln().trim()
}

fun <T> chooseFrom(items: List<T>, prompt: String): T? {
    val no = readNumber(prompt) ?: return null
    return if (no in 1..items.size) items[no - 1] else null
}
