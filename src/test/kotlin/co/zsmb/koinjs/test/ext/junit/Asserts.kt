package co.zsmb.koinjs.test.ext.junit

import co.zsmb.koinjs.KoinContext
import co.zsmb.koinjs.standalone.StandAloneContext
import co.zsmb.koinjs.test.KoinTest
import co.zsmb.koinjs.test.ext.koin.*
import kotlin.reflect.KClass
import kotlin.test.assertEquals

internal fun context() = (StandAloneContext.koinContext as KoinContext)

/**
 * context definition definitionCount
 * @param definitionCount - number of definitions
 */
fun KoinTest.assertDefinitions(definitionCount: Int) {
    assertEquals(definitionCount, context().AllDefinitions().size, "applicationContext must have $definitionCount definition")
}

/**
 * definitionClazz is defined in given scope
 * @param definitionClazz - bean definition class
 * @param scopeName - scope name
 */
fun KoinTest.assertDefinedInScope(definitionClazz: KClass<*>, scopeName: String) {
    val definition = context().definition(definitionClazz)
    assertEquals(scopeName, definition?.scope?.name ?: "", "$definitionClazz must be in scope '$scopeName'")
}

/**
 * context has definition instanceCount
 * @param scopeName - scope name
 * @param instanceCount - number of instances
 */
fun KoinTest.assertContextInstances(scopeName: String, instanceCount: Int) {
    val scope = context().getScope(scopeName)
    val definitions = context().AllDefinitions().filter { it.scope == scope }.toSet()
    val instances = context().allInstances().filter { it.first in definitions }
    assertEquals(instanceCount, instances.size, "scope $scopeName must have $instanceCount instances")
}

/**
 * scope has given parent scope
 * @param scopeName - target scope name
 * @param scopeParent - parent scope name
 */
fun KoinTest.assertScopeParent(scopeName: String, scopeParent: String) {
    assertEquals(scopeParent, context().beanRegistry.getScope(scopeName).parent?.name, "Scope '$scopeName' must have parent '$scopeName'")
}

/**
 * Koin has reminaing instances
 * @param instanceCount - instances count
 */
fun KoinTest.assertRemainingInstances(instanceCount: Int) {
    assertEquals(instanceCount, context().allInstances().size, "context must have $instanceCount instances")
}

/**
 * Koin has properties count
 * @param propertyCount - properties count
 */
fun KoinTest.assertProperties(propertyCount: Int) {
    val nonKoinProps = context().allProperties().filterKeys { it != "test.koin" && it != "os.version" }
    assertEquals(propertyCount, nonKoinProps.size, "context must have $propertyCount properties")
}

/**
 * Koin has contextCount contexts
 * @param contextCount - context count
 */
fun KoinTest.assertContexts(contextCount: Int) {
    assertEquals(contextCount, context().allContext().size, "context must have $contextCount contexts")
}