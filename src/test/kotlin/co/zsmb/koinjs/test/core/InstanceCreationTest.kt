package co.zsmb.koinjs.test.core

import co.zsmb.koinjs.core.scope.Scope
import co.zsmb.koinjs.dsl.module.applicationContext
import co.zsmb.koinjs.standalone.StandAloneContext.startKoin
import co.zsmb.koinjs.standalone.get
import co.zsmb.koinjs.test.AutoCloseKoinTest
import co.zsmb.koinjs.test.ext.junit.assertContexts
import co.zsmb.koinjs.test.ext.junit.assertDefinedInScope
import co.zsmb.koinjs.test.ext.junit.assertDefinitions
import co.zsmb.koinjs.test.ext.junit.assertRemainingInstances
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class InstanceCreationTest : AutoCloseKoinTest() {

    val FlatModule = applicationContext {
        bean { ComponentA() }
        bean { ComponentB(get()) }
        bean { ComponentC(get(), get()) }
    }

    val HierarchicModule = applicationContext {
        bean { ComponentA() }

        context("B") {
            bean { ComponentB(get()) }

            context("C") {
                bean { ComponentC(get(), get()) }
            }
        }
    }

    class ComponentA
    class ComponentB(val componentA: ComponentA)
    class ComponentC(val componentB: ComponentB, val componentA: ComponentA)

    @Test
    fun load_and_create_instances_for_flat_module() {
        startKoin(listOf(FlatModule))

        val a = get<ComponentA>()
        val b = get<ComponentB>()
        val c = get<ComponentC>()

        assertNotNull(a)
        assertNotNull(b)
        assertNotNull(c)
        assertEquals(a, b.componentA)
        assertEquals(a, c.componentA)
        assertEquals(b, c.componentB)

        assertRemainingInstances(3)
        assertDefinitions(3)
        assertContexts(1)
        assertDefinedInScope(ComponentA::class, Scope.ROOT)
        assertDefinedInScope(ComponentB::class, Scope.ROOT)
        assertDefinedInScope(ComponentC::class, Scope.ROOT)
    }

    @Test
    fun load_and_create_instances_for_hierarchic_context() {
        startKoin(listOf(HierarchicModule))

        val a = get<ComponentA>()
        val b = get<ComponentB>()
        val c = get<ComponentC>()

        assertNotNull(a)
        assertNotNull(b)
        assertNotNull(c)
        assertEquals(a, b.componentA)
        assertEquals(a, c.componentA)
        assertEquals(b, c.componentB)

        assertRemainingInstances(3)
        assertDefinitions(3)
        assertContexts(3)
        assertDefinedInScope(ComponentA::class, Scope.ROOT)
        assertDefinedInScope(ComponentB::class, "B")
        assertDefinedInScope(ComponentC::class, "C")
    }

}