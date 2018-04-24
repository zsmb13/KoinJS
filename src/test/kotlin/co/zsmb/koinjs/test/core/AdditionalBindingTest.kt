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
import kotlin.test.fail


class AdditionalBindingTest : AutoCloseKoinTest() {

    val BoundModule = applicationContext {
        bean { ComponentA() } bind InterfaceComponent::class
    }

    val NotBoundModule = applicationContext {
        bean { ComponentA() }
    }

    val GenericBoundModule = applicationContext {
        bean { ComponentB() } bind OtherInterfaceComponent::class
    }

    val TwoBoundModule = applicationContext {
        bean { ComponentB() } bind OtherInterfaceComponent::class
        bean { ComponentC() } bind OtherInterfaceComponent::class
    }

    class ComponentA : InterfaceComponent
    interface InterfaceComponent

    class ComponentB : OtherInterfaceComponent<String> {
        override fun get() = "HELLO"

    }

    class ComponentC : OtherInterfaceComponent<String> {
        override fun get() = "HELLO_C"

    }

    interface OtherInterfaceComponent<T> {
        fun get(): T
    }

    @Test
    fun same_instance_for_provided_bound_component() {
        startKoin(listOf(BoundModule))

        val a = get<ComponentA>()
        val intf = get<InterfaceComponent>()

        assertNotNull(a)
        assertNotNull(intf)
        assertEquals(a, intf)

        assertRemainingInstances(1)
        assertDefinitions(1)
        assertContexts(1)
        assertDefinedInScope(ComponentA::class, Scope.ROOT)
    }

    @Test
    fun should_not_bound_component() {
        startKoin(listOf(NotBoundModule))

        val a = get<ComponentA>()

        try {
            get<InterfaceComponent>()
            fail()
        } catch (e: Exception) {
        }

        assertNotNull(a)

        assertRemainingInstances(1)
        assertDefinitions(1)
        assertContexts(1)
        assertDefinedInScope(ComponentA::class, Scope.ROOT)
    }

    @Test
    fun should_bind_generic_component() {
        startKoin(listOf(GenericBoundModule))

        val b = get<ComponentB>()
        val intf = get<OtherInterfaceComponent<String>>()

        assertNotNull(b)
        assertNotNull(intf)
        assertEquals("HELLO", intf.get())

        assertRemainingInstances(1)
        assertDefinitions(1)
        assertContexts(1)
        assertDefinedInScope(ComponentB::class, Scope.ROOT)
    }

    @Test
    fun should_not_bind_generic_component() {
        startKoin(listOf(TwoBoundModule))

        try {
            get<OtherInterfaceComponent<String>>()
            fail()
        } catch (e: Exception) {
        }

        assertRemainingInstances(0)
        assertDefinitions(2)
        assertContexts(1)
        assertDefinedInScope(ComponentB::class, Scope.ROOT)
        assertDefinedInScope(ComponentC::class, Scope.ROOT)
    }

}