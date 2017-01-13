package pl.elpassion.space.pacman.api

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.KotlinModule
import pl.elpassion.space.pacman.model.LocationUpdateEvent
import rx.Observable

fun Observable<String>.deserialize(clazz: Class<LocationUpdateEvent>): Observable<LocationUpdateEvent> {
    return map { parse(it, clazz) }
}

private val objectMapper = ObjectMapper()
        .registerModule(KotlinModule())
        .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.NON_PRIVATE)
        .setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE)
        .setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE)

private fun <T> parse(json: String, clazz: Class<T>): T {
    return objectMapper.readValue(json, clazz)
}
