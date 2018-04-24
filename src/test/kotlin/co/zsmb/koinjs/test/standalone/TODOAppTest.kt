package co.zsmb.koinjs.test.standalone

import co.zsmb.koinjs.dsl.module.applicationContext
import co.zsmb.koinjs.standalone.KoinComponent
import co.zsmb.koinjs.standalone.StandAloneContext.startKoin
import co.zsmb.koinjs.standalone.get
import co.zsmb.koinjs.standalone.inject
import co.zsmb.koinjs.test.AutoCloseKoinTest
import co.zsmb.koinjs.test.ext.junit.assertRemainingInstances
import kotlin.test.Test
import kotlin.test.assertNotNull

class TODOAppTest : AutoCloseKoinTest() {

    val TodoAppModule = applicationContext {
        bean { TasksView() } bind TasksContract.View::class
        bean { TasksPresenter(get()) as TasksContract.Presenter }
    }

    val RepositoryModule = applicationContext {
        bean("remoteDataSource") { FakeTasksRemoteDataSource() as TasksDataSource }
        bean("localDataSource") { TasksLocalDataSource() as TasksDataSource }
        bean { TasksRepository(get("remoteDataSource"), get("localDataSource")) } bind TasksDataSource::class
    }

    interface TasksContract {
        interface View
        interface Presenter
    }

    class TasksView() : KoinComponent, TasksContract.View {
        val taskPreenter by inject<TasksContract.Presenter>()
    }

    class TasksPresenter(val tasksRepository: TasksRepository) : TasksContract.Presenter
    interface TasksDataSource
    class FakeTasksRemoteDataSource() : TasksDataSource
    class TasksLocalDataSource() : TasksDataSource
    class TasksRepository(val remoteDataSource: TasksDataSource, val localDatasource: TasksDataSource) : TasksDataSource

    @Test
    fun should_create_all_components() {
        startKoin(listOf(TodoAppModule, RepositoryModule))

        val view = get<TasksView>()
        assertNotNull(view.taskPreenter)
        assertRemainingInstances(5)
    }
}