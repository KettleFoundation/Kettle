package kettle

import net.md_5.specialsource.JarMapping
import net.md_5.specialsource.JarRemapper

class ThermosRemapper(jarMapping: JarMapping) : JarRemapper(jarMapping) {

    override fun mapSignature(signature: String, typeSignature: Boolean): String {
        try {
            return super.mapSignature(signature, typeSignature)
        } catch (e: Exception) {
            return signature
        }

    }
}
