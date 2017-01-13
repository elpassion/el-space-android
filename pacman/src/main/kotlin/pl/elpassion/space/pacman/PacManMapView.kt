package pl.elpassion.space.pacman

import android.graphics.BitmapFactory
import com.indoorway.android.common.sdk.model.IndoorwayPosition
import com.indoorway.android.map.sdk.view.IndoorwayMapView
import com.indoorway.android.map.sdk.view.drawable.textures.DrawableTexture
import pl.elpassion.space.pacman.config.buildingUuid
import pl.elpassion.space.pacman.config.mapUuid
import pl.elpassion.space.pacman.utils.CreaturePicker
import rx.AsyncEmitter
import rx.Completable
import rx.Observable

val SPRITE_SHEET_ID = "sprite-sheet"

class PacManMapView(val mapView: IndoorwayMapView) : PacMan.MapView {

    override fun loadMap(): Completable {
        return Observable.fromAsync<Unit>({ emitter ->
            mapView.apply {
                setOnMapLoadCompletedListener<IndoorwayMapView> {
                    emitter.onCompleted()
                }
                setOnMapLoadFailedListener<IndoorwayMapView> {
                    emitter.onError(RuntimeException())
                }
                loadMap(buildingUuid, mapUuid)
            }
        }, AsyncEmitter.BackpressureMode.BUFFER)
                .toCompletable()
    }

    override fun initTextures() {
        mapView.markerControl.apply {
            // sprite sheet registration
            val bitmap = BitmapFactory.decodeResource(mapView.context.resources, R.drawable.sprites)
            registerTexture(DrawableTexture(SPRITE_SHEET_ID, bitmap))
        }
    }

    override fun updatePosition(position: IndoorwayPosition) {
        mapView.markerControl.add(CreaturePicker().createPacMan(position.coordinates))
    }
}