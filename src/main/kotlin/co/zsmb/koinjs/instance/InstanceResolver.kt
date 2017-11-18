package co.zsmb.koinjs.instance

import co.zsmb.koinjs.bean.BeanDefinition
import co.zsmb.koinjs.dsl.context.Scope
import co.zsmb.koinjs.js.logger
import kotlin.reflect.KClass

/**
 * Manage all InstanceFactory per Scope
 */
class InstanceResolver() {

    private val logger by logger<InstanceResolver>()

    val all_context = HashMap<Scope, InstanceFactory>()

    fun getInstanceFactory(scope: Scope) = all_context[scope] ?: throw IllegalStateException("couldn't resolve scope $scope")

    fun <T> resolveInstance(def: BeanDefinition<*>?): T? {
        if (def == null) return null
        else {
            val instanceFactory = getInstanceFactory(def.scope)
            return instanceFactory.resolveInstance<T>(def, def.scope)
        }
    }

    fun deleteInstance(vararg classes: KClass<*>, scope: Scope) {
        val instanceFactory = getInstanceFactory(scope)
        classes.forEach { instanceFactory.deleteInstance(it) }
    }

    fun createContext(scope: Scope) {
        if (!all_context.containsKey(scope)) {
            all_context[scope] = InstanceFactory()
            logger.log(">> Create scope $scope")
        }
    }
}