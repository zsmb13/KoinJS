package co.zsmb.koinjs.core.instance

import co.zsmb.koinjs.core.bean.BeanDefinition
import co.zsmb.koinjs.dsl.context.ParameterProvider
import co.zsmb.koinjs.error.BeanInstanceCreationException

/**
 * Instance factory - handle objects creation against BeanRegistry
 * @author - Arnaud GIULIANI
 */
class InstanceFactory {

    val instances = HashMap<BeanDefinition<*>, Any>()

    /**
     * Retrieve or create bean instance
     * @return Instance / has been created
     */
    fun <T> retrieveInstance(def: BeanDefinition<*>, p: ParameterProvider): Pair<T, Boolean> {
        // Factory
        return if (def.isNotASingleton()) {
            Pair(createInstance(def, p), true)
        } else {
            // Singleton
            val found: T? = findInstance<T>(def)
            val instance: T = found ?: createInstance(def, p)
            val created = found == null
            if (created) {
                saveInstance(def, instance)
            }
            Pair(instance, created)
        }
    }

    private fun <T> saveInstance(def: BeanDefinition<*>, instance: T) {
        instances[def] = instance as Any
    }

    /**
     * Find existing instance
     */
    @Suppress("UNCHECKED_CAST")
    private fun <T> findInstance(def: BeanDefinition<*>): T? {
        val existingClass = instances.keys.firstOrNull { it == def }
        return if (existingClass != null) {
            instances[existingClass] as? T
        } else {
            null
        }
    }

    /**
     * create instance for given bean definition
     */
    @Suppress("UNCHECKED_CAST")
    private fun <T> createInstance(def: BeanDefinition<*>, p: ParameterProvider): T {
        try {
            val instance = def.definition.invoke(p) as Any
            instance as T
            return instance
        } catch (e: Throwable) {
            throw BeanInstanceCreationException("Can't create bean $def due to error :\n\t$e")
        }
    }

    /**
     * Drop all instances for definitions
     */
    fun dropAllInstances(definitions: List<BeanDefinition<*>>) {
        definitions.forEach { instances.remove(it) }
    }

    /**
     * Clear all resources
     */
    fun clear() {
        instances.clear()
    }

}