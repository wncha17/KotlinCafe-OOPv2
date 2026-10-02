package cafe.model

data class Money(val amount: Int) : Comparable<Money> {
    operator fun plus(other: Money) = Money(amount + other.amount)
    operator fun minus(other: Money) = Money(amount - other.amount)
    operator fun times(n: Int) = Money(amount * n)
    operator fun div(n: Int) = Money(amount / n)

    override fun compareTo(other: Money): Int = amount.compareTo(other.amount)

    override fun toString(): String = "%,d원".format(amount)

    companion object {
        val ZERO = Money(0)
    }
}
