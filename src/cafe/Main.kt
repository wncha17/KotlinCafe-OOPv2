package cafe

import cafe.app.Kiosk
import cafe.store.CafeStore

fun main() {
    val store = CafeStore("코틀린 카페")
    val kiosk = Kiosk(store)
    kiosk.start()
}
