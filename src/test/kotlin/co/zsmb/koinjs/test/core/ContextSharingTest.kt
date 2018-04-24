package co.zsmb.koinjs.test.core

import co.zsmb.koinjs.dsl.module.applicationContext
import co.zsmb.koinjs.standalone.StandAloneContext.loadKoinModules
import co.zsmb.koinjs.standalone.StandAloneContext.startKoin
import co.zsmb.koinjs.standalone.get
import co.zsmb.koinjs.test.AutoCloseKoinTest
import co.zsmb.koinjs.test.ext.junit.assertContexts
import co.zsmb.koinjs.test.ext.junit.assertDefinitions
import co.zsmb.koinjs.test.ext.junit.assertRemainingInstances
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.fail

class ContextSharingTest : AutoCloseKoinTest() {
    val module1 = applicationContext {
        bean { ComponentA() }
    }

    val module2 = applicationContext {
        bean { ComponentB() }
    }

    class ComponentA
    class ComponentB

    @Test
    fun allow_context_sharing() {
        startKoin(listOf(module1))
        loadKoinModules(module2)

        assertDefinitions(2)
        assertContexts(1)

        assertNotNull(get<ComponentA>())
        assertNotNull(get<ComponentB>())
        assertRemainingInstances(2)
    }

    @Test
    fun allow_context_not_sharing() {
        startKoin(listOf(module1))
        try {
            startKoin(listOf(module2))
        } catch (e: Exception) {
            assertNotNull(e)
        }

        assertDefinitions(1)
        assertContexts(1)

        assertNotNull(get<ComponentA>())
        try {
            get<ComponentB>()
            fail()
        } catch (e: Exception) {
        }
        assertRemainingInstances(1)
    }
}