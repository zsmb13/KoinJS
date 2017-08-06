package co.zsmb.koinjs.instance

import co.zsmb.koinjs.bean.BeanDefinition
import co.zsmb.koinjs.dsl.context.Scope
import co.zsmb.koinjs.js.logger
import kotlin.reflect.KClass

/**
 * Instance factory - handle objects creation for BeanRegistry
 * @author - Arnaud GIULIANI
 */
@Suppress("UNCHECKED_CAST")
class InstanceFactory {

    val logger by logger<InstanceFactory>()

    val instances = mutableMapOf<KClass<*>, Any>()

    /**
     * Retrieve or create bean instance
     */
    fun <T> retrieveInstance(def: BeanDefinition<*>, clazz: KClass<*>, scope: Scope): T? {
        var instance = findInstance<T>(clazz)
        if (instance == null) {
            instance = createInstance(def, clazz, scope)
        }
        return instance
    }

    /**
     * Find existing instance
     */
    private fun <T> findInstance(clazz: KClass<*>): T? {
        val existingClass = instances.keys.filter { it == clazz }.firstOrNull()
        if (existingClass != null) {
            return instances[existingClass] as? T
        }
        else {
            return null
        }
    }

    /**
     * create instance for given bean definition
     */
    private fun <T> createInstance(def: BeanDefinition<*>, clazz: KClass<*>, scope: Scope): T? {
        logger.log(">> Create instance : $def")
        if (def.scope == scope) {
            try {
                val instance = def.definition.invoke() as Any
                instances[clazz] = instance
                return instance as T
            } catch(e: Exception) {
                logger.log("Couldn't get instance for $def due to error $e")
                return null
            }
        }
        else return null
    }

    fun <T> resolveInstance(def: BeanDefinition<*>, scope: Scope): T? {
        return retrieveInstance<T>(def, def.clazz, scope)
    }

    fun deleteInstance(vararg kClasses: KClass<*>) {
        kClasses.forEach { instances.remove(it) }
    }

    fun clear() {
        instances.clear()
    }
}