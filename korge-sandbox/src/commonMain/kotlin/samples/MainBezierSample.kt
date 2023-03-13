package samples

import com.soywiz.korge.input.draggable
import com.soywiz.korge.input.mouse
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.SContainer
import com.soywiz.korge.view.Text
import com.soywiz.korge.view.View
import com.soywiz.korge.view.centered
import com.soywiz.korge.view.circle
import com.soywiz.korge.view.container
import com.soywiz.korge.view.cpuGraphics
import com.soywiz.korge.view.filter.filter
import com.soywiz.korge.view.filter.ColorTransformFilter
import com.soywiz.korge.view.position
import com.soywiz.korge.view.text
import com.soywiz.korge.view.vector.gpuShapeView
import com.soywiz.korge.view.xy
import com.soywiz.korim.color.ColorAdd
import com.soywiz.korim.color.ColorTransform
import com.soywiz.korim.color.Colors
import com.soywiz.korim.paint.Paint
import com.soywiz.korim.vector.ShapeBuilder
import com.soywiz.korma.geom.*
import com.soywiz.korma.geom.bezier.Bezier
import com.soywiz.korma.geom.vector.*
import kotlin.reflect.*

class MainBezierSample : Scene() {
    var p0 = Point(109, 135)
    var p1 = Point(25, 190)
    var p2 = Point(210, 250)
    var p3 = Point(234, 49)

    override suspend fun SContainer.sceneMain() {
        val graphics = cpuGraphics(autoScaling = true)
        val graphics2 = gpuShapeView().xy(0, 300)

        fun ShapeBuilder.updateBezier() {
            stroke(Colors.DIMGREY, info = StrokeInfo(thickness = 1.0)) {
                moveTo(p0)
                lineTo(p1)
                lineTo(p2)
                lineTo(p3)
            }
            stroke(Colors.WHITE, info = StrokeInfo(thickness = 2.0)) {
                cubic(p0, p1, p2, p3)
            }
            val ratio = 0.3
            val split = Bezier(p0, p1, p2, p3).split(ratio)
            val cubic2 = split.leftCurve
            val cubic3 = split.rightCurve

            stroke(Colors.PURPLE, info = StrokeInfo(thickness = 4.0)) {
                curve(cubic2)
            }
            stroke(Colors.YELLOW, info = StrokeInfo(thickness = 4.0)) {
                curve(cubic3)
            }
        }

        fun updateGraphics() {
            graphics.updateShape {
                updateBezier()
            }
            graphics2.updateShape {
                updateBezier()
            }
        }

        updateGraphics()
        createPointController(this@MainBezierSample::p0, Colors.RED) { updateGraphics() }
        createPointController(this@MainBezierSample::p1, Colors.GREEN) { updateGraphics() }
        createPointController(this@MainBezierSample::p2, Colors.BLUE) { updateGraphics() }
        createPointController(this@MainBezierSample::p3, Colors.YELLOW) { updateGraphics() }
    }

    fun Container.createPointController(pointRef: KMutableProperty0<Point>, color: Paint, onMove: () -> Unit) {
        lateinit var circle: View
        lateinit var text: Text
        val anchorView = container {
            circle = circle(6.0, fill = color, stroke = Colors.DARKGRAY, strokeThickness = 2.0).centered
            text = text("", 10.0).position(10.0, 6.0)
        }.position(pointRef.get())

        fun updateText() {
            text.text = "(${anchorView.x.toInt()}, ${anchorView.y.toInt()})"
        }
        circle.mouse {
            onOver { circle.filter = ColorTransformFilter(ColorTransform(add = ColorAdd(+64, +64, +64, 0))) }
            onOut { circle.filter = null }
        }
        updateText()
        anchorView.draggable(circle) {
            pointRef.set(anchorView.pos)
            updateText()
            onMove()
        }
    }
}
