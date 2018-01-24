package thermos.wrapper

import gnu.trove.map.TLongObjectMap
import net.minecraft.util.LongHashMap

class LongHashMapTrove<T>(private val mMap: TLongObjectMap<T>) : LongHashMap() {

    val numHashElements: Int
        get() = mMap.size()

    fun add(key: Long, value: Any) {
        mMap.put(key, value as T)
    }

    fun getValueByKey(key: Long): Any {
        return mMap.get(key)
    }

    fun containsItem(key: Long): Boolean {
        return mMap.containsKey(key)
    }

    fun remove(key: Long): Any {
        return mMap.remove(key)
    }


}
