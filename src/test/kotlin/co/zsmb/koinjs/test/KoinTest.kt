package co.zsmb.koinjs.test

import co.zsmb.koinjs.Koin
import co.zsmb.koinjs.KoinContext
import co.zsmb.koinjs.core.parameter.Parameters
import co.zsmb.koinjs.standalone.KoinComponent
import co.zsmb.koinjs.standalone.StandAloneContext
import co.zsmb.koinjs.standalone.StandAloneContext.closeKoin
import kotlin.test.AfterTest

/**
 * Koin Test Component
 */
interface KoinTest : KoinComponent

/**
 * Make a Dry Run - Test if each definition is injectable
 */
fun KoinTest.dryRun(defaultParameters: Parameters = { emptyMap() }) {
    (StandAloneContext.koinContext as KoinContext).dryRun(defaultParameters)
}

/**
 * Koin Test - embed autoclose @after method to close Koin after every test
 */
abstract class AutoCloseKoinTest() : KoinTest {

    @AfterTest
    fun autoClose() {
        Koin.logger.log("AutoClose Koin")
        closeKoin()
    }

}

