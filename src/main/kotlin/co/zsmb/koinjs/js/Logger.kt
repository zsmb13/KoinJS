package co.zsmb.koinjs.js


inline fun <reified T> logger(): Lazy<Logger> = lazy {
    val name = T::class.simpleName ?: "Unknown"
    Logger(name)
}

class Logger(val tag: String, var enabled: Boolean = false) {

    fun log(message: String) {
        if(enabled) {
            println("[$tag] $message")
        }
    }

}
