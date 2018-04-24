package co.zsmb.koinjs.test.core

import co.zsmb.koinjs.dsl.module.applicationContext
import co.zsmb.koinjs.standalone.StandAloneContext.startKoin
import co.zsmb.koinjs.standalone.get
import co.zsmb.koinjs.test.AutoCloseKoinTest
import co.zsmb.koinjs.test.ext.junit.assertContexts
import co.zsmb.koinjs.test.ext.junit.assertDefinitions
import co.zsmb.koinjs.test.ext.junit.assertRemainingInstances
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.fail

class NamedBeansTest : AutoCloseKoinTest() {

    val DataSourceModule = applicationContext {
        bean(name = "debug") { DebugDatasource() } bind (Datasource::class)
        bean(name = "prod") { ProdDatasource() } bind (Datasource::class)
    }

    val ServiceModule = applicationContext {
        bean(name = "debug") { Service(get("debug")) } bind (Service::class)
    }

    interface Datasource
    class DebugDatasource : Datasource
    class ProdDatasource : Datasource

    class Service(val datasource: Datasource)

    @Test
    fun should_get_named_bean() {
        startKoin(listOf(DataSourceModule))

        val debug = get<Datasource>("debug")
        val prod = get<Datasource>("prod")

        assertNotNull(debug)
        assertNotNull(prod)

        assertDefinitions(2)
        assertRemainingInstances(2)
        assertContexts(1)
    }

    @Test
    fun should_not_get_named_bean() {
        startKoin(listOf(DataSourceModule))

        try {
            get<Datasource>("otherDatasource")
            fail()
        } catch (e: Exception) {
        }

        assertDefinitions(2)
        assertRemainingInstances(0)
        assertContexts(1)
    }

    @Test
    fun should_resolve_different_types_for_same_bean_name() {
        startKoin(listOf(DataSourceModule, ServiceModule))

        val debug = get<Datasource>("debug")
        val debugService = get<Service>("debug")

        assertNotNull(debug)
        assertNotNull(debugService)
        assertEquals(debug, debugService.datasource)

        assertDefinitions(3)
        assertRemainingInstances(2)
        assertContexts(1)
    }
}