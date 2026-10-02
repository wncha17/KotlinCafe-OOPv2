package cafe.discount

import cafe.model.Money
import cafe.order.Cart

interface DiscountPolicy {
    val title: String
    fun discount(cart: Cart): Money
    fun isApplicable(cart: Cart): Boolean = discount(cart) > Money.ZERO
}

class QuantityDiscount : DiscountPolicy {
    override val title = "수량 할인"

    override fun discount(cart: Cart): Money = when {
        cart.totalQty >= 10 -> cart.totalPrice * 20 / 100
        cart.totalQty >= 5 -> cart.totalPrice * 10 / 100
        else -> Money.ZERO
    }
}

class CouponDiscount(private val coupon: Coupon) : DiscountPolicy {
    override val title = "쿠폰(${coupon.name})"

    override fun discount(cart: Cart): Money = coupon.amount
}
