package co.zsmb.koinjs.test.core

import co.zsmb.koinjs.dsl.module.applicationContext
import co.zsmb.koinjs.standalone.KoinComponent
import co.zsmb.koinjs.standalone.StandAloneContext.startKoin
import co.zsmb.koinjs.standalone.inject
import co.zsmb.koinjs.test.AutoCloseKoinTest
import co.zsmb.koinjs.test.dryRun
import co.zsmb.koinjs.test.ext.junit.assertRemainingInstances
import kotlin.test.Test
import kotlin.test.assertEquals

class ParametersTest : AutoCloseKoinTest() {

    val simpleModule1 = applicationContext {

        factory { params -> ComponentA(params[PARAM_URL]) }
    }

    val simpleModule2 = applicationContext {

        bean { params -> ComponentA(params[PARAM_URL]) }
    }

    val simpleModule3 = applicationContext {

        bean { params -> ComponentA(params.getOrNUll(PARAM_URL) ?: DEFAULT_URL) }
    }

    class ComponentA(val url: String)

    class Component1 : KoinComponent {

        init {
            println("Ctor Component1")
        }

        val compA: ComponentA by inject { mapOf(PARAM_URL to URL1) }
    }

    class Component2 : KoinComponent {

        init {
            println("Ctor Component2")
        }

        val compA: ComponentA by inject { mapOf(PARAM_URL to URL2) }
    }

    class Component3 : KoinComponent {

        init {
            println("Ctor Component3")
        }

        val compA: ComponentA by inject()
    }

    @Test
    fun should_inject_parameters_with_factory() {
        startKoin(listOf(simpleModule1))

        val c1 = Component1()
        val c2 = Component2()

        assertRemainingInstances(0)

        assertEquals(URL1, c1.compA.url)
        assertEquals(URL2, c2.compA.url)
    }

    @Test
    fun should_inject_parameters_with_bean() {
        startKoin(listOf(simpleModule2))

        val c1 = Component1()
        val c2 = Component2()

        assertRemainingInstances(0)

        assertEquals(URL1, c1.compA.url)
        assertEquals(URL1, c2.compA.url)
    }

    @Test
    fun should_dry_run_default_parameters() {
        startKoin(listOf(simpleModule1))

        dryRun { mapOf(PARAM_URL to "DEFAULT") }
    }

    @Test
    fun should_inject_default_params_with_bean() {
        startKoin(listOf(simpleModule3))

        val c1 = Component3()

        assertRemainingInstances(0)

        assertEquals(DEFAULT_URL, c1.compA.url)
    }

    companion object {
        const val PARAM_URL = "URL"
        const val URL1 = "URL_1"
        const val URL2 = "URL_2"
        const val DEFAULT_URL = "DEFAULT"
    }

}