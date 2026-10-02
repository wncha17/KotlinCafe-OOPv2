package cafe.model

abstract class MenuItem(val name: String, protected val basePrice: Money) {
    init {
        require(basePrice > Money.ZERO) { "가격은 0보다 커야 합니다: $name" }
    }

    abstract val category: Category

    open val price: Money
        get() = basePrice

    open fun label(): String = name

    override fun toString(): String = "[${category.label}] ${label()} - $price"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MenuItem) return false
        return label() == other.label()
    }

    override fun hashCode(): Int = label().hashCode()
}
