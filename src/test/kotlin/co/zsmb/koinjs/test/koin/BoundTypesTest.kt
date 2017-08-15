package co.zsmb.koinjs.test.koin


import co.zsmb.koinjs.Koin
import co.zsmb.koinjs.test.ext.assertRootScopeSize
import co.zsmb.koinjs.test.ext.assertScopes
import co.zsmb.koinjs.test.ext.assertSizes
import co.zsmb.koinjs.test.koin.example.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class BoundTypesTest {

    @Test
    fun getBoundedInstance() {
        val ctx = Koin().build(BindModuleB())
        ctx.assertScopes(1)
        ctx.assertSizes(1, 0)
        assertNotNull(ctx.get<Processor>())

        ctx.assertRootScopeSize(1)
        ctx.assertSizes(1, 1)
    }

    @Test
    fun shouldNotGetBoundedInstance() {
        val ctx = Koin().build(SampleModuleB())
        ctx.assertScopes(1)
        ctx.assertSizes(1, 0)
        assertNull(ctx.getOrNull<Processor>())

        ctx.assertRootScopeSize(0)
        ctx.assertSizes(1, 0)
    }

    @Test
    fun getSameBoundedInstance() {
        val ctx = Koin().build(BindModuleB())
        ctx.assertScopes(1)
        ctx.assertSizes(1, 0)
        val intf = ctx.get<Processor>()
        val servB = ctx.get<ServiceB>()

        assertEquals(servB, intf)
        ctx.assertRootScopeSize(1)
        ctx.assertSizes(1, 1)
    }

    @Test
    fun injectWithBoundedInstance() {
        val ctx = Koin().build(SampleModuleOA(), BindModuleB())
        ctx.assertScopes(1)
        ctx.assertSizes(2, 0)

        val servA = ctx.get<OtherServiceA>()
        assertNotNull(servA)
        servA.doSomethingWithB()

        ctx.assertScopes(1)
        ctx.assertRootScopeSize(2)
        ctx.assertSizes(2, 2)
    }

    @Test
    fun shouldNotInjectWithBoundedInstance() {
        val ctx = Koin().build(SampleModuleOA(), SampleModuleB())
        ctx.assertScopes(1)
        ctx.assertSizes(2, 0)

        assertNull(ctx.getOrNull<OtherServiceA>())

        ctx.assertScopes(1)
        ctx.assertRootScopeSize(0)
        ctx.assertSizes(2, 0)
    }

}
