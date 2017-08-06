package co.zsmb.koinjs

import co.zsmb.koinjs.bean.BeanRegistry
import co.zsmb.koinjs.dsl.context.Scope
import co.zsmb.koinjs.error.CyclicDependencyException
import co.zsmb.koinjs.error.InstanceNotFoundException
import co.zsmb.koinjs.error.MissingPropertyException
import co.zsmb.koinjs.instance.InstanceResolver
import co.zsmb.koinjs.js.Stack
import co.zsmb.koinjs.js.logger
import co.zsmb.koinjs.property.PropertyResolver
import kotlin.reflect.KClass

/**
 * Koin Application Context
 * Context from where you can get beans defines in modules
 *
 * @author Arnaud GIULIANI
 */
class KoinContext(val beanRegistry: BeanRegistry, val propertyResolver: PropertyResolver, val instanceResolver: InstanceResolver) {

    val logger by logger<KoinContext>()

    /**
     * Retrieve a bean instance
     */
    inline fun <reified T> get(): T = getOrNull<T>() ?: throw InstanceNotFoundException("No instance found for ${T::class}")

    /**
     * Safely Retrieve a bean instance (can be null)
     */
    inline fun <reified T> getOrNull(): T? {
        return resolve<T>()
    }

    /**
     * resolution stack
     */
    val resolutionStack = Stack<KClass<*>>()

    /**
     * Resolve a dependency for its bean definition
     */
    inline fun <reified T> resolve(): T? {
        val clazz = T::class
        logger.log("resolve $clazz :: $resolutionStack")

        if (resolutionStack.contains(clazz)) {
            throw CyclicDependencyException("Cyclic dependency for $clazz")
        }
        resolutionStack.add(clazz)

        val instance = instanceResolver.resolveInstance<T>(beanRegistry.searchAll(clazz))
        val head = resolutionStack.pop()
        if (head != clazz) {
            throw IllegalStateException("Calling HEAD was $head but must be $clazz")
        }
        return instance
    }

    /**
     * provide bean definition at Root scope
     * @param functional decleration
     */
    inline fun <reified T : Any> provide(noinline definition: () -> T) {
        logger.log("declare singleton $definition")
        provideDefinition(definition, Scope.root())
    }

    /**
     * provide bean definition at given class/scope
     * @param functional decleration
     */
    inline fun <reified T : Any> provide(noinline definition: () -> T, scopeClass: KClass<*>) {
        logger.log("declare singleton $definition")
        provideDefinition(definition, Scope(scopeClass))
    }

    /**
     * provide bean definition at given scope
     * @param functional decleration
     */
    inline fun <reified T : Any> provideDefinition(noinline definition: () -> T, scope: Scope) {
        instanceResolver.deleteInstance(T::class, scope = scope)
        beanRegistry.declare(definition, T::class, scope = scope)
    }

    /**
     * Clear given scope instance
     */
    fun release(vararg scopeClasses: KClass<*>) {
        scopeClasses.forEach {
            logger.log("Clear instance $it ")
            instanceResolver.getInstanceFactory(Scope(it)).clear()
        }
    }

    /**
     * Clear given Root instance
     */
    fun release() {
        logger.log("Clear instance ROOT")
        instanceResolver.getInstanceFactory(Scope.root()).clear()
    }

    /**
     * Retrieve a property
     */
    inline fun <reified T> getProperty(key: String): T = getPropertyOrNull(key) ?: throw MissingPropertyException("Could not bind property $key")

    /**
     * Retrieve safely a property
     */
    inline fun <reified T> getPropertyOrNull(key: String): T? = propertyResolver.getProperty(key)

    /**
     * Set a property
     */
    fun setProperty(key: String, value: Any) = propertyResolver.setProperty(key, value)
}