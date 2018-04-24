package co.zsmb.koinjs.error

/**
 * Koin has already been started
 */
class AlreadyStartedException(msg: String = "Koin has already been started!") : Exception(msg)