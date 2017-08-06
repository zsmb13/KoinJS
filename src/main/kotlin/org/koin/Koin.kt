package org.koin

import org.koin.bean.BeanRegistry
import org.koin.dsl.context.Scope
import org.koin.dsl.module.Module
import org.koin.instance.InstanceResolver
import org.koin.js.logger
import org.koin.property.PropertyResolver

/**
 * Koin Context Builder
 * @author - Arnaud GIULIANI
 */
class Koin {

    val logger by logger<Koin>()

    val beanRegistry = BeanRegistry()
    val propertyResolver = PropertyResolver()
    val instanceResolver = InstanceResolver()

    init {
        logger.log("(-) Koin Started ! (-)")
        instanceResolver.createContext(Scope.root())
    }

    /**
     * Inject properties to context
     */
    fun properties(props: Map<String, Any>): Koin {
        logger.log("load properties $props ...")
        propertyResolver.addAll(props)
        return this
    }

    /**
     * load given module instances into current koin context
     */
    fun <T : Module> build(vararg modules: T): KoinContext {
        logger.log("load module $modules ...")

        val koinContext = KoinContext(beanRegistry, propertyResolver, instanceResolver)
        modules.forEach {
            it.koinContet = koinContext
            val ctx = it.context()
            val scope = ctx.contextScope
            if (scope != null) {
                logger.log("preparing scope $scope")
                instanceResolver.createContext(scope)
            }
            ctx.provided.forEach { beanRegistry.declare<Any>(it) }
        }

        return koinContext
    }

    /**
     * load directly Koin context with no modules
     */
    fun build(): KoinContext = KoinContext(beanRegistry, propertyResolver, instanceResolver)
}
