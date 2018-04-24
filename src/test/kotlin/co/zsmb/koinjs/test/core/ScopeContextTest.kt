package co.zsmb.koinjs.test.core

import co.zsmb.koinjs.core.scope.Scope
import co.zsmb.koinjs.dsl.module.applicationContext
import co.zsmb.koinjs.standalone.StandAloneContext.startKoin
import co.zsmb.koinjs.standalone.get
import co.zsmb.koinjs.test.AutoCloseKoinTest
import co.zsmb.koinjs.test.ext.junit.assertContexts
import co.zsmb.koinjs.test.ext.junit.assertDefinitions
import co.zsmb.koinjs.test.ext.junit.assertRemainingInstances
import co.zsmb.koinjs.test.ext.junit.assertScopeParent
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.fail

class ScopeContextTest : AutoCloseKoinTest() {

    val FlatContextsModule = applicationContext {
        context(name = "B") {
            bean { ComponentA() }
            bean("B_B") { ComponentB(get()) }
        }

        context(name = "C") {
            bean { ComponentA() }
            bean("B_C") { ComponentB(get()) }
        }
    }

    val HierarchyContextsModule = applicationContext {
        context(name = "A") {
            bean { ComponentA() }

            context(name = "B") {
                bean { ComponentB(get()) }

                context(name = "C") {
                    bean { ComponentC(get()) }
                }
            }

        }
        context(name = "A_2") {
            bean { ComponentA() }
        }
    }

    val badVisibility = applicationContext {
        context(name = "A") {
            bean { ComponentA() }
        }

        bean { ComponentB(get()) }
    }

    class ComponentA
    class ComponentB(val componentA: ComponentA)
    class ComponentC(val componentA: ComponentA)

    @Test
    fun has_flat_visibility() {
        startKoin(listOf(FlatContextsModule))

        assertContexts(3)
        assertDefinitions(4)

        assertScopeParent("B", Scope.ROOT)
        assertScopeParent("C", Scope.ROOT)

        assertNotNull(get<ComponentB>("B_B"))
        assertNotNull(get<ComponentB>("B_C"))
        try {
            get<ComponentA>()
            fail()
        } catch (e: Exception) {
            println(e.message)
        }
        try {
            get<ComponentB>()
            fail()
        } catch (e: Exception) {
            println(e.message)
        }
    }

    @Test
    fun hierarchial_visibility() {
        startKoin(listOf(HierarchyContextsModule))

        assertContexts(5)
        assertDefinitions(4)

        assertScopeParent("A", Scope.ROOT)
        assertScopeParent("B", "A")
        assertScopeParent("C", "B")

        val c = get<ComponentC>()
        assertNotNull(c)
        val b = get<ComponentB>()
        assertNotNull(b)
        assertEquals(b.componentA, c.componentA)

        try {
            get<ComponentA>()
            fail()
        } catch (e: Exception) {
            println(e.message)
        }
    }

    @Test
    fun bad_visibility() {
        startKoin(listOf(badVisibility))

        try {
            get<ComponentB>()
            fail()
        } catch (e: Exception) {
            println(e.message)
        }

        assertRemainingInstances(0)
    }

}