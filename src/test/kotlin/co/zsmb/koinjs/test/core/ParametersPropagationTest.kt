package co.zsmb.koinjs.test.core

import co.zsmb.koinjs.dsl.module.applicationContext
import co.zsmb.koinjs.standalone.KoinComponent
import co.zsmb.koinjs.standalone.StandAloneContext.startKoin
import co.zsmb.koinjs.standalone.inject
import co.zsmb.koinjs.test.AutoCloseKoinTest
import co.zsmb.koinjs.test.ext.junit.assertRemainingInstances
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ParametersPropagationTest : AutoCloseKoinTest() {

    val simpleModule1 = applicationContext {
        bean { params -> ComponentA(params["this"]) }
        bean { params -> ComponentB(get { params.values }) }
    }

    class ComponentA(val componentC: ComponentC)
    class ComponentB(val componentA: ComponentA)
    class ComponentC : KoinComponent {

        init {
            println("Ctor Component1")
        }

        val componentB: ComponentB by inject { mapOf("this" to this) }
    }

    @Test
    fun should_inject_and_propagate_parameters() {
        startKoin(listOf(simpleModule1))

        val c = ComponentC()

        assertRemainingInstances(0)
        assertNotNull(c.componentB.componentA)

        assertRemainingInstances(2)
        assertEquals(c, c.componentB.componentA.componentC)
    }
}