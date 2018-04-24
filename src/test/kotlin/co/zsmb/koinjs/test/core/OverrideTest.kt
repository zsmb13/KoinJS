package co.zsmb.koinjs.test.core

import co.zsmb.koinjs.dsl.module.applicationContext
import co.zsmb.koinjs.standalone.StandAloneContext.startKoin
import co.zsmb.koinjs.standalone.get
import co.zsmb.koinjs.test.AutoCloseKoinTest
import kotlin.test.*

class OverrideTest : AutoCloseKoinTest() {

    val sampleModule1 = applicationContext {
        bean { ComponentA() } bind MyInterface::class
        bean { ComponentA() }
    }

    val sampleModule2 = applicationContext {
        bean("A") { ComponentA() as MyInterface }
        bean("B") { ComponentB() as MyInterface }
    }

    val sampleModule3 = applicationContext {
        bean { ComponentB() as MyInterface }
        bean { ComponentA() as MyInterface }
    }

    val sampleModule4 = applicationContext {
        bean { ComponentB() as MyInterface }
        factory { ComponentA() as MyInterface }
    }

    class ComponentA : MyInterface
    class ComponentB : MyInterface
    interface MyInterface

    @Test
    fun override_provide_with_bind() {
        startKoin(listOf(sampleModule1))

        assertNotNull(get<ComponentA>())

        try {
            get<MyInterface>()
            fail()
        } catch (e: Exception) {
        }
    }

    @Test
    fun no_override_but_conflicting_def() {
        startKoin(listOf(sampleModule2))

        assertNotEquals(get<MyInterface>("A"), get<MyInterface>("B"))
    }

    @Test
    fun override_provide_with_bean() {
        startKoin(listOf(sampleModule3))

        val intf = get<MyInterface>()
        assertNotNull(intf)
        assertTrue(intf is ComponentA)

    }

    @Test
    fun override_provide_with_factory() {
        startKoin(listOf(sampleModule4))

        val intf = get<MyInterface>()
        assertNotNull(intf)
        assertTrue(intf is ComponentA)
        assertNotEquals(intf, get<MyInterface>())
    }
}