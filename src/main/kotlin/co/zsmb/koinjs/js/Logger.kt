package co.zsmb.koinjs.js


inline fun <reified T> logger(): Lazy<Logger> = lazy {
    val name = T::class.simpleName ?: "Unknown"
    Logger(name)
}

class Logger(val tag: String) {

    fun log(message: String) {
        println("[$tag] $message")
    }

}
