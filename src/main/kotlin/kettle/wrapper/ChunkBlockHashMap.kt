package thermos.wrapper

import com.koloboke.collect.map.LongObjMap
import com.koloboke.collect.map.hash.HashLongObjMaps
import net.minecraft.world.chunk.Chunk

class ChunkBlockHashMap {

    //private final ConcurrentHashMap<Integer, Chunk[][]> map = new ConcurrentHashMap<Integer, Chunk[][]>();
    private val map = HashLongObjMaps.newMutableMap(1000)
    private var size = 0

    private var last1: Chunk? = null
    private var last2: Chunk? = null
    private var last3: Chunk? = null
    private var last4: Chunk? = null

    /*public static long chunk_hash(int x, int z)
	{
		//return ((x & 0xFFFF) << 16) | (z & 0xFFFF);
//		long key = LongHash.toLong(x, z);
//		return LongHash.toLong((int) (key & 0xFFFFFFFFL), (int) (key >>> 32));
		return (((long)x)<<32L)^z;
	}*/

    /*private static int chunk_array(int index)
	{
		index = index % 16;
		return (index + (index >> 31)) ^ (index >> 31);
	}
	*/
    /*private Chunk[][] chunk_array_get(int x, int z)
	{
		Chunk[][] bunch = this.map.get(chunk_hash(x >> 4, z >> 4));
		return bunch;
	}*/

    /*private Chunk[][] chunk_array_remove(int x, int z)
	{
		Chunk[][] bunch = this.map.remove(chunk_hash(x >> 4, z >> 4));
		return bunch;
	}*/

    fun raw(): LongObjMap<Array<Array<Chunk>>> {
        return this.map
    }

    fun size(): Int {
        return this.size
    }

    fun bulkCheck(coords: Collection<IntArray>): Boolean {
        // FYI: this class repeats a lot of code for a reason. Rather than stupidly jump around methods,
        // this kind of low level, highly-optimized code requires us to avoid raising the stack size and do in-method
        // optimal operations
        var last: Array<Array<Chunk>>? = null // FYI: local field does not hide a class field
        var x = -1
        var z = -1
        for (set in coords) {
            if (last != null) {
                if (set[0] shr 4 == x shr 4 && set[1] shr 4 == z shr 4) {
                    x = set[0]
                    z = set[1]
                    x %= 16
                    z %= 16
                    if (last[x + (x shr 31) xor (x shr 31)][z + (z shr 31) xor (z shr 31)] == null) {
                        return false
                    }
                    x = set[0]
                    z = set[1]
                } else {
                    x = set[0]
                    z = set[1]
                    last = this.map.get((x shr 4).toLong() shl 32L xor (z shr 4))
                    if (last == null) {
                        return false
                    }
                    x %= 16
                    z %= 16
                    if (last!![x + (x shr 31) xor (x shr 31)][z + (z shr 31) xor (z shr 31)] == null) {
                        return false
                    }
                    x = set[0]
                    z = set[1]

                }
            } else {
                x = set[0]
                z = set[1]
                last = this.map.get((x shr 4).toLong() shl 32L xor (z shr 4))

                if (last == null) {
                    return false
                }
                x %= 16
                z %= 16
                if (last!![x + (x shr 31) xor (x shr 31)][z + (z shr 31) xor (z shr 31)] == null) {
                    return false
                }
                x = set[0]
                z = set[1]
            }
        }

        return true
    }

    operator fun get(x: Int, z: Int): Chunk? {
        var x = x
        var z = z
        if (last1 != null && last1!!.xPosition === x && last1!!.zPosition === z) {
            return last1
        }
        if (last2 != null && last2!!.xPosition === x && last2!!.zPosition === z) {
            return last2
        }
        if (last3 != null && last3!!.xPosition === x && last3!!.zPosition === z) {
            return last3
        }
        if (last4 != null && last4!!.xPosition === x && last4!!.zPosition === z) {
            return last4
        }

        val bunch = this.map.get((x shr 4).toLong() shl 32L xor (z shr 4)) ?: return null

        x %= 16
        z %= 16
        val ref = bunch[x + (x shr 31) xor (x shr 31)][z + (z shr 31) xor (z shr 31)]

        if (ref != null) {
            last4 = last3
            last3 = last2
            last2 = last1
            last1 = ref
        }
        return ref
    }

    fun put(chunk: Chunk?) {
        if (chunk == null)
            return
        size++
        var x = chunk.xPosition
        var z = chunk.zPosition

        val chunkhash = (x shr 4).toLong() shl 32L xor (z shr 4)

        var temp_chunk_bunch = this.map.get(chunkhash)

        x %= 16
        z %= 16

        x = x + (x shr 31) xor (x shr 31)
        z = z + (z shr 31) xor (z shr 31)

        if (temp_chunk_bunch != null) {
            temp_chunk_bunch!![x][z] = chunk
        } else {
            temp_chunk_bunch = Array<Array<Chunk>>(16) { arrayOfNulls(16) }
            temp_chunk_bunch!![x][z] = chunk
            this.map.put(chunkhash, temp_chunk_bunch) //Thermos
        }
        if (chunk != null) {
            last4 = last3
            last3 = last2
            last2 = last1
            last1 = chunk
        }
    }

    fun remove(chunk: Chunk) {
        var x = chunk.xPosition
        var z = chunk.zPosition
        val temp_chunk_bunch = this.map.get((x shr 4).toLong() shl 32L xor (z shr 4))

        x %= 16
        z %= 16

        x = x + (x shr 31) xor (x shr 31)
        z = z + (z shr 31) xor (z shr 31)

        if (temp_chunk_bunch != null) {
            if (temp_chunk_bunch!![x][z] != null) {
                size--
                temp_chunk_bunch!![x][z] = null
            }
        }
        if (last1 != null && last1!!.xPosition === chunk.xPosition && last1!!.zPosition === chunk.zPosition) {
            last1 = null
            last1 = last2
            last2 = last3
            last3 = last4
            last4 = null
        }
        if (last2 != null && last2!!.xPosition === chunk.xPosition && last2!!.zPosition === chunk.zPosition) {
            last2 = null
            last2 = last3
            last3 = last4
            last4 = null
        }
        if (last3 != null && last3!!.xPosition === chunk.xPosition && last3!!.zPosition === chunk.zPosition) {
            last3 = null
            last3 = last4
            last4 = null
        }
        if (last4 != null && last4!!.xPosition === chunk.xPosition && last4!!.zPosition === chunk.zPosition) {
            last4 = null
        }
    }
}
