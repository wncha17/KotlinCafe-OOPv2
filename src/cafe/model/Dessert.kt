package cafe.model

class Dessert(name: String, basePrice: Money, val kcal: Int) : MenuItem(name, basePrice) {
    override val category = Category.DESSERT

    override fun toString(): String = "${super.toString()} (${kcal}kcal)"
}
