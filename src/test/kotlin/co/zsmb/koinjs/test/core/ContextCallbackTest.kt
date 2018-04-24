package co.zsmb.koinjs.test.core

import co.zsmb.koinjs.ContextCallback
import co.zsmb.koinjs.dsl.module.applicationContext
import co.zsmb.koinjs.standalone.StandAloneContext.registerContextCallBack
import co.zsmb.koinjs.standalone.StandAloneContext.startKoin
import co.zsmb.koinjs.standalone.releaseContext
import co.zsmb.koinjs.test.AutoCloseKoinTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ContextCallbackTest : AutoCloseKoinTest() {

    val module = applicationContext {
        context(name = "A") {
            bean { ComponentA() }
        }
    }

    class ComponentA

    @Test
    fun should_release_context_from_B() {
        startKoin(listOf(module))

        var name = ""

        registerContextCallBack(object : ContextCallback {
            override fun onContextReleased(contextName: String) {
                name = contextName
            }
        })

        releaseContext("A")

        assertEquals("A", name)
    }
}