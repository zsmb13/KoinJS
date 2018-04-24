package co.zsmb.koinjs.test.core

import co.zsmb.koinjs.dsl.module.applicationContext
import co.zsmb.koinjs.error.BeanInstanceCreationException
import co.zsmb.koinjs.standalone.StandAloneContext.startKoin
import co.zsmb.koinjs.standalone.get
import co.zsmb.koinjs.test.AutoCloseKoinTest
import co.zsmb.koinjs.test.dryRun
import co.zsmb.koinjs.test.ext.junit.assertContexts
import co.zsmb.koinjs.test.ext.junit.assertDefinitions
import co.zsmb.koinjs.test.ext.junit.assertRemainingInstances
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.fail

class DryRunTest : AutoCloseKoinTest() {

    val SimpleModule = applicationContext {
        bean { ComponentA() }
        bean { ComponentB(get()) }
    }

    val BrokenModule = applicationContext {
        bean { ComponentB(get()) }
    }

    class ComponentA()
    class ComponentB(val componentA: ComponentA)

    @Test
    fun successful_dry_run() {
        startKoin(listOf(SimpleModule))
        dryRun()

        assertDefinitions(2)
        assertRemainingInstances(2)

        assertNotNull(get<ComponentA>())
        assertNotNull(get<ComponentB>())

        assertRemainingInstances(2)
        assertContexts(1)
    }

    @Test
    fun unsuccessful_dry_run() {
        try {
            startKoin(listOf(BrokenModule))
            dryRun()
            fail()
        } catch (e: BeanInstanceCreationException) {
            println(e.message)
        }

        assertDefinitions(1)
        assertRemainingInstances(0)

        try {
            get<ComponentA>()
            fail()
        } catch (e: Exception) {
        }
        try {
            get<ComponentB>()
            fail()
        } catch (e: Exception) {
        }

        assertRemainingInstances(0)
        assertContexts(1)
    }
}