package kettle.wrapper

import gnu.trove.map.TLongObjectMap
import net.minecraft.util.LongHashMap
import net.minecraft.world.ChunkCoordIntPair
import net.minecraft.world.chunk.Chunk

import java.util.HashMap
import java.util.concurrent.*

import org.bukkit.craftbukkit.util.LongHash

class VanillaChunkHashMap : LongHashMap {
    private var chunkt_TH: ChunkBlockHashMap? = null
    private var vanilla: ConcurrentHashMap<Long, Chunk>? = null

    private val notRealFace = false

    constructor(chunkt_TH: ChunkBlockHashMap) {
        this.chunkt_TH = chunkt_TH
        this.vanilla = ConcurrentHashMap()
    }

    constructor(chunkt_TH: ChunkBlockHashMap, vanilla: ConcurrentHashMap<Long, Chunk>) {
        this.chunkt_TH = chunkt_TH
        this.vanilla = vanilla
        this.notRealFace = true
    }

    fun thisIsNotMyRealFace(): VanillaChunkHashMap {
        return VanillaChunkHashMap(chunkt_TH, vanilla)
    }

    private fun V2B(key: Long): Long {
        return if (notRealFace) {
            key
        } else {
            ChunkCoordIntPair.chunkXZ2Int(LongHash.msw(key), LongHash.lsw(key))
        }
        //return LongHash.toLong((int) (key & 0xFFFFFFFFL), (int) (key >>> 32));
    }

    fun rawVanilla(): ConcurrentHashMap<Long, Chunk>? {
        return vanilla
    }

    fun rawThermos(): ChunkBlockHashMap? {
        return chunkt_TH
    }

    fun size(): Int {
        return this.chunkt_TH!!.size()
    }

    fun add(key: Long, value: Any) {
        if (value is Chunk) {
            chunkt_TH!!.put(value)
            vanilla!![V2B(key)] = value
        }
    }


    fun containsItem(key: Long): Boolean {
        return vanilla!!.containsKey(V2B(key))
    }

    fun getValueByKey(key: Long): Any {
        return vanilla!![V2B(key)]
    }

    fun remove(key: Long): Any {
        val o = vanilla!!.remove(V2B(key))
        if (o is Chunk)
        // Thermos - Use our special map
        {
            chunkt_TH!!.remove(o)
        }
        return o
    }

}
