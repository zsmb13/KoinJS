package co.zsmb.koinjs.test.core

import co.zsmb.koinjs.core.scope.Scope
import co.zsmb.koinjs.dsl.module.applicationContext
import co.zsmb.koinjs.error.NoScopeFoundException
import co.zsmb.koinjs.standalone.StandAloneContext.startKoin
import co.zsmb.koinjs.standalone.get
import co.zsmb.koinjs.standalone.releaseContext
import co.zsmb.koinjs.test.AutoCloseKoinTest
import co.zsmb.koinjs.test.ext.junit.*
import kotlin.test.*

class ContextReleaseTest : AutoCloseKoinTest() {

    val HierarchyContextsModule = applicationContext {
        context(name = "A") {
            bean { ComponentA() }

            context(name = "B") {
                bean { ComponentB() }

                context(name = "C") {
                    bean { ComponentC() }
                }
            }
        }
    }

    class ComponentA
    class ComponentB
    class ComponentC

    @Test
    fun should_release_context_from_B() {
        startKoin(listOf(HierarchyContextsModule))

        assertContexts(4)
        assertDefinitions(3)

        assertDefinedInScope(ComponentA::class, "A")
        assertDefinedInScope(ComponentB::class, "B")
        assertDefinedInScope(ComponentC::class, "C")

        assertScopeParent("B", "A")
        assertScopeParent("C", "B")

        val a1 = get<ComponentA>()
        val b1 = get<ComponentB>()
        val c1 = get<ComponentC>()

        assertRemainingInstances(3)
        assertContextInstances("A", 1)
        assertContextInstances("B", 1)
        assertContextInstances("C", 1)

        releaseContext("B")

        assertRemainingInstances(1)
        assertContextInstances("A", 1)
        assertContextInstances("B", 0)
        assertContextInstances("C", 0)

        val a2 = get<ComponentA>()
        val b2 = get<ComponentB>()
        val c2 = get<ComponentC>()

        assertRemainingInstances(3)
        assertContextInstances("A", 1)
        assertContextInstances("B", 1)
        assertContextInstances("C", 1)

        assertEquals(a1, a2)
        assertNotEquals(b1, b2)
        assertNotEquals(c1, c2)
    }

    @Test
    fun should_release_context_from_A() {
        startKoin(listOf(HierarchyContextsModule))

        assertContexts(4)
        assertDefinitions(3)

        assertDefinedInScope(ComponentA::class, "A")
        assertDefinedInScope(ComponentB::class, "B")
        assertDefinedInScope(ComponentC::class, "C")

        assertScopeParent("B", "A")
        assertScopeParent("C", "B")

        val a1 = get<ComponentA>()
        val b1 = get<ComponentB>()
        val c1 = get<ComponentC>()

        assertRemainingInstances(3)
        assertContextInstances("A", 1)
        assertContextInstances("B", 1)
        assertContextInstances("C", 1)

        releaseContext("A")

        assertRemainingInstances(0)
        assertContextInstances("A", 0)
        assertContextInstances("B", 0)
        assertContextInstances("C", 0)

        val a2 = get<ComponentA>()
        val b2 = get<ComponentB>()
        val c2 = get<ComponentC>()

        assertRemainingInstances(3)
        assertContextInstances("A", 1)
        assertContextInstances("B", 1)
        assertContextInstances("C", 1)

        assertNotEquals(a1, a2)
        assertNotEquals(b1, b2)
        assertNotEquals(c1, c2)
    }

    @Test
    fun should_release_context_from_ROOT() {
        startKoin(listOf(HierarchyContextsModule))

        assertContexts(4)
        assertDefinitions(3)

        assertDefinedInScope(ComponentA::class, "A")
        assertDefinedInScope(ComponentB::class, "B")
        assertDefinedInScope(ComponentC::class, "C")

        assertScopeParent("B", "A")
        assertScopeParent("C", "B")

        val a1 = get<ComponentA>()
        val b1 = get<ComponentB>()
        val c1 = get<ComponentC>()

        assertRemainingInstances(3)
        assertContextInstances("A", 1)
        assertContextInstances("B", 1)
        assertContextInstances("C", 1)

        releaseContext(Scope.ROOT)

        assertRemainingInstances(0)
        assertContextInstances("A", 0)
        assertContextInstances("B", 0)
        assertContextInstances("C", 0)

        val a2 = get<ComponentA>()
        val b2 = get<ComponentB>()
        val c2 = get<ComponentC>()

        assertRemainingInstances(3)
        assertContextInstances("A", 1)
        assertContextInstances("B", 1)
        assertContextInstances("C", 1)

        assertNotEquals(a1, a2)
        assertNotEquals(b1, b2)
        assertNotEquals(c1, c2)
    }

    @Test
    fun should_release_context_from_C() {
        startKoin(listOf(HierarchyContextsModule))

        assertContexts(4)
        assertDefinitions(3)

        assertDefinedInScope(ComponentA::class, "A")
        assertDefinedInScope(ComponentB::class, "B")
        assertDefinedInScope(ComponentC::class, "C")

        assertScopeParent("B", "A")
        assertScopeParent("C", "B")

        val a1 = get<ComponentA>()
        val b1 = get<ComponentB>()
        val c1 = get<ComponentC>()

        assertRemainingInstances(3)
        assertContextInstances("A", 1)
        assertContextInstances("B", 1)
        assertContextInstances("C", 1)

        releaseContext("C")

        assertRemainingInstances(2)
        assertContextInstances("A", 1)
        assertContextInstances("B", 1)
        assertContextInstances("C", 0)

        val a2 = get<ComponentA>()
        val b2 = get<ComponentB>()
        val c2 = get<ComponentC>()

        assertRemainingInstances(3)
        assertContextInstances("A", 1)
        assertContextInstances("B", 1)
        assertContextInstances("C", 1)

        assertEquals(a1, a2)
        assertEquals(b1, b2)
        assertNotEquals(c1, c2)
    }

    @Test
    fun should_not_release_context_unknown_context() {
        startKoin(listOf(HierarchyContextsModule))

        assertContexts(4)
        assertDefinitions(3)

        assertDefinedInScope(ComponentA::class, "A")
        assertDefinedInScope(ComponentB::class, "B")
        assertDefinedInScope(ComponentC::class, "C")

        assertScopeParent("B", "A")
        assertScopeParent("C", "B")

        assertNotNull(get<ComponentA>())
        assertNotNull(get<ComponentB>())
        assertNotNull(get<ComponentC>())

        assertRemainingInstances(3)
        assertContextInstances("A", 1)
        assertContextInstances("B", 1)
        assertContextInstances("C", 1)

        try {
            releaseContext("D")
            fail()
        } catch (e: NoScopeFoundException) {
        }

        assertRemainingInstances(3)
        assertContextInstances("A", 1)
        assertContextInstances("B", 1)
        assertContextInstances("C", 1)
    }
}