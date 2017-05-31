package pl.elpassion.elspace.common;

typealias Initializer<T> = () -> T

abstract class Provider<T>(initializer: Initializer<T>) {

    private val original by lazy(initializer)

    fun get() = override?.invoke() ?: original

    var override: Initializer<T>? = null
}