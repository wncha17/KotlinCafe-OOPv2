package cafe.model

enum class Category(val label: String) {
    DRINK("음료"),
    DESSERT("디저트")
}

enum class Temperature(val label: String, val extra: Money) {
    HOT("HOT", Money.ZERO),
    ICE("ICE", Money(500))
}
