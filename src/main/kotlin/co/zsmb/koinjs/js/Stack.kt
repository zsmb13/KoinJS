package co.zsmb.koinjs.js

class Stack<T>(private val items: MutableList<T> = mutableListOf()) : MutableList<T> by items {

    fun pop(): T = items.removeAt(items.lastIndex)

    fun peek(): T = items.last()

}