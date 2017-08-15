package co.zsmb.koinjs.test.koin.example

import co.zsmb.koinjs.dsl.context.Context
import co.zsmb.koinjs.dsl.module.Module


class SampleModuleA_C : Module() {
    override fun context() =
            declareContext {
                provide { ServiceA(get()) }
                provide { ServiceC(get(), get()) }
            }
}

class SampleModuleB : Module() {
    override fun context() =
            declareContext {
                provide { ServiceB() }
            }
}

class SampleModuleD : Module() {
    override fun context() =
            declareContext {
                provide { ServiceD(getProperty<String>("myVal")) }
            }
}

class ScopedModuleB : Module() {
    override fun context() =
            declareContext {
                scope { ServiceB::class }
                provide { ServiceB() }
            }
}


class ScopedModuleA : Module() {
    override fun context() =
            declareContext {
                scope { ServiceA::class }
                provide { ServiceA(get()) }
            }
}

class SampleModuleC : Module() {
    override fun context(): Context = declareContext {
        provide { ServiceC(get(), get()) }
    }
}

class SampleModuleOA : Module() {
    override fun context() = declareContext {
        provide { OtherServiceA(get()) }
    }
}

class BindModuleB : Module() {
    override fun context() = declareContext {
        provide { ServiceB() } bind { Processor::class }
    }
}
