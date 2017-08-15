package co.zsmb.koinjs.test.example

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class SimpleObjectTest {

    @Test
    fun simpleInjectionFromObjectContainerClasses() {

        val serviceA = Container.serviceA
        serviceA.doSomethingWithB()

        val serviceC = Container.serviceC
        serviceC.doSomethingWithAll()

        val serviceB = Container.serviceB
        assertNotNull(serviceA)
        assertNotNull(serviceB)
        assertNotNull(serviceC)

        assertEquals(serviceC.serviceA, serviceA)
        assertEquals(serviceC.serviceB, serviceB)
    }

}
