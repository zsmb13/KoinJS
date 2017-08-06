package org.koin.bean

import org.koin.dsl.context.Scope
import kotlin.reflect.KClass

/**
 * Bean definition
 * @author - Arnaud GIULIANI
 *
 * Gather type of T
 * definied by lazy/function
 * has a type (clazz)
 * has a BeanType : default singleton
 */
data class BeanDefinition<out T>(val definition: () -> T, val clazz: KClass<*>, val scope: Scope = Scope.root(), var bindTypes: List<KClass<*>> = arrayListOf()) {

    /**
     * Add a compatible type to current bounded definition
     */
    infix fun bind(bind: () -> KClass<*>): BeanDefinition<T> {
        bindTypes += bind()
        return this
    }

}