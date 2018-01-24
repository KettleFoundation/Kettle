package kettle

import org.objectweb.asm.Opcodes.ALOAD
import org.objectweb.asm.Opcodes.ARETURN
import org.objectweb.asm.Opcodes.GETFIELD

import org.apache.logging.log4j.Level
import org.objectweb.asm.tree.FieldInsnNode
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.InsnNode
import org.objectweb.asm.tree.IntInsnNode

import pw.prok.kimagine.asm.ImagineASM
import pw.prok.kimagine.asm.Transformer

import net.minecraftforge.fml.common.FMLLog

@Transformer.RegisterTransformer
class KettleClassTransformer : Transformer {
    fun transform(asm: ImagineASM) {
        if (asm.`is`("climateControl.utils.ChunkGeneratorExtractor")) {
            var undergroundBiomesInstalled = false
            try {
                Class.forName("exterminatorJeff.undergroundBiomes.worldGen.ChunkProviderWrapper")
                undergroundBiomesInstalled = true
            } catch (ignored: Exception) {
            }

            if (!undergroundBiomesInstalled) {
                FMLLog.log(Level.INFO, "Kettle: Patching " + asm.getActualName() + " for compatibility with Climate Control")
                extractFrom(asm, asm.method("extractFrom",
                        "(Lnet/minecraft/world/WorldServer;)Lnet/minecraft/world/chunk/IChunkProvider;").instructions())
            }
        }
    }

    fun extractFrom(asm: ImagineASM, list: InsnList) {
        //Pair<String, String> fieldChunkProvider = asm.field("net/minecraft/world/World", "chunkProvider");
        list.clear()
        list.add(IntInsnNode(ALOAD, 1))
        list.add(FieldInsnNode(GETFIELD, "ahb", "v", "Lapu;"))
        list.add(InsnNode(ARETURN))
    }
}
