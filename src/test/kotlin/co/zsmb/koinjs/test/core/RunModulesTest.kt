package co.zsmb.koinjs.test.core

import co.zsmb.koinjs.dsl.module.applicationContext
import co.zsmb.koinjs.standalone.StandAloneContext.loadKoinModules
import co.zsmb.koinjs.standalone.StandAloneContext.startKoin
import co.zsmb.koinjs.standalone.get
import co.zsmb.koinjs.test.AutoCloseKoinTest
import co.zsmb.koinjs.test.ext.junit.assertContexts
import co.zsmb.koinjs.test.ext.junit.assertDefinitions
import co.zsmb.koinjs.test.ext.junit.assertRemainingInstances
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.fail

class RunModulesTest : AutoCloseKoinTest() {

    val moduleA = applicationContext {
        bean { ComponentA("A1") }
    }

    val moduleB = applicationContext {
        bean { ComponentB(get()) }
    }

    val moduleC = applicationContext {
        bean { ComponentA("A2") }
    }

    val moduleD = applicationContext {
        context("D") {
            bean { ComponentA("D") }
        }
    }

    class ComponentA(val name: String)
    class ComponentB(val componentA: ComponentA)
    class ComponentC(val componentA: ComponentA)

    @Test
    fun load_several_modules() {
        startKoin(listOf(moduleA))

        assertNotNull(get<ComponentA>())

        try {
            get<ComponentB>()
            fail()
        } catch (e: Exception) {
        }

        loadKoinModules(moduleB)
        assertNotNull(get<ComponentB>())
    }

    @Test
    fun load_and_override() {
        startKoin(listOf(moduleA))

        loadKoinModules(moduleC, moduleB)

        val b = get<ComponentB>()
        assertNotNull(b)
        val a2 = get<ComponentA>()
        assertEquals(a2, b.componentA)
        assertEquals("A2", a2.name)
    }

    @Test
    fun several_loads_and_override_with_contexts() {
        startKoin(listOf(moduleD))

        assertRemainingInstances(0)
        assertDefinitions(1)
        assertContexts(2)

        loadKoinModules(moduleD)

        assertRemainingInstances(0)
        assertDefinitions(1)
        assertContexts(2)

        assertNotNull(get<ComponentA>())
        assertRemainingInstances(1)

        loadKoinModules(moduleD)

        assertRemainingInstances(1)
        assertDefinitions(1)
        assertContexts(2)
    }
}