package co.zsmb.koinjs.test.core

import co.zsmb.koinjs.error.AlreadyStartedException
import co.zsmb.koinjs.standalone.StandAloneContext.closeKoin
import co.zsmb.koinjs.standalone.StandAloneContext.startKoin
import co.zsmb.koinjs.test.KoinTest
import co.zsmb.koinjs.test.ext.junit.assertContexts
import co.zsmb.koinjs.test.ext.junit.assertDefinitions
import co.zsmb.koinjs.test.ext.junit.assertRemainingInstances
import kotlin.test.Test
import kotlin.test.fail


class StartCloseTest : KoinTest {

    @Test
    fun start_and_close_Koin() {
        startKoin(listOf())

        assertRemainingInstances(0)
        assertDefinitions(0)
        assertContexts(1)

        closeKoin()
    }

    @Test
    fun start_and_restart_Koin() {
        startKoin(listOf())
        try {
            startKoin(listOf())
            fail()
        } catch (e: AlreadyStartedException) {
        }
        closeKoin()
    }
}