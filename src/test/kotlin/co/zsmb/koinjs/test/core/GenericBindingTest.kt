package co.zsmb.koinjs.test.core

import co.zsmb.koinjs.dsl.module.applicationContext
import co.zsmb.koinjs.standalone.StandAloneContext.startKoin
import co.zsmb.koinjs.standalone.get
import co.zsmb.koinjs.test.AutoCloseKoinTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue


class GenericBindingTest : AutoCloseKoinTest() {

    val module = applicationContext {
        bean("a") { ComponentA() as InterfaceComponent<String> }
        bean("b") { ComponentB() as InterfaceComponent<Int> }

    }

    val badModule = applicationContext {
        bean { ComponentA() as InterfaceComponent<String> }
        bean { ComponentB() as InterfaceComponent<Int> }

    }

    interface InterfaceComponent<T>
    class ComponentA : InterfaceComponent<String>
    class ComponentB : InterfaceComponent<Int>

    @Test
    fun should_inject_generic_interface_component() {
        startKoin(listOf(module))

        val a = get<InterfaceComponent<String>>("a")
        assertTrue(a is ComponentA)
    }

    @Test
    fun should_not_inject_generic_interface_component() {
        startKoin(listOf(badModule))

        val a = get<InterfaceComponent<String>>()
        // Bean has been overridden
        assertFalse(a is ComponentA)
    }
}