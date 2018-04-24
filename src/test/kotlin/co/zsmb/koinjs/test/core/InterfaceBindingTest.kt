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
import kotlin.test.assertNotNull
import kotlin.test.fail


class InterfaceBindingTest : AutoCloseKoinTest() {

    val InterfacesModule = applicationContext {
        bean { ComponentA() as InterfaceComponent }
        bean("B") { ComponentB() as OtherInterfaceComponent }
        bean("C") { ComponentC() as OtherInterfaceComponent }
    }

    interface InterfaceComponent
    class ComponentA : InterfaceComponent

    interface OtherInterfaceComponent
    class ComponentB : OtherInterfaceComponent
    class ComponentC : OtherInterfaceComponent

    @Test
    fun should_get_from_interface_but_not_implementation() {
        startKoin(listOf(InterfacesModule))

        val a = get<InterfaceComponent>()
        assertNotNull(a)

        try {
            get<ComponentA>()
            fail()
        } catch (e: Exception) {
        }

        assertRemainingInstances(1)
        assertDefinitions(3)
        assertContexts(1)
        assertDefinedInScope(InterfaceComponent::class, Scope.ROOT)
    }

    @Test
    fun should_get_two_components_from_interface_only() {
        startKoin(listOf(InterfacesModule))

        val b = get<OtherInterfaceComponent>("B")
        val c = get<OtherInterfaceComponent>("C")
        assertNotNull(b)
        assertNotNull(c)

        try {
            get<ComponentB>()
            fail()
        } catch (e: Exception) {
        }
        try {
            get<ComponentC>()
            fail()
        } catch (e: Exception) {
        }
        try {
            get<OtherInterfaceComponent>()
            fail()
        } catch (e: Exception) {
        }

        assertRemainingInstances(2)
        assertDefinitions(3)
        assertContexts(1)
        assertDefinedInScope(OtherInterfaceComponent::class, Scope.ROOT)
    }
}