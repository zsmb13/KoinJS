package co.zsmb.koinjs.js

class Stack<T> {

    private val items = mutableListOf<T>()

    fun add(item: T) = items.add(item)

    fun contains(item: T): Boolean = items.contains(item)

    fun pop(): T = items.removeAt(items.lastIndex)

}