package pl.elpassion.space.pacman.utils

import pl.elpassion.space.pacman.SPRITE_SHEET_ID
import pl.elpassion.space.pacman.model.Size
import pl.elpassion.space.pacman.model.SpriteRect
import pl.elpassion.space.pacman.model.SpriteSheet

class CreaturePicker {

    companion object {
        val pacManName = "pacMan"
        val pacMan = SpriteSheet(SPRITE_SHEET_ID, SpriteRect(144, 64, 100, 128), Size(980f, 640f))
        val redGhost = SpriteSheet(SPRITE_SHEET_ID, SpriteRect(12, 256, 64, 64), Size(980f, 640f))
    }
}