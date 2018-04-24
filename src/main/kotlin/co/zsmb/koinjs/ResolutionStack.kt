package co.zsmb.koinjs

import co.zsmb.koinjs.core.bean.BeanDefinition
import co.zsmb.koinjs.error.DependencyResolutionException
import co.zsmb.koinjs.js.Stack

class ResolutionStack {

    /**
     * call stack - bean definition resolution
     */
    private val stack = Stack<StackItem>()

    /**
     * Allow to execute the execution function if the bean definition is well defined on stack
     * @param beanDefinition - bean definition
     * @param execution - executed code once bean definition has been stacked
     */
    fun resolve(beanDefinition: BeanDefinition<*>, execution: () -> Unit) {
        checkStackEnter(beanDefinition)

        stack.add(beanDefinition)
        execution()

        checkStackExit(beanDefinition)
    }

    /**
     * Check if bean is not already on stack
     */
    private fun checkStackEnter(beanDefinition: BeanDefinition<*>) {
        if (stack.any { it == beanDefinition }) {
            throw DependencyResolutionException(
                    "Cyclic call while resolving $beanDefinition. Definition is already in resolution in current call:\n\t${stack.joinToString(
                            "\n\t"
                    )}"
            )
        }
    }

    /**
     * Should pop the same bean definition after exit
     */
    private fun checkStackExit(beanDefinition: BeanDefinition<*>) {
        val head: BeanDefinition<*> = stack.pop()
        if (head != beanDefinition) {
            stack.clear()
            throw IllegalStateException("Stack resolution error : was $head but should be $beanDefinition")
        }
    }

    /**
     * For log indentation
     */
    fun indent(): String = stack.joinToString(separator = "") { "\t" }

    /**
     * Last stack item
     */
    fun last(): StackItem? = if (stack.size > 0) stack.peek() else null

    /**
     * Clear stack
     */
    fun clear() = stack.clear()
}


/**
 * Resolution Stack Item
 */
typealias StackItem = BeanDefinition<*>