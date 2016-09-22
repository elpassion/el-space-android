package pl.elpassion.common;

abstract class Provider<T>(private val initializer: () -> T) {

    private val original by lazy { initializer() }

    fun get() = override?.invoke() ?: original

    var override: (() -> T)? = null
}