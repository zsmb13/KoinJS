package co.zsmb.koinjs.test.core

import co.zsmb.koinjs.dsl.module.applicationContext
import co.zsmb.koinjs.standalone.KoinComponent
import co.zsmb.koinjs.standalone.StandAloneContext.startKoin
import co.zsmb.koinjs.standalone.inject
import co.zsmb.koinjs.test.AutoCloseKoinTest
import co.zsmb.koinjs.test.ext.junit.assertRemainingInstances
import kotlin.test.Test
import kotlin.test.assertEquals

class ParametersLazyInstanceTest : AutoCloseKoinTest() {

    val module = applicationContext {
        bean { params -> ComponentA(params[URL]) }
    }

    class ComponentA(val url: String)
    class ComponentB : KoinComponent {

        lateinit var url: String

        val a: ComponentA by inject { mapOf(URL to url) }

        fun getAWithUrl(url: String) {
            this.url = url
            println("Got A : $a")
        }
    }

    @Test
    fun should_inject_parameters_with_factory() {
        startKoin(listOf(module))

        val url = "https://..."
        val b = ComponentB()

        assertRemainingInstances(0)
        b.getAWithUrl(url)

        assertRemainingInstances(1)
        assertEquals(url, b.a.url)
    }

    companion object {
        const val URL = "URL"
    }
}