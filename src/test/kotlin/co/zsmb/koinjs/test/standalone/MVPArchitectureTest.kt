package co.zsmb.koinjs.test.standalone

import co.zsmb.koinjs.core.scope.Scope
import co.zsmb.koinjs.dsl.module.applicationContext
import co.zsmb.koinjs.standalone.KoinComponent
import co.zsmb.koinjs.standalone.StandAloneContext.startKoin
import co.zsmb.koinjs.standalone.get
import co.zsmb.koinjs.standalone.inject
import co.zsmb.koinjs.standalone.releaseContext
import co.zsmb.koinjs.test.AutoCloseKoinTest
import co.zsmb.koinjs.test.ext.junit.assertContexts
import co.zsmb.koinjs.test.ext.junit.assertDefinedInScope
import co.zsmb.koinjs.test.ext.junit.assertDefinitions
import co.zsmb.koinjs.test.ext.junit.assertRemainingInstances
import kotlin.test.Test
import kotlin.test.assertEquals

class MVPArchitectureTest : AutoCloseKoinTest() {

    val MVPModule =
            applicationContext {
                bean { Repository(get()) }

                context("View") {
                    bean { View() }
                    bean { Presenter(get()) }
                }
            }

    val DataSourceModule =
            applicationContext {
                bean { DebugDatasource() } bind (Datasource::class)
            }


    class View() : KoinComponent {
        val presenter: Presenter by inject()

        fun onDestroy() {
            releaseContext("View")
        }
    }

    class Presenter(val repository: Repository)
    class Repository(val datasource: Datasource)
    interface Datasource
    class DebugDatasource : Datasource

    @Test
    fun should_create_all_MVP_hierarchy() {
        startKoin(listOf(MVPModule, DataSourceModule))

        val view = get<View>()
        val presenter = get<Presenter>()
        val repository = get<Repository>()
        val datasource = get<DebugDatasource>()

        assertEquals(presenter, view.presenter)
        assertEquals(repository, presenter.repository)
        assertEquals(repository, view.presenter.repository)
        assertEquals(datasource, repository.datasource)

        assertRemainingInstances(4)
        assertDefinitions(4)
        assertContexts(2)
        assertDefinedInScope(Repository::class, Scope.ROOT)
        assertDefinedInScope(DebugDatasource::class, Scope.ROOT)
        assertDefinedInScope(View::class, "View")
        assertDefinedInScope(Presenter::class, "View")

        view.onDestroy()
        assertRemainingInstances(2)
        assertDefinitions(4)
        assertContexts(2)
    }
}