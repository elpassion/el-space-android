package pl.elpassion.space.pacman.utils

import android.graphics.Color
import com.indoorway.android.common.sdk.model.Coordinates
import com.indoorway.android.map.sdk.view.drawable.figures.DrawableCircle
import pl.elpassion.space.pacman.SPRITE_SHEET_ID
import pl.elpassion.space.pacman.model.Player
import pl.elpassion.space.pacman.model.Size
import pl.elpassion.space.pacman.model.SpriteRect
import pl.elpassion.space.pacman.model.SpriteSheet

class CreaturePicker {

    companion object {
        val pacManName = "pacMan"
        val pacMan = SpriteSheet(SPRITE_SHEET_ID, SpriteRect(144, 64, 100, 128), Size(980f, 640f))
        val redGhost = SpriteSheet(SPRITE_SHEET_ID, SpriteRect(12, 256, 64, 64), Size(980f, 640f))
    }

    fun createCreature(player: Player): DrawableCircle {
        return DrawableCircle(player.id, 0.2f, Color.argb(255, 255, 255, 0), Color.RED, 0.1f, player.toCoordinates())
    }

    private fun Player.toCoordinates() = Coordinates(position.lat, position.long)
}