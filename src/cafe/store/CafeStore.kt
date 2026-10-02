package cafe.store

import cafe.model.Dessert
import cafe.model.Drink
import cafe.model.MenuItem
import cafe.model.Money

class CafeStore(val storeName: String) {
    val menus: List<MenuItem> by lazy {
        println("📋 메뉴를 불러옵니다...")
        listOf(
            Drink("아메리카노", Money(3000)),
            Drink("카페라떼", Money(3500)),
            Drink("바닐라라떼", Money(4000)),
            Drink("녹차라떼", Money(4000)),
            Dessert("치즈케이크", Money(5500), 350),
            Dessert("초코쿠키", Money(2500), 220),
        )
    }

    private val soldOut = mutableSetOf<String>()

    var totalSales: Money = Money.ZERO
        private set

    var orderCount: Int = 0
        private set

    fun isSoldOut(item: MenuItem): Boolean = item.name in soldOut

    internal fun toggleSoldOut(item: MenuItem): Boolean {
        return if (item.name in soldOut) {
            soldOut.remove(item.name)
            false
        } else {
            soldOut.add(item.name)
            true
        }
    }

    fun recordSale(amount: Money) {
        totalSales += amount
        orderCount++
    }

    companion object {
        const val MAX_QTY = 20
    }
}
