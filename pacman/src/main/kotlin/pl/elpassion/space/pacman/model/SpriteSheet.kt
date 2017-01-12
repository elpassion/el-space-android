package pl.elpassion.space.pacman.model

import android.graphics.RectF
import com.indoorway.android.common.sdk.model.Coordinates
import com.indoorway.android.map.sdk.view.drawable.figures.DrawableIcon

class Size(val x: Float, val y: Float)

class SpriteRect(val x: Int, val y: Int, val sizeX: Int, val sizeY: Int)

class SpriteSheet(val textureId: String, spriteRect: SpriteRect, bitmapSize: Size) {

    val textureCoords: RectF = RectF(
            spriteRect.x / bitmapSize.x,
            spriteRect.y / bitmapSize.y,
            (spriteRect.x + spriteRect.sizeX) / bitmapSize.x,
            (spriteRect.y + spriteRect.sizeY) / bitmapSize.y
    )

    init {
        // for safety
        textureCoords.sort()
    }

    fun asDrawable(id: String, coordinates: Coordinates, size: Size): DrawableIcon {
        return DrawableIcon(
                id,
                textureId,
                coordinates,
                textureCoords,
                size.x,
                size.y
        )
    }

}