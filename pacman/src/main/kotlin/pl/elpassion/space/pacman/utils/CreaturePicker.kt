package pl.elpassion.space.pacman.utils

import com.indoorway.android.common.sdk.model.Coordinates
import com.indoorway.android.map.sdk.view.drawable.figures.DrawableFigure
import pl.elpassion.space.pacman.SPRITE_SHEET_ID
import pl.elpassion.space.pacman.model.Player
import pl.elpassion.space.pacman.model.Size
import pl.elpassion.space.pacman.model.SpriteRect
import pl.elpassion.space.pacman.model.SpriteSheet

class CreaturePicker {

    companion object {
        val pacManName = "pacMan"
        val pacMan = SpriteSheet(SPRITE_SHEET_ID, SpriteRect(144, 64, 100, 128), Size(980f, 640f))
        val redGhostName = "redGhost"
        val redGhost = SpriteSheet(SPRITE_SHEET_ID, SpriteRect(12, 256, 64, 64), Size(980f, 640f))
    }

    fun createPacMan(coordinates: Coordinates) =
            pacMan.asDrawable(pacManName, coordinates, Size(2f, 2.34f))

    fun createCreature(player: Player): DrawableFigure =
            redGhost.asDrawable(redGhostName, player.toCoordinates(), Size(1.5f, 1.5f))

    private fun Player.toCoordinates() = Coordinates(position.lat, position.long)
}