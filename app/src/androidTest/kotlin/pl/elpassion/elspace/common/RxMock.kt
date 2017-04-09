package pl.elpassion.elspace.common

import com.nhaarman.mockito_kotlin.mock
import org.mockito.stubbing.Answer
import rx.Observable

inline fun <reified T : Any> rxMockJust(value: Any?): T {
    return mock(defaultAnswer = Answer<Any> { Observable.just(value) })
}