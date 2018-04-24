package co.zsmb.koinjs.test.core

import co.zsmb.koinjs.dsl.module.applicationContext
import co.zsmb.koinjs.standalone.StandAloneContext.startKoin
import co.zsmb.koinjs.standalone.get
import co.zsmb.koinjs.test.AutoCloseKoinTest
import kotlin.test.Test
import kotlin.test.fail


class ErrorLoggingTest : AutoCloseKoinTest() {

    val module = applicationContext {
        bean { ComponentA() }
        bean { ComponentB(get()) }
    }

    val cyclicModule = applicationContext {
        bean { ComponentAA(get()) }
        bean { ComponentAB(get()) }
    }

    class ComponentA {
        init {
            error("Boom !")
        }
    }

    class ComponentB(val a: ComponentA)

    class ComponentAA(val componentAB: ComponentAB)
    class ComponentAB(val componentAA: ComponentAA)

    @Test
    fun should_not_inject_generic_interface_component() {
        startKoin(listOf(module))

        try {
            get<ComponentA>()
            fail()
        } catch (e: Exception) {
            println(e.message)
        }
    }

    @Test
    fun should_not_inject_generic_interface_component_linked_dependency() {
        startKoin(listOf(module))

        try {
            get<ComponentB>()
            fail()
        } catch (e: Exception) {
            println(e.message)
        }
    }

    @Test
    fun should_not_inject_cyclic_deps() {
        startKoin(listOf(cyclicModule))

        try {
            get<ComponentAA>()
            fail()
        } catch (e: Exception) {
            println(e.message)
        }
    }
}