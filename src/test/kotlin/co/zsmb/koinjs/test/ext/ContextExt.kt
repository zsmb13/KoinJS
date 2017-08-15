package co.zsmb.koinjs.test.ext

import co.zsmb.koinjs.KoinContext
import co.zsmb.koinjs.dsl.context.Scope
import kotlin.reflect.KClass
import kotlin.test.assertEquals

/**
 * Context Test Utils
 */

fun KoinContext.definitions() = beanRegistry.definitions

fun KoinContext.allContext() = instanceResolver.all_context

fun KoinContext.instances() = allContext().flatMap { it.value.instances.toList() }

fun KoinContext.properties() = propertyResolver.registry.properties

fun KoinContext.getScope(scope: Scope) = instanceResolver.getInstanceFactory(scope)

fun KoinContext.getScopeInstances(scope: Scope) = getScope(scope).instances

fun KoinContext.assertSizes(definitionSize: Int, instanceSize: Int) {
    assertEquals(definitions().size, definitionSize, "context definition size must be equals")
    assertEquals(instances().size, instanceSize, "context instances size must be equals")
}

fun KoinContext.assertProps(properties: Int) {
    assertEquals(properties().size, properties, "context properties size must be equals")
}

fun KoinContext.assertScopes(scopeSize: Int) {
    assertEquals(allContext().size, scopeSize, "context scope size must be equals")
}

fun KoinContext.assertScopeSize(scope: KClass<*>, size: Int) {
    assertEquals(getScopeInstances(Scope(scope)).size, size, "context scope $scope must be equals")
}

fun KoinContext.assertRootScopeSize(size: Int) {
    assertEquals(getScopeInstances(Scope.root()).size, size, "context scope ROOT must be equals")
}
