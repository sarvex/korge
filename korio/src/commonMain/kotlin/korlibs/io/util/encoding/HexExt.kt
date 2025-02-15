package korlibs.io.util.encoding

import korlibs.memory.ByteArrayBuilder
import korlibs.crypto.encoding.Hex

fun Hex.decode(src: String, dst: ByteArrayBuilder) = decode(src) { n, byte -> dst.append(byte) }
