package samples

import com.soywiz.klock.*
import com.soywiz.korev.Key
import com.soywiz.korge.*
import com.soywiz.korge.input.keys
import com.soywiz.korge.scene.ScaledScene
import com.soywiz.korge.view.*
import com.soywiz.korge.view.filter.*
import com.soywiz.korim.color.*
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.*

class MainFlag : ScaledScene(592, 592) {
    override suspend fun SContainer.sceneMain() {
        val bitmap = resourcesVfs["korge.png"].readBitmap()
        val flagFilter = FlagFilter()

        // "Flag Pole"
        solidRect(10, 582, Colors.BLACK) {
            position(30, 30)
        }
        solidRect(20, 5, Colors.BLACK) {
            position(25, 25)
        }

        // Flag
        image(bitmap) {
            position(40, 40)
            scaleY = 0.5
            filter = flagFilter
        }

        // Propagates the wave over time
        addUpdater { dt: TimeSpan ->
            println("MainFlag.addUpdater: dt=$dt")
            flagFilter.time = flagFilter.time.plus(dt)
            invalidateRender()
        }

        fun min(a: Double, b: Double) = if (a > b) b else a
        fun max(a: Double, b: Double) = if (a > b) a else b

        keys {
            down {
                when (it.key) {
                    Key.LEFT -> flagFilter.amplitude = max(0.0, flagFilter.amplitude - 5)
                    Key.RIGHT -> flagFilter.amplitude = min(100.0, flagFilter.amplitude + 5)
                    Key.DOWN -> flagFilter.crestCount = max(0.0, flagFilter.crestCount - 0.5)
                    Key.UP -> flagFilter.crestCount = min(10.0, flagFilter.crestCount + 0.5)
                    Key.PLUS, Key.RIGHT_BRACKET, Key.CLOSE_BRACKET -> flagFilter.cyclesPerSecond = min(10.0, flagFilter.cyclesPerSecond + 0.5)
                    Key.MINUS, Key.LEFT_BRACKET, Key.OPEN_BRACKET -> flagFilter.cyclesPerSecond = max(0.0, flagFilter.cyclesPerSecond - 0.5)
                    else -> Unit
                }
                println("amplitude = ${flagFilter.amplitude}, crestCount = ${flagFilter.crestCount}, cyclesPerSecond = ${flagFilter.cyclesPerSecond}")
            }
        }
    }
}
