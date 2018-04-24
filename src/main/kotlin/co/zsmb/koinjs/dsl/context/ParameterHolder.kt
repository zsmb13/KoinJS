package co.zsmb.koinjs.dsl.context

import co.zsmb.koinjs.core.parameter.Parameters
import co.zsmb.koinjs.error.MissingParameterException

/**
 * Provide a parameter
 */
interface ParameterProvider {

    /**
     * Returns the value corresponding to the given [key] - cast to type T
     */
    operator fun <T> get(key: String): T

    /**
     * Returns the value or null if key is missing, corresponding to the given [key] - cast to type T
     */
    fun <T> getOrNUll(key: String): T?

    /**
     * Real values
     */
    val values: Map<String, Any>
}

/**
 * Function to return empty dynamic parameters
 */
fun emptyParameters(): Parameters = { emptyMap() }

/**
 * ParameterHolder holder
 */
data class ParameterHolder(private val internal: Parameters = emptyParameters()) : ParameterProvider {

    override val values by lazy { internal() }

    @Suppress("UNCHECKED_CAST")
    override fun <T> getOrNUll(key: String): T? {
        return values[key] as? T
    }

    @Suppress("UNCHECKED_CAST")
    override operator fun <T> get(key: String): T =
            if (!values.containsKey(key)) throw MissingParameterException("Parameter '$key' is missing")
            else values[key] as T

}