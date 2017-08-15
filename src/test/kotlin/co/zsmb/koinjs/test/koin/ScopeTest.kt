package co.zsmb.koinjs.test.koin

import co.zsmb.koinjs.Koin
import co.zsmb.koinjs.test.ext.assertRootScopeSize
import co.zsmb.koinjs.test.ext.assertScopeSize
import co.zsmb.koinjs.test.ext.assertScopes
import co.zsmb.koinjs.test.ext.assertSizes
import co.zsmb.koinjs.test.koin.example.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

class ScopeTest {

    @Test
    fun getScopedInstances() {
        val ctx = Koin().build(ScopedModuleB())
        ctx.assertScopes(2)
        ctx.assertSizes(1, 0)
        assertNotNull(ctx.get<ServiceB>())

        ctx.assertScopeSize(ServiceB::class, 1)
        ctx.assertSizes(1, 1)
    }

    @Test
    fun isolatedScopeWithOneInstance() {
        val ctx = Koin().build(ScopedModuleB())
        ctx.assertScopes(2)
        ctx.assertScopeSize(ServiceB::class, 0)
        ctx.assertSizes(1, 0)
        ctx.assertRootScopeSize(0)
        assertNotNull(ctx.getOrNull<ServiceB>())

        ctx.assertScopeSize(ServiceB::class, 1)
        ctx.assertSizes(1, 1)
        ctx.assertRootScopeSize(0)
    }

    @Test
    fun multiScopeRemoveTest() {
        val ctx = Koin().build(ScopedModuleB(), ScopedModuleA())

        val serviceB_1 = ctx.get<ServiceB>()
        var serviceA = ctx.get<ServiceA>()

        assertEquals(serviceA.serviceB, serviceB_1)
        ctx.assertScopeSize(ServiceB::class, 1)
        ctx.assertScopeSize(ServiceA::class, 1)
        ctx.assertSizes(2, 2)
        ctx.assertRootScopeSize(0)

        ctx.release(ServiceB::class)
        ctx.assertScopeSize(ServiceB::class, 0)
        ctx.assertScopeSize(ServiceA::class, 1)
        ctx.assertSizes(2, 1)
        ctx.assertRootScopeSize(0)

        val serviceB_2 = ctx.get<ServiceB>()
        serviceA = ctx.get<ServiceA>()
        assertNotEquals(serviceA.serviceB, serviceB_2)
        assertNotEquals(serviceB_1, serviceB_2)
        ctx.assertScopeSize(ServiceB::class, 1)
        ctx.assertScopeSize(ServiceA::class, 1)
        ctx.assertSizes(2, 2)
        ctx.assertRootScopeSize(0)

        ctx.release(ServiceA::class)
        serviceA = ctx.get<ServiceA>()
        assertEquals(serviceA.serviceB, serviceB_2)
        ctx.assertScopeSize(ServiceB::class, 1)
        ctx.assertScopeSize(ServiceA::class, 1)
        ctx.assertSizes(2, 2)
        ctx.assertRootScopeSize(0)
    }

    @Test
    fun getMultiScopedInstances() {
        val ctx = Koin().build(ScopedModuleB(), ScopedModuleA())
        ctx.assertScopes(3)
        ctx.assertSizes(2, 0)
        assertNotNull(ctx.get<ServiceB>())
        assertNotNull(ctx.get<ServiceA>())

        ctx.assertScopeSize(ServiceB::class, 1)
        ctx.assertScopeSize(ServiceA::class, 1)
        ctx.assertSizes(2, 2)
    }

    @Test
    fun getMultiScopedInstancesWithRoot() {
        val ctx = Koin().build(ScopedModuleB(), ScopedModuleA(), SampleModuleC())
        ctx.assertScopes(3)
        ctx.assertSizes(3, 0)
        assertNotNull(ctx.get<ServiceC>())

        ctx.assertScopeSize(ServiceB::class, 1)
        ctx.assertScopeSize(ServiceA::class, 1)
        ctx.assertRootScopeSize(1)
        ctx.assertSizes(3, 3)
    }

    @Test
    fun isolatedScopeThreeInstances() {
        val ctx = Koin().build(ScopedModuleB(), ScopedModuleA(), SampleModuleC())
        ctx.assertScopes(3)
        ctx.assertRootScopeSize(0)
        ctx.assertSizes(3, 0)

        val serviceB = ctx.get<ServiceB>()
        assertNotNull(serviceB)
        ctx.assertScopes(3)
        ctx.assertRootScopeSize(0)
        ctx.assertScopeSize(ServiceB::class, 1)
        ctx.assertSizes(3, 1)

        val serviceA = ctx.get<ServiceA>()
        assertNotNull(serviceA)
        ctx.assertScopes(3)
        ctx.assertRootScopeSize(0)
        ctx.assertScopeSize(ServiceB::class, 1)
        ctx.assertScopeSize(ServiceA::class, 1)
        ctx.assertSizes(3, 2)

        val serviceC = ctx.get<ServiceC>()
        assertNotNull(serviceC)
        ctx.assertScopes(3)
        ctx.assertRootScopeSize(1)
        ctx.assertScopeSize(ServiceB::class, 1)
        ctx.assertScopeSize(ServiceA::class, 1)
        ctx.assertSizes(3, 3)

        assertEquals(serviceB, serviceA.serviceB)
        assertEquals(serviceB, serviceC.serviceB)
        assertEquals(serviceA, serviceC.serviceA)
    }

    @Test
    fun scopeRelease() {
        val ctx = Koin().build(ScopedModuleB())
        ctx.assertScopes(2)
        ctx.assertSizes(1, 0)
        assertNotNull(ctx.get<ServiceB>())
        ctx.assertSizes(1, 1)
        ctx.assertScopeSize(ServiceB::class, 1)

        ctx.release(ServiceB::class)
        ctx.assertSizes(1, 0)
        ctx.assertScopeSize(ServiceB::class, 0)
    }

}
