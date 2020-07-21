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

import com.esotericsoftware.spine.graphics.Color

import com.esotericsoftware.spine.PathConstraint
import com.esotericsoftware.spine.utils.SpineUtils.arraycopy

/** An attachment whose vertices make up a composite Bezier curve.
 *
 *
 * See [PathConstraint] and [Paths](http://esotericsoftware.com/spine-paths) in the Spine User Guide.  */
class PathAttachment(name: String) : VertexAttachment(name) {
    /** The lengths along the path in the setup pose from the start of the path to the end of each Bezier curve.  */
    lateinit var lengths: FloatArray

    /** If true, the start and end knots are connected.  */
    var closed: Boolean = false

    /** If true, additional calculations are performed to make calculating positions along the path more accurate. If false, fewer
     * calculations are performed but calculating positions along the path is less accurate.  */
    var constantSpeed: Boolean = false

    // Nonessential.
    /** The color of the path as it was in Spine. Available only when nonessential data was exported. Paths are not usually
     * rendered at runtime.  */
    val color = Color(1f, 0.5f, 0f, 1f) // ff7f00ff

    override fun copy(): Attachment {
        val copy = PathAttachment(name)
        copyTo(copy)
        copy.lengths = FloatArray(lengths.size)
        arraycopy(lengths, 0, copy.lengths, 0, lengths.size)
        copy.closed = closed
        copy.constantSpeed = constantSpeed
        copy.color.set(color)
        return copy
    }
}
