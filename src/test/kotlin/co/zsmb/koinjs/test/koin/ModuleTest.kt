package co.zsmb.koinjs.test.koin

import co.zsmb.koinjs.Koin
import co.zsmb.koinjs.error.InstanceNotFoundException
import co.zsmb.koinjs.test.ext.assertSizes
import co.zsmb.koinjs.test.koin.example.*
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ModuleTest {

    @Test
    fun loadModule() {
        val ctx = Koin().build(SampleModuleB())

        ctx.assertSizes(1, 0)

        assertNotNull(ctx.get<ServiceB>())

        ctx.assertSizes(1, 1)
    }

    @Test
    fun loadModuleWithMissingDependency() {
        val ctx = Koin().build(SampleModuleB())

        ctx.assertSizes(1, 0)

        assertNotNull(ctx.get<ServiceB>())

        ctx.assertSizes(1, 1)
        assertNull(ctx.getOrNull<ServiceA>())
        ctx.assertSizes(1, 1)
    }

    @Test
    fun loadMulitpleModules() {
        val ctx = Koin().build(SampleModuleA_C(), SampleModuleB())
        assertNotNull(ctx.get<ServiceB>())
        assertNotNull(ctx.get<ServiceA>())
        ctx.assertSizes(3, 2)
    }

    @Test
    fun loadMultipleModulesWithLazyDeps() {
        val ctx = Koin().build(SampleModuleB(), SampleModuleA_C())

        assertNotNull(ctx.get<ServiceB>())
        assertNotNull(ctx.get<ServiceA>())
        assertNotNull(ctx.get<ServiceC>())
        ctx.assertSizes(3, 3)
    }

    @Test
    fun importWithLazyLinking() {
        //context only ServiceB
        val ctx = Koin().build(SampleModuleA_C())

        ctx.assertSizes(2, 0)

        ctx.provide { ServiceB() }

        ctx.assertSizes(3, 0)

        assertNotNull(ctx.get<ServiceA>())
        assertNotNull(ctx.get<ServiceB>())
        assertNotNull(ctx.get<ServiceC>())
        ctx.assertSizes(3, 3)
    }

    @Test
    fun missingBeanComponentWithLazyLinking() {
        val ctx = Koin().build(SampleModuleA_C())

        try {
            assertNull(ctx.getOrNull<ServiceA>())
            assertNull(ctx.getOrNull<ServiceC>())
        } catch (e: InstanceNotFoundException) {
            assertNotNull(e)
        }

        ctx.assertSizes(2, 0)
    }
}
