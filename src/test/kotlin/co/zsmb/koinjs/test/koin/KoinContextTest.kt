//package co.zsmb.koinjs.test.koin
//
//import co.zsmb.koinjs.Koin
//import co.zsmb.koinjs.error.InstanceNotFoundException
//import co.zsmb.koinjs.test.ext.assertScopes
//import co.zsmb.koinjs.test.ext.assertSizes
//import co.zsmb.koinjs.test.koin.example.ServiceA
//import co.zsmb.koinjs.test.koin.example.ServiceB
//import co.zsmb.koinjs.test.koin.example.ServiceOne
//import co.zsmb.koinjs.test.koin.example.ServiceTwo
//import kotlin.test.Test
//import kotlin.test.assertEquals
//import kotlin.test.assertNotNull
//import kotlin.test.assertNull
//
//
//class KoinContextTest {
//
//    @Test
//    fun noModuleProvideComponents() {
//        val ctx = Koin().build()
//
//        ctx.provide { ServiceB() }
//        ctx.provide { ServiceA(ctx.get()) }
//
//        ctx.assertSizes(2, 0)
//
//        assertNotNull(ctx.get<ServiceA>())
//        assertNotNull(ctx.get<ServiceB>())
//
//        ctx.assertSizes(2, 2)
//        ctx.assertScopes(1)
//    }
//
//    //TODO Handle Stack
//
//    @Test
//    fun circularDeps() {
//        val ctx = Koin().build()
//
//        ctx.provide { ServiceTwo(ctx.get()) }
//        ctx.provide { ServiceOne(ctx.get()) }
//
//        ctx.assertSizes(2, 0)
//
//        assertNull(ctx.getOrNull<ServiceTwo>())
//        assertNull(ctx.getOrNull<ServiceOne>())
//
//        ctx.assertSizes(2, 0)
//        ctx.assertScopes(1)
//    }
//
//    @Test
//    fun functionalProvideMock() {
//        val ctx = Koin().build()
//
//        val serviceB: ServiceB = mock(ServiceB::class.java)
//        `when`(serviceB.process()).then {
//            println("done B Mock")
//        }
//
//        ctx.provide { serviceB }
//        ctx.provide({ ServiceA(ctx.get()) })
//
//        ctx.assertSizes(2, 0)
//
//        val serviceA_1 = ctx.get<ServiceA>()
//        serviceA_1.doSomethingWithB()
//
//        val serviceA_2 = ctx.get<ServiceA>()
//        serviceA_2.doSomethingWithB()
//
//        ctx.assertSizes(2, 2)
//
//        assertEquals(serviceA_1, serviceA_2)
//        verify(serviceB, times(2)).process()
//    }
//
//    @Test
//    fun safeMissingBean() {
//        val ctx = Koin().build()
//
//        ctx.provide { ServiceB() }
//
//        assertNull(ctx.getOrNull<ServiceA>())
//        ctx.assertSizes(1, 0)
//    }
//
//    @Test
//    fun unsafeMissingBean() {
//        val ctx = Koin().build()
//
//        try {
//            assertNull(ctx.get<ServiceA>())
//        } catch(e: InstanceNotFoundException) {
//            assertNotNull(e)
//        }
//
//        ctx.assertSizes(0, 0)
//    }
//
//}
