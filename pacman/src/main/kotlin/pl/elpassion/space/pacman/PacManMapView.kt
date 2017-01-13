package pl.elpassion.space.pacman

import android.graphics.BitmapFactory
import android.graphics.Color
import com.indoorway.android.common.sdk.model.IndoorwayPosition
import com.indoorway.android.map.sdk.view.IndoorwayMapView
import com.indoorway.android.map.sdk.view.drawable.figures.DrawableCircle
import com.indoorway.android.map.sdk.view.drawable.textures.DrawableTexture
import pl.elpassion.space.pacman.config.TEST_POINTS
import pl.elpassion.space.pacman.config.buildingUuid
import pl.elpassion.space.pacman.config.mapUuid
import pl.elpassion.space.pacman.utils.CreaturePicker
import pl.elpassion.space.pacman.model.Size
import rx.AsyncEmitter
import rx.Observable

val SPRITE_SHEET_ID = "sprite-sheet"

class PacManMapView(val mapView: IndoorwayMapView) : PacMan.MapView {

    override fun loadMap(): Observable<Unit> {
        return Observable.fromAsync<Unit>({ emitter ->
            mapView.apply {
                setOnMapLoadCompletedListener<IndoorwayMapView> {
                    emitter.onNext(Unit)
                }
                setOnMapLoadFailedListener<IndoorwayMapView> {
                    emitter.onError(RuntimeException())
                }
                loadMap(buildingUuid, mapUuid)
            }
        }, AsyncEmitter.BackpressureMode.BUFFER)
    }

    override fun initTextures() {
        mapView.markerControl.apply {
            // sprite sheet registration
            val bitmap = BitmapFactory.decodeResource(mapView.context.resources, R.drawable.sprites)
            registerTexture(DrawableTexture(SPRITE_SHEET_ID, bitmap))

            for ((idx, point) in TEST_POINTS.withIndex())
                add(DrawableCircle(idx.toString(), 0.2f, Color.argb(255, 255, 255 - idx, idx), Color.RED, 0.1f, point))
        }
    }

    override fun updatePosition(position: IndoorwayPosition) {
        mapView.markerControl.add(CreaturePicker.pacMan.asDrawable(CreaturePicker.pacManName, position.coordinates, Size(2f, 2.34f)))
    }
}