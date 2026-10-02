package cafe.app

import cafe.discount.Coupon
import cafe.discount.CouponDiscount
import cafe.discount.DiscountPolicy
import cafe.discount.QuantityDiscount
import cafe.model.Drink
import cafe.model.MenuItem
import cafe.model.Money
import cafe.model.Temperature
import cafe.order.Cart
import cafe.payment.Payment
import cafe.payment.PaymentResult
import cafe.payment.processPayment
import cafe.store.CafeStore
import cafe.util.chooseFrom
import cafe.util.readNumber
import cafe.util.readText

class Kiosk(private val store: CafeStore) {
    private lateinit var cart: Cart
    private val quantityDiscount = QuantityDiscount()

    fun start() {
        cart = Cart()
        println("☕ ${store.storeName}에 오신 것을 환영합니다!")
        while (true) {
            printMainMenu()
            when (readNumber("선택 > ")) {
                1 -> addToCart()
                2 -> showCart()
                3 -> removeFromCart()
                4 -> checkout()
                5 -> manageSoldOut()
                6 -> showSales()
                0 -> {
                    println("이용해 주셔서 감사합니다. 👋")
                    break
                }
                null -> println("⚠️ 숫자를 입력해 주세요.")
                else -> println("⚠️ 0~6 사이 번호를 입력해 주세요.")
            }
        }
    }

    // ===== 화면 =====
    private fun printMainMenu() {
        println()
        println("=".repeat(30))
        println("1. 메뉴 담기")
        println("2. 장바구니 보기")
        println("3. 장바구니에서 빼기")
        println("4. 결제하기")
        println("5. 품절 관리 (관리자)")
        println("6. 오늘 매출 보기")
        println("0. 종료")
        println("=".repeat(30))
    }

    private fun printMenuBoard() {
        println("----- MENU -----")
        store.menus.forEachIndexed { i, item ->
            val status = if (store.isSoldOut(item)) " (품절)" else ""
            println("${i + 1}. $item$status")
        }
    }

    // ===== 장바구니 =====
    private fun addToCart() {
        printMenuBoard()
        val base = chooseFrom(store.menus, "메뉴 번호 > ")
        if (base == null) {
            println("⚠️ 없는 메뉴 번호입니다.")
            return
        }
        if (store.isSoldOut(base)) {
            println("⚠️ ${base.name}은(는) 품절입니다.")
            return
        }
        val item: MenuItem = (base as? Drink)?.let { askTemperature(it) } ?: base
        val qty = readNumber("수량 > ")
        if (qty == null || qty !in 1..CafeStore.MAX_QTY) {
            println("⚠️ 수량은 1~${CafeStore.MAX_QTY} 사이여야 합니다.")
            return
        }
        cart.add(item, qty)
        println("✅ ${item.label()} ${qty}개를 담았습니다.")
    }

    private fun askTemperature(drink: Drink): Drink {
        val temps = Temperature.entries
        temps.forEachIndexed { i, t -> print("${i + 1}. ${t.label}(+${t.extra})  ") }
        println()
        val temp = chooseFrom(temps, "온도 > ") ?: Temperature.HOT
        return drink.withTemperature(temp)
    }

    private fun showCart() {
        if (cart.isEmpty) {
            println("🛒 장바구니가 비어 있습니다.")
            return
        }
        println("----- 장바구니 -----")
        for ((item, qty) in cart.lines) {
            println("${item.label()} x$qty = ${item.price * qty}")
        }
        println("합계: ${cart.totalPrice}")
    }

    private fun removeFromCart() {
        if (cart.isEmpty) {
            println("🛒 장바구니가 비어 있습니다.")
            return
        }
        cart.lines.forEachIndexed { i, line ->
            println("${i + 1}. ${line.item.label()} x${line.qty}")
        }
        val no = readNumber("뺄 메뉴 번호 > ") ?: 0
        val line = cart[no - 1]
        if (line == null) {
            println("⚠️ 잘못된 번호입니다.")
            return
        }
        cart.removeAt(no - 1)
        println("🗑 ${line.item.label()}을(를) 뺐습니다.")
    }

    // ===== 결제 =====
    private fun checkout() {
        if (cart.isEmpty) {
            println("🛒 담긴 메뉴가 없습니다.")
            return
        }
        showCart()

        val policies = mutableListOf<DiscountPolicy>(quantityDiscount)
        val code = readText("쿠폰 코드 (없으면 Enter) > ")
        if (code.isNotEmpty()) {
            val coupon = Coupon.find(code)
            if (coupon == null) {
                println("⚠️ 유효하지 않은 쿠폰입니다.")
            } else {
                policies.add(CouponDiscount(coupon))
            }
        }

        val total = cart.totalPrice
        var discountSum = Money.ZERO
        for (p in policies) discountSum += p.discount(cart)
        var payAmount = total - discountSum
        if (payAmount < Money.ZERO) payAmount = Money.ZERO
        println("결제 금액: $payAmount")

        val payment = askPayment()
        if (payment == null) {
            println("⚠️ 결제를 취소했습니다.")
            return
        }

        when (val result = processPayment(payAmount, payment)) {
            is PaymentResult.Success -> {
                printReceipt(total, policies, result)
                store.recordSale(result.paid)
                cart = Cart()
            }
            is PaymentResult.Failure -> println("❌ 결제 실패: ${result.reason}")
        }
    }

    private fun askPayment(): Payment? {
        return when (readNumber("결제 수단 (1. 카드  2. 현금) > ")) {
            1 -> Payment.Card
            2 -> {
                val received = readNumber("받은 금액 > ") ?: return null
                Payment.Cash(Money(received))
            }
            else -> null
        }
    }

    private fun printReceipt(total: Money, policies: List<DiscountPolicy>, result: PaymentResult.Success) {
        val line = "-".repeat(30)
        println()
        println("=".repeat(30))
        println("         🧾 영 수 증")
        println("=".repeat(30))
        for ((item, qty) in cart.lines) {
            row("${item.label()} x$qty", "${item.price * qty}")
        }
        println(line)
        row("합계", "$total")
        for (p in policies) {
            if (p.isApplicable(cart)) row(p.title, "-${p.discount(cart)}")
        }
        println(line)
        row("결제 금액", "${result.paid}")
        if (result.change > Money.ZERO) row("거스름돈", "${result.change}")
        println("오늘 ${store.orderCount + 1}번째 손님입니다. 감사합니다!")
    }

    private fun row(left: String, right: String) {
        println(left.padEnd(18) + right.padStart(12))
    }

    // ===== 관리자 =====
    private fun manageSoldOut() {
        printMenuBoard()
        val item = chooseFrom(store.menus, "품절 전환할 메뉴 번호 (0: 취소) > ")
        if (item == null) {
            println("취소했습니다.")
            return
        }
        if (store.toggleSoldOut(item)) {
            cart.removeAllNamed(item.name)
            println("⛔ ${item.name} 품절 처리")
        } else {
            println("✅ ${item.name} 판매 재개")
        }
    }

    private fun showSales() {
        if (store.orderCount == 0) {
            println("아직 주문이 없습니다.")
            return
        }
        println("주문 수: ${store.orderCount}건")
        println("총 매출: ${store.totalSales}")
        println("평균 결제 금액: ${store.totalSales / store.orderCount}")
    }
}
