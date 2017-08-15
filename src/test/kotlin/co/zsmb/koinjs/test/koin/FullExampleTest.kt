package co.zsmb.koinjs.test.koin


import co.zsmb.koinjs.Koin
import co.zsmb.koinjs.dsl.context.Scope
import co.zsmb.koinjs.dsl.module.Module
import co.zsmb.koinjs.test.ext.assertProps
import co.zsmb.koinjs.test.ext.assertSizes
import co.zsmb.koinjs.test.koin.example.ServiceA
import co.zsmb.koinjs.test.koin.example.ServiceB
import co.zsmb.koinjs.test.koin.example.ServiceC
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull


class SimpleModule : Module() {
    override fun context() =
            declareContext {
                provide { ServiceA(get()) }
                provide { ServiceB() }
                provide { ServiceC(get(), get()) }
            }
}

class FullExampleTest {

    @Test
    fun loadMyModuleAndTestInject() {
        val ctx = Koin().build(SimpleModule())

        val serviceA = ctx.get<ServiceA>()
        serviceA.doSomethingWithB()

        val serviceC = ctx.get<ServiceC>()
        serviceC.doSomethingWithAll()

        val serviceB = ctx.get<ServiceB>()

        assertNotNull(serviceA)
        assertNotNull(serviceB)
        assertNotNull(serviceC)
        assertEquals(serviceA.serviceB, serviceB)
        assertEquals(serviceC.serviceA, serviceA)
        assertEquals(serviceC.serviceB, serviceB)

        ctx.assertSizes(3, 3)
        ctx.assertProps(0)
        assertEquals(3, ctx.instanceResolver.getInstanceFactory(Scope.root()).instances.size)
    }

}
