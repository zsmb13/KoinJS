//package co.zsmb.koinjs.test.example
//
//import co.zsmb.koinjs.test.koin.example.ServiceA
//import co.zsmb.koinjs.test.koin.example.ServiceB
//import co.zsmb.koinjs.test.koin.example.ServiceC
//import kotlin.test.Test
//
//class MockingInjectionTest {
//
//    val serviceB: ServiceB = mock(ServiceB::class.java)
//    val serviceA: ServiceA by lazy { ServiceA(serviceB) }
//
//    @Test
//    fun simpleMockInjection() {
//        `when`(serviceB.process()).then {
//            println("<mock> $this processor !")
//        }
//        serviceA.doSomethingWithB()
//        verify(serviceB).process()
//    }
//
//    @Test
//    fun multipleMockInjection() {
//        val serviceB: ServiceB = mock(ServiceB::class.java)
//        val serviceA: ServiceA = mock(ServiceA::class.java)
//        `when`(serviceA.doSomethingWithB()).then {
//            println("<mock> A $this doSomethingWithB !")
//            serviceB.process()
//        }
//        `when`(serviceB.process()).then {
//            println("<mock> B $this processor !")
//        }
//        val serviceC = ServiceC(serviceA, serviceB)
//
//        serviceC.doSomethingWithAll()
//        verify(serviceA).doSomethingWithB()
//        verify(serviceB, times(2)).process()
//    }
//
//}
