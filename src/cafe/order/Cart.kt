package cafe.order

import cafe.model.MenuItem
import cafe.model.Money

class Cart {
    data class Line(val item: MenuItem, val qty: Int) {
        val subtotal: Money
            get() = item.price * qty
    }

    private val items = mutableListOf<Line>()

    val lines: List<Line>
        get() = items

    val isEmpty: Boolean
        get() = items.isEmpty()

    val totalQty: Int
        get() {
            var sum = 0
            for (line in items) sum += line.qty
            return sum
        }

    val totalPrice: Money
        get() {
            var total = Money.ZERO
            for (line in items) total += line.subtotal
            return total
        }

    fun add(item: MenuItem, qty: Int) {
        val index = indexOf(item)
        if (index == -1) {
            items.add(Line(item, qty))
        } else {
            val old = items[index]
            items[index] = old.copy(qty = old.qty + qty)
        }
    }

    operator fun get(index: Int): Line? = if (index in items.indices) items[index] else null

    operator fun contains(item: MenuItem): Boolean = indexOf(item) != -1

    fun removeAt(index: Int): Line = items.removeAt(index)

    fun removeAllNamed(name: String) {
        for (i in items.indices.reversed()) {
            if (items[i].item.name == name) items.removeAt(i)
        }
    }

    private fun indexOf(item: MenuItem): Int {
        for (i in items.indices) {
            if (items[i].item == item) return i
        }
        return -1
    }
}
