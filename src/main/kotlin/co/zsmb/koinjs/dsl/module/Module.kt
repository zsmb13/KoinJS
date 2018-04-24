package co.zsmb.koinjs.dsl.module

import co.zsmb.koinjs.KoinContext
import co.zsmb.koinjs.core.scope.Scope
import co.zsmb.koinjs.dsl.context.Context
import co.zsmb.koinjs.standalone.StandAloneContext


/**
 * Create Context
 */
fun applicationContext(init: Context.() -> Unit): Module = { Context(Scope.ROOT, StandAloneContext.koinContext as KoinContext).apply(init) }

/**
 * Module - function that gives a module
 */
typealias Module = () -> Context