package cafe.discount

import cafe.model.Money

enum class Coupon(val amount: Money) {
    KOTLIN(Money(1000)),
    WELCOME(Money(2000));

    companion object {
        fun find(code: String): Coupon? {
            for (c in entries) {
                if (c.name == code.uppercase()) return c
            }
            return null
        }
    }
}
