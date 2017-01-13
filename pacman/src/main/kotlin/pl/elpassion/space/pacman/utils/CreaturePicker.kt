package pl.elpassion.space.pacman.utils

import android.graphics.Color
import com.indoorway.android.common.sdk.model.Coordinates
import com.indoorway.android.map.sdk.view.drawable.figures.DrawableCircle
import pl.elpassion.space.pacman.SPRITE_SHEET_ID
import pl.elpassion.space.pacman.model.MapObject
import pl.elpassion.space.pacman.model.Size
import pl.elpassion.space.pacman.model.SpriteRect
import pl.elpassion.space.pacman.model.SpriteSheet

class CreaturePicker {

    companion object {
        var pacManName = "pacMan"
        val pacMan = SpriteSheet(SPRITE_SHEET_ID, SpriteRect(144, 64, 100, 128), Size(980f, 640f))
        val redGhostName = "redGhost"
        val redGhost = SpriteSheet(SPRITE_SHEET_ID, SpriteRect(12, 256, 64, 64), Size(980f, 640f))
    }

    fun createPacMan(coordinates: Coordinates) =
            pacMan.asDrawable(pacManName, coordinates, Size(2f, 2.34f))

    fun createCreature(mapObject: MapObject) =
            when (mapObject.type) {
                MapObject.Type.GHOST -> DrawableCircle(mapObject.id, 0.2f, Color.argb(255, 255, 255, 0), Color.WHITE, 0.1f, mapObject.toCoordinates())
                MapObject.Type.FOOD -> DrawableCircle(mapObject.id, 0.2f, Color.argb(255, 255, 255, 0), Color.RED, 0.1f, mapObject.toCoordinates())
                MapObject.Type.PLAYER -> DrawableCircle(mapObject.id, 0.2f, Color.argb(255, 255, 255, 0), Color.GREEN, 0.1f, mapObject.toCoordinates())
            }

    private fun MapObject.toCoordinates() = Coordinates(position.lat, position.long)
}