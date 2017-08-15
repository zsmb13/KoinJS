package co.zsmb.koinjs.test.example

import co.zsmb.koinjs.test.koin.example.ServiceA
import co.zsmb.koinjs.test.koin.example.ServiceB
import co.zsmb.koinjs.test.koin.example.ServiceC

object Container {
    val serviceA: ServiceA by lazy { ServiceA(serviceB) }
    val serviceB: ServiceB by lazy { ServiceB() }
    val serviceC: ServiceC by lazy { ServiceC(serviceA, serviceB) }
}
