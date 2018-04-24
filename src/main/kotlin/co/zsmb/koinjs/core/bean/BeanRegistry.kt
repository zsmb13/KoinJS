package co.zsmb.koinjs.core.bean

import co.zsmb.koinjs.Koin
import co.zsmb.koinjs.core.scope.Scope
import co.zsmb.koinjs.error.NoScopeFoundException

/**
 * Bean registry
 * gather definitions of beans & communicate with instance factory to handle instances
 *
 * @author - Arnaud GIULIANI
 */
class BeanRegistry {

    val definitions = ArrayList<BeanDefinition<*>>()
    val scopes = arrayListOf<Scope>()
    val rootScope = Scope.root()

    init {
        scopes += rootScope
    }

    /**
     * Add/Replace an existing bean
     *
     * @param def : Bean definition
     */
    fun declare(def: BeanDefinition<*>, scope: Scope) {
        val definition = def.copy(scope = scope)
        val existingBean = definitions.firstOrNull { it == definition }
        val override = existingBean != null
        existingBean?.let {
            definitions.remove(existingBean)
        }
        definitions += definition
        val kw = if (override) "override" else "declare"
        Koin.logger.log("[module] $kw $definition")
    }

    /**
     * Retrieve context scope for given name
     */
    fun getScope(name: String) = scopes.firstOrNull { it.name == name }
            ?: throw NoScopeFoundException("Context scope '$name' not found")

    /**
     * Find or create context scope
     */
    fun findOrCreateScope(scopeName: String?, parentScopeName: String? = null): Scope {
        return if (scopeName == null) rootScope
        else {
            scopes.firstOrNull { it.name == scopeName } ?: createScope(scopeName, parentScopeName)
        }
    }

    /**
     * Create context scope
     */
    private fun createScope(scope: String, parentScope: String?): Scope {
        Koin.logger.log("[scope] create [$scope] with parent [$parentScope]")
        val s = Scope(scope, parent = findOrCreateScope(parentScope))
        scopes += s
        return s
    }

    /**
     * Search bean by its name, respectfully to requested type.
     */
    fun searchByName(name: String, clazz: kotlin.reflect.KClass<*>): List<BeanDefinition<*>> =
            searchDefinition { it.name == name && (it.clazz == clazz || it.types.contains(clazz)) }

    /**
     * Search for any bean definition
     */
    fun searchAll(clazz: kotlin.reflect.KClass<*>): List<BeanDefinition<*>> {
        val concreteTypes = searchDefinition { it.clazz == clazz }
        val extraBindTypes = searchDefinition { it.types.contains(clazz) }
        return (concreteTypes + extraBindTypes)
    }

    /**
     * Search definition with given filter function
     */
    private fun searchDefinition(filter: (BeanDefinition<*>) -> Boolean): List<BeanDefinition<*>> = definitions.filter(filter)

    /**
     * Get bean definitions from given scope context & child
     */
    fun getDefinitionsFromScope(name: String): List<BeanDefinition<*>> {
        val scopes = allScopesfrom(name).toSet()
        return definitions.filter { def -> definitions.first { it == def }.scope in scopes }
    }

    /**
     * Retrieve scope and child for given name
     */
    private fun allScopesfrom(name: String): List<Scope> {
        val scope = getScope(name)
        val firstChild = scopes.filter { it.parent == scope }
        return listOf(scope) + firstChild + firstChild.flatMap { allScopesfrom(it.name) }
    }

    /**
     * Clear resources
     */
    fun clear() {
        definitions.clear()
        scopes.clear()
    }

}