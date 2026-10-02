package cafe.model

class Drink(
    name: String,
    basePrice: Money,
    val temperature: Temperature = Temperature.HOT,
) : MenuItem(name, basePrice) {
    override val category = Category.DRINK

    override val price: Money
        get() = basePrice + temperature.extra

    override fun label(): String =
        if (temperature == Temperature.ICE) "(ICE) ${super.label()}" else super.label()

    fun withTemperature(temp: Temperature): Drink = Drink(name, basePrice, temp)
}
