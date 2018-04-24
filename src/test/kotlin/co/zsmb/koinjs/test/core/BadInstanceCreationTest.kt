package co.zsmb.koinjs.test.core

import co.zsmb.koinjs.core.scope.Scope
import co.zsmb.koinjs.dsl.module.applicationContext
import co.zsmb.koinjs.standalone.StandAloneContext.startKoin
import co.zsmb.koinjs.standalone.get
import co.zsmb.koinjs.test.AutoCloseKoinTest
import co.zsmb.koinjs.test.dryRun
import co.zsmb.koinjs.test.ext.junit.assertContexts
import co.zsmb.koinjs.test.ext.junit.assertDefinedInScope
import co.zsmb.koinjs.test.ext.junit.assertDefinitions
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.fail

class BadInstanceCreationTest : AutoCloseKoinTest() {

    val module1 = applicationContext {
        bean { ComponentA() as MyInterface }
        bean { ComponentB() } bind MyInterface::class
    }

    val module2 = applicationContext {
        bean { ComponentA() } bind MyInterface::class
        bean { ComponentB() } bind MyInterface::class
    }

    val module3 = applicationContext {
        bean { ComponentC(get()) }
    }

    val module4 = applicationContext {
        bean { ComponentD(get()) }
        bean { ComponentE(get()) }
    }

    val module5 = applicationContext {
        bean { ComponentError() }
    }

    val module6 = applicationContext {
        bean { ComponentA() as MyInterface } bind MyInterface::class
    }

    interface MyInterface
    class ComponentA : MyInterface
    class ComponentB : MyInterface
    class ComponentC(val intf: MyInterface)
    class ComponentD(val componentE: ComponentE)
    class ComponentE(val componentD: ComponentD)
    class ComponentError() {
        init {
            error("Boum")
        }
    }

    @Test
    fun cant_create_instance_for_MyInterface_one_bind() {
        startKoin(listOf(module1))

        val b = get<ComponentB>()

        try {
            get<MyInterface>()
            fail()
        } catch (e: Exception) {
            println(e.message)
        }

        assertNotNull(b)

        assertDefinitions(2)
        assertContexts(1)
        assertDefinedInScope(MyInterface::class, Scope.ROOT)
        assertDefinedInScope(ComponentB::class, Scope.ROOT)
    }

    @Test
    fun cant_create_instance_for_MyInterface_two_binds() {
        startKoin(listOf(module2))

        val a = get<ComponentB>()
        val b = get<ComponentA>()

        try {
            get<MyInterface>()
            fail()
        } catch (e: Exception) {
            println(e.message)
        }

        assertNotNull(a)
        assertNotNull(b)

        assertDefinitions(2)
        assertContexts(1)
        assertDefinedInScope(ComponentA::class, Scope.ROOT)
        assertDefinedInScope(ComponentB::class, Scope.ROOT)
    }

    @Test
    fun missing_dependency() {
        startKoin(listOf(module3))
        try {
            dryRun()
            fail()
        } catch (e: Exception) {
            println(e.message)
        }
    }

    @Test
    fun cyclic_dependency() {
        startKoin(listOf(module4))
        try {
            dryRun()
            fail()
        } catch (e: Exception) {
            println(e.message)
        }
    }

    @Test
    fun bean_internal_error() {
        startKoin(listOf(module5))
        try {
            dryRun()
            fail()
        } catch (e: Exception) {
            println(e.message)
        }
    }

    @Test
    fun multiple_bean_definitions() {
        startKoin(listOf(module6))
        dryRun()
        assertNotNull(get<MyInterface>())
    }
}