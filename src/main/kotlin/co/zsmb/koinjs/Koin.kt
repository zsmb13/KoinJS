package co.zsmb.koinjs

import co.zsmb.koinjs.dsl.context.Context
import co.zsmb.koinjs.dsl.module.Module
import co.zsmb.koinjs.log.Logger
import co.zsmb.koinjs.log.PrintLogger

/**
 * Koin Context Builder
 * @author - Arnaud GIULIANI
 */
class Koin(val koinContext: KoinContext) {
    val propertyResolver = koinContext.propertyResolver
    val beanRegistry = koinContext.beanRegistry

    /**
     * Inject properties to context
     */
    fun bindAdditionalProperties(props: Map<String, Any>): Koin {
        if (props.isNotEmpty()) {
            propertyResolver.addAll(props)
        }
        return this
    }

    /**
     * load given list of module instances into current StandAlone koin context
     */
    fun build(modules: Collection<Module>): Koin {
        modules.forEach { module ->
            registerDefinitions(module())
        }

        logger.log("[modules] loaded ${beanRegistry.definitions.size} definitions")
        return this
    }

    /**
     * Register context definitions & subContexts
     */
    private fun registerDefinitions(context: Context, parentContext: Context? = null) {
        // Create or reuse getScopeForDefinition context
        val scope = beanRegistry.findOrCreateScope(context.name, parentContext?.name)

        // Add definitions
        context.definitions.forEach { definition ->
            beanRegistry.declare(definition, scope)
        }

        // Check sub contexts
        context.subContexts.forEach { subContext -> registerDefinitions(subContext, context) }
    }

    companion object {
        /**
         * Koin Logger
         */
        var logger: Logger = PrintLogger()
    }
}