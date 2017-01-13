package pl.elpassion.space.pacman.json

import org.junit.Test
import pl.elpassion.space.pacman.api.deserialize
import pl.elpassion.space.pacman.model.LocationUpdateEvent
import rx.Observable
import rx.observers.TestSubscriber

class DeserializeTest {

    private val testSubscriber = TestSubscriber.create<List<LocationUpdateEvent>>()

    @Test
    fun shouldDeserializePositionUpdate() {
        val json = stringFromFile("location-update.json")
        Observable.just(json).deserialize().subscribe(testSubscriber)
        testSubscriber.assertValue(listOf(LocationUpdateEvent("unique_string_player_id", 52.0, 21.0)))
    }
}
