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

class MultipleModuleTest : AutoCloseKoinTest() {

    class ComponentA
    class ComponentB(val componentA: ComponentA)
    class ComponentC(val componentA: ComponentA, val componentB: ComponentB)

    val SimpleModuleA = applicationContext {
        bean { ComponentA() }
    }

    val SimpleModuleB = applicationContext {
        bean { ComponentB(get()) }
    }

    val SimpleModuleC = applicationContext {
        context(name = "C") {
            bean { ComponentC(get(), get()) }
        }
    }

    @Test
    fun load_mulitple_module() {
        startKoin(listOf(SimpleModuleA, SimpleModuleB, SimpleModuleC))

        assertRemainingInstances(0)
        assertDefinitions(3)
        assertContexts(2)

        assertNotNull(get<ComponentA>())
        assertNotNull(get<ComponentB>())
        assertNotNull(get<ComponentC>())

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
        assertContexts(2)
        assertDefinedInScope(ComponentA::class, Scope.ROOT)
        assertDefinedInScope(ComponentB::class, Scope.ROOT)
        assertDefinedInScope(ComponentC::class, "C")
    }
}