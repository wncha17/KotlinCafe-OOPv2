package cafe.payment

import cafe.model.Money

sealed class Payment {
    object Card : Payment()
    data class Cash(val received: Money) : Payment()
}

sealed class PaymentResult {
    data class Success(val paid: Money, val change: Money) : PaymentResult()
    data class Failure(val reason: String) : PaymentResult()
}

fun processPayment(amount: Money, payment: Payment): PaymentResult = when (payment) {
    Payment.Card -> PaymentResult.Success(amount, Money.ZERO)
    is Payment.Cash ->
        if (payment.received >= amount) {
            PaymentResult.Success(amount, payment.received - amount)
        } else {
            PaymentResult.Failure("현금이 ${amount - payment.received} 부족합니다.")
        }
}
