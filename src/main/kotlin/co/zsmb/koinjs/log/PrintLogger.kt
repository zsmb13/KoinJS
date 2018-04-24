package co.zsmb.koinjs.log

/**
 * Logger that print on system.out
 */
class PrintLogger : Logger {
    override fun debug(msg: String) {
        println("(KOIN) :: [DEBUG] :: $msg")
    }

    override fun log(msg: String) {
        println("(KOIN) :: [INFO] :: $msg")
    }

    override fun err(msg: String) {
        println("(KOIN) :: [ERROR] :: $msg")
    }
}