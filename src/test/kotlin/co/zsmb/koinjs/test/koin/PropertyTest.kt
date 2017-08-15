package co.zsmb.koinjs.test.koin

import co.zsmb.koinjs.Koin
import co.zsmb.koinjs.test.ext.assertProps
import co.zsmb.koinjs.test.koin.example.SampleModuleD
import co.zsmb.koinjs.test.koin.example.ServiceD
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class PropertyTest {

    @Test
    fun getBooleanProperty() {
        val ctx = Koin()
                .properties(mapOf("isTrue" to true))
                .build(SampleModuleD())

        val myVal = ctx.getProperty<Boolean>("isTrue")
        assertTrue(myVal)

        ctx.assertProps(1)
    }


    @Test
    fun propertyHasBeenAdded() {
        val ctx = Koin()
                .properties(mapOf("myVal" to "VALUE!"))
                .build(SampleModuleD())

        val myVal = ctx.getProperty<String>("myVal")
        assertNotNull(myVal)

        ctx.assertProps(1)

        val serviceD = ctx.get<ServiceD>()
        assertNotNull(serviceD)
    }

    @Test
    fun setPropertyOnContext() {
        val ctx = Koin().build(SampleModuleD())

        ctx.assertProps(0)

        val myVal = "myVal"
        ctx.setProperty("myVal", myVal)

        ctx.assertProps(1)

        val serviceD = ctx.get<ServiceD>()
        assertEquals(myVal, serviceD.myVal)
    }

}
