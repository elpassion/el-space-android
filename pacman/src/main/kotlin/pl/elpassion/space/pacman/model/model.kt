package pl.elpassion.space.pacman.model

import android.graphics.RectF
import com.fasterxml.jackson.annotation.JsonProperty
import com.indoorway.android.common.sdk.model.Coordinates
import com.indoorway.android.map.sdk.view.drawable.figures.DrawableIcon

data class Size(val x: Float, val y: Float)

data class SpriteRect(val x: Int, val y: Int, val sizeX: Int, val sizeY: Int)

data class SpriteSheet(val textureId: String, val spriteRect: SpriteRect, val bitmapSize: Size) {

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

data class Player(val id: String, val position: Position)

data class Position(val lat: Double, val long: Double)

data class LocationUpdateEvent(
        @JsonProperty
        val player: String,
        @JsonProperty
        val latitude: Double,
        @JsonProperty
        val longitude: Double,
        @JsonProperty
        val type: Type
) {
    enum class Type {
        @JsonProperty("ghost")
        GHOST,
        @JsonProperty("food")
        FOOD,
        @JsonProperty("player")
        PLAYER
    }
}