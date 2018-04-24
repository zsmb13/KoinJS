package co.zsmb.koinjs.test.core

import co.zsmb.koinjs.dsl.module.applicationContext
import co.zsmb.koinjs.error.BeanInstanceCreationException
import co.zsmb.koinjs.standalone.StandAloneContext.startKoin
import co.zsmb.koinjs.standalone.get
import co.zsmb.koinjs.standalone.getProperty
import co.zsmb.koinjs.test.AutoCloseKoinTest
import co.zsmb.koinjs.test.ext.junit.assertContexts
import co.zsmb.koinjs.test.ext.junit.assertDefinitions
import co.zsmb.koinjs.test.ext.junit.assertRemainingInstances
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class KoinContextTest : AutoCloseKoinTest() {

    val CircularDeps = applicationContext {
        bean { ComponentA(get()) }
        bean { ComponentB(get()) }
    }


    val SingleModule = applicationContext {
        bean { ComponentA(get()) }
    }


    class ComponentA(val componentB: ComponentB)
    class ComponentB(val componentA: ComponentA)


    @Test
    fun circular_deps_injection_error() {
        startKoin(listOf(CircularDeps))

        assertDefinitions(2)
        assertRemainingInstances(0)

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

        assertRemainingInstances(0)
        assertContexts(1)
    }

    @Test
    fun safe_missing_bean() {
        startKoin(listOf(SingleModule))

        assertDefinitions(1)
        assertRemainingInstances(0)

        try {
            get<ComponentA>()
            fail()
        } catch (e: Exception) {
            println(e.message)
        }

        assertRemainingInstances(0)
    }

    @Test
    fun unsafe_missing_bean() {
        startKoin(listOf(SingleModule))

        assertDefinitions(1)
        assertRemainingInstances(0)
        try {
            get<ComponentA>()
            fail("should not inject ")
        } catch (e: BeanInstanceCreationException) {
            println(e.message)
        }
        assertRemainingInstances(0)
    }

    @Test
    fun assert_given_properties_are_injected() {

        // Should read koin.properties file which contains OS_VERSION definition
        startKoin(arrayListOf(SingleModule), extraProperties = mapOf(GIVEN_PROP to VALUE_ANDROID))
        assertEquals(VALUE_ANDROID, getProperty(GIVEN_PROP))
    }

    companion object {
        const val GIVEN_PROP = "given.prop"

        const val VALUE_ANDROID = "Android"
    }
}