package co.zsmb.koinjs.test.core

import co.zsmb.koinjs.dsl.module.applicationContext
import co.zsmb.koinjs.error.BeanInstanceCreationException
import co.zsmb.koinjs.standalone.KoinComponent
import co.zsmb.koinjs.standalone.StandAloneContext.startKoin
import co.zsmb.koinjs.standalone.inject
import co.zsmb.koinjs.test.AutoCloseKoinTest
import co.zsmb.koinjs.test.ext.junit.assertRemainingInstances
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.fail

class ParametersInstanceTest : AutoCloseKoinTest() {

    val simpleModule1 = applicationContext {

        bean { params -> ComponentA(params["this"]) }
    }

    class ComponentA(val componentB: ComponentB)
    class ComponentB : KoinComponent {

        val compA: ComponentA by inject { mapOf("this" to this) }
    }

    class ComponentC : KoinComponent {

        val compA: ComponentA by inject()
    }

    @Test
    fun should_inject_parameters_with_factory() {
        startKoin(listOf(simpleModule1))

        val b = ComponentB()

        assertRemainingInstances(0)
        assertNotNull(b.compA)

        assertRemainingInstances(1)
        assertEquals(b, b.compA.componentB)
    }

    @Test
    fun missing_parameters() {
        startKoin(listOf(simpleModule1))

        val c = ComponentC()

        assertRemainingInstances(0)
        try {
            c.compA
            fail()
        } catch (e: BeanInstanceCreationException) {
            println(e.message)
        }
    }
}