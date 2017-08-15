//package co.zsmb.koinjs.test.koin
//
//import co.zsmb.koinjs.Koin
//import co.zsmb.koinjs.test.ext.assertSizes
//import co.zsmb.koinjs.test.koin.example.SampleModuleB
//import co.zsmb.koinjs.test.koin.example.ServiceB
//import kotlin.test.Test
//import kotlin.test.assertNotEquals
//
//class OverwriteTest {
//
//    @Test
//    fun overwriteAnAlreadyExistingBeanDefinition() {
//        val ctx = Koin().build(SampleModuleB())
//
//        ctx.assertSizes(1, 0)
//
//        val serviceB = ctx.get<ServiceB>()
//
//        ctx.assertSizes(1, 1)
//
//        val mockB = mock(ServiceB::class.java)
//        ctx.provide { mockB }
//
//        val serviceBMock = ctx.get<ServiceB>()
//
//        ctx.assertSizes(1, 1)
//
//        assertNotEquals(serviceB, serviceBMock)
//    }
//
//}
