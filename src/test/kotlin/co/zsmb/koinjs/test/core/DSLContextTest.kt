package co.zsmb.koinjs.test.core

import co.zsmb.koinjs.core.scope.Scope
import co.zsmb.koinjs.dsl.module.applicationContext
import co.zsmb.koinjs.standalone.StandAloneContext.startKoin
import co.zsmb.koinjs.test.AutoCloseKoinTest
import co.zsmb.koinjs.test.ext.junit.assertContexts
import co.zsmb.koinjs.test.ext.junit.assertDefinedInScope
import co.zsmb.koinjs.test.ext.junit.assertDefinitions
import co.zsmb.koinjs.test.ext.junit.assertScopeParent
import kotlin.test.Test

class DSLContextTest : AutoCloseKoinTest() {

    val FlatContextsModule = applicationContext {

        bean { ComponentA() }

        context(name = "B") {
            bean { ComponentB() }
        }

        context(name = "C") {
            bean { ComponentC() }
        }
    }

    val HierarchyContextsModule = applicationContext {
        context(name = "A") {
            bean { ComponentA() }

            context(name = "B") {
                bean { ComponentB() }

                context(name = "C") {
                    bean { ComponentC() }
                }
            }
        }
    }

    class ComponentA
    class ComponentB
    class ComponentC

    @Test
    fun can_create_flat_contexts() {
        startKoin(listOf(FlatContextsModule))

        assertContexts(3)
        assertDefinitions(3)

        assertDefinedInScope(ComponentA::class, Scope.ROOT)
        assertDefinedInScope(ComponentB::class, "B")
        assertDefinedInScope(ComponentC::class, "C")

        assertScopeParent("B", Scope.ROOT)
        assertScopeParent("C", Scope.ROOT)
    }

    @Test
    fun can_create_hierarchic_context() {
        startKoin(listOf(HierarchyContextsModule))

        assertContexts(4)
        assertDefinitions(3)

        assertDefinedInScope(ComponentA::class, "A")
        assertDefinedInScope(ComponentB::class, "B")
        assertDefinedInScope(ComponentC::class, "C")

        assertScopeParent("A", Scope.ROOT)
        assertScopeParent("B", "A")
        assertScopeParent("C", "B")
    }

}