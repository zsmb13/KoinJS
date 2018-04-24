package co.zsmb.koinjs.test.core

import co.zsmb.koinjs.dsl.module.applicationContext
import co.zsmb.koinjs.standalone.StandAloneContext.startKoin
import co.zsmb.koinjs.standalone.get
import co.zsmb.koinjs.test.AutoCloseKoinTest
import co.zsmb.koinjs.test.ext.junit.assertContexts
import co.zsmb.koinjs.test.ext.junit.assertDefinitions
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.fail

class DSLProviderTest : AutoCloseKoinTest() {

    val sampleModule = applicationContext {

        bean { ComponentA() } bind MyInterface::class

        bean { ComponentB() }

        factory { ComponentC() }
    }

    val sampleModule2 = applicationContext {
        bean { ComponentA() as MyInterface }
    }

    interface MyInterface
    class ComponentA : MyInterface
    class ComponentB
    class ComponentC

    @Test
    fun can_create_use_several_providers() {
        startKoin(listOf(sampleModule))

        assertContexts(1)
        assertDefinitions(3)

        val a = get<ComponentA>()
        assertEquals(a, get<MyInterface>())

        val b = get<ComponentB>()
        assertEquals(b, get<ComponentB>())

        val c = get<ComponentB>()
        assertNotEquals<Any>(c, get<ComponentC>())
    }

    @Test
    fun can_create_use_bean_for_interface() {
        startKoin(listOf(sampleModule2))

        assertContexts(1)
        assertDefinitions(1)

        val b = get<MyInterface>()
        assertEquals(b, get<MyInterface>())

        try {
            get<ComponentA>()
            fail()
        } catch (e: Exception) {
        }
    }

}