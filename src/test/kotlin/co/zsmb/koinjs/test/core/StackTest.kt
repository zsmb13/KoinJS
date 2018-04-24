package co.zsmb.koinjs.test.core

import co.zsmb.koinjs.Koin
import co.zsmb.koinjs.core.scope.Scope
import co.zsmb.koinjs.dsl.module.applicationContext
import co.zsmb.koinjs.error.BeanInstanceCreationException
import co.zsmb.koinjs.error.ContextVisibilityException
import co.zsmb.koinjs.log.PrintLogger
import co.zsmb.koinjs.standalone.StandAloneContext.startKoin
import co.zsmb.koinjs.standalone.get
import co.zsmb.koinjs.test.AutoCloseKoinTest
import co.zsmb.koinjs.test.ext.junit.assertContexts
import co.zsmb.koinjs.test.ext.junit.assertDefinedInScope
import co.zsmb.koinjs.test.ext.junit.assertDefinitions
import co.zsmb.koinjs.test.ext.junit.assertScopeParent
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.fail

class StackTest : AutoCloseKoinTest() {

    val FlatContextsModule = applicationContext {

        bean { ComponentA() }

        context(name = "B") {
            bean { ComponentB(get()) }
        }

        context(name = "C") {
            bean { ComponentC(get()) }
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
        bean { ComponentD(get()) }
    }

    val NotVisibleContextsModule = applicationContext {

        bean { ComponentB(get()) }

        context(name = "A") {
            bean { ComponentA() }
        }

        context(name = "D") {
            bean { ComponentD(get()) }
        }
    }

    class ComponentA
    class ComponentB(val componentA: ComponentA)
    class ComponentC(val componentA: ComponentA)
    class ComponentD(val componentB: ComponentB)


    @Test
    fun has_flat_visibility() {
        startKoin(listOf(FlatContextsModule))

        assertContexts(3)
        assertDefinitions(3)

        assertDefinedInScope(ComponentA::class, Scope.ROOT)
        assertDefinedInScope(ComponentB::class, "B")
        assertDefinedInScope(ComponentC::class, "C")

        assertScopeParent("B", Scope.ROOT)
        assertScopeParent("C", Scope.ROOT)

        assertNotNull(get<ComponentC>())
        assertNotNull(get<ComponentB>())
        assertNotNull(get<ComponentA>())
    }

    @Test
    fun has_hierarchic_visibility() {
        startKoin(listOf(HierarchyContextsModule))

        assertNotNull(get<ComponentC>())
        assertNotNull(get<ComponentB>())
        assertNotNull(get<ComponentA>())
        try {
            get<ComponentD>()
            fail()
        } catch (e: BeanInstanceCreationException) {
            println(e.message)
        }
    }

    @Test
    fun not_good_visibility_context() {
        Koin.logger = PrintLogger()
        startKoin(listOf(NotVisibleContextsModule))

        assertNotNull(get<ComponentA>())
        try {
            get<ComponentB>()
            fail()
        } catch (e: BeanInstanceCreationException) {
            println(e.message)
        }
        try {
            get<ComponentD>()
            fail()
        } catch (e: ContextVisibilityException) {
            println(e.message)
        }
    }

}