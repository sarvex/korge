package samples

import korlibs.time.*
import korlibs.korge.scene.*
import korlibs.korge.time.*
import korlibs.korge.ui.*
import korlibs.korge.view.*
import korlibs.image.color.*
import korlibs.math.interpolation.*
import korlibs.math.random.*
import kotlin.random.*

//class MainCache : ScaledScene(512, 512) {
class MainCache : Scene() {
    override suspend fun SContainer.sceneMain() {
        //val cached = CachedContainer().addTo(this)
        //val cached = container {  }
        val cached = cachedContainer {  }
        val random = Random(0L)
        for (n in 0 until 100_000) {
            cached.solidRect(2, 2, random[Colors.RED, Colors.BLUE]).xy(2 * (n % 300), 2 * (n / 300))
        }
        uiHorizontalStack {
            uiButton("Cached").clicked {
                cached.cache = !cached.cache
                it.text = if (cached.cache) "Cached" else "Uncached"
            }
            uiText("children=${cached.numChildren}")
        }

        interval(1.seconds) {
            for (n in 0 until 2000) {
                cached.getChildAt(50_000 + n).colorMul = random[Colors.RED, Colors.BLUE].mix(Colors.WHITE, 0.3.toRatio())
            }
            println(cached.getChildAt(50000)._invalidateNotifier)
        }


        //timeout(1.seconds) {
        //    rect.color = Colors.BLUE
        //}
    }
}
