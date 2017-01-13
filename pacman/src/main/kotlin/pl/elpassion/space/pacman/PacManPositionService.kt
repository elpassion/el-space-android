package pl.elpassion.space.pacman

import android.content.Context
import com.indoorway.android.common.sdk.model.IndoorwayPosition
import com.indoorway.android.location.sdk.IndoorwayLocationSdk
import com.indoorway.android.location.sdk.service.PositioningServiceConnection
import rx.AsyncEmitter
import rx.Observable

class PacManPositionService(val context: Context) : PacMan.PositionService {
    override fun start(): Observable<IndoorwayPosition> {
        return Observable.fromAsync({ emitter ->
            IndoorwayLocationSdk.getInstance().positioningServiceConnection.run {
                emitter.setCancellation {
                    stop(context)
                }
                setOnPositionChangedListener<PositioningServiceConnection> { position ->
                    emitter.onNext(position)
                }
                try {
                    start(context)
                } catch (exception: Exception) {
                    emitter.onError(exception)
                }
            }
        }, AsyncEmitter.BackpressureMode.BUFFER)
    }
}