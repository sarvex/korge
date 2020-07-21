/******************************************************************************
 * Spine Runtimes License Agreement
 * Last updated January 1, 2020. Replaces all prior versions.
 *
 * Copyright (c) 2013-2020, Esoteric Software LLC
 *
 * Integration of the Spine Runtimes into software or otherwise creating
 * derivative works of the Spine Runtimes is permitted under the terms and
 * conditions of Section 2 of the Spine Editor License Agreement:
 * http://esotericsoftware.com/spine-editor-license
 *
 * Otherwise, it is permitted to integrate the Spine Runtimes into software
 * or otherwise create derivative works of the Spine Runtimes (collectively,
 * "Products"), provided that each user of the Products must obtain their own
 * Spine Editor license and redistribution of the Products in any form must
 * include this license and copyright notice.
 *
 * THE SPINE RUNTIMES ARE PROVIDED BY ESOTERIC SOFTWARE LLC "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL ESOTERIC SOFTWARE LLC BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES,
 * BUSINESS INTERRUPTION, OR LOSS OF USE, DATA, OR PROFITS) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THE SPINE RUNTIMES, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.esotericsoftware.spine.attachments

import com.badlogic.gdx.math.MathUtils.cosDeg
import com.badlogic.gdx.math.MathUtils.radDeg
import com.badlogic.gdx.math.MathUtils.sinDeg

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import com.esotericsoftware.spine.Bone

/** An attachment which is a single point and a rotation. This can be used to spawn projectiles, particles, etc. A bone can be
 * used in similar ways, but a PointAttachment is slightly less expensive to compute and can be hidden, shown, and placed in a
 * skin.
 *
 *
 * See [Point Attachments](http://esotericsoftware.com/spine-point-attachments) in the Spine User Guide.  */
class PointAttachment(name: String) : Attachment(name) {
    var x: Float = 0.toFloat()
    var y: Float = 0.toFloat()
    var rotation: Float = 0.toFloat()

    // Nonessential.
    /** The color of the point attachment as it was in Spine. Available only when nonessential data was exported. Point attachments
     * are not usually rendered at runtime.  */
    val color = Color(0.9451f, 0.9451f, 0f, 1f) // f1f100ff

    fun computeWorldPosition(bone: Bone, point: Vector2): Vector2 {
        point.x = x * bone.a + y * bone.b + bone.worldX
        point.y = x * bone.c + y * bone.d + bone.worldY
        return point
    }

    fun computeWorldRotation(bone: Bone): Float {
        val cos = cosDeg(rotation)
        val sin = sinDeg(rotation)
        val x = cos * bone.a + sin * bone.b
        val y = cos * bone.c + sin * bone.d
        return Math.atan2(y.toDouble(), x.toDouble()).toFloat() * radDeg
    }

    override fun copy(): Attachment {
        val copy = PointAttachment(name)
        copy.x = x
        copy.y = y
        copy.rotation = rotation
        copy.color.set(color)
        return copy
    }
}
