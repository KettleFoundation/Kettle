/*
 * Copyright (C) 2016 Robotia.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package thermos.wrapper

import java.util.*
import net.minecraft.world.ChunkPosition

/**
 *
 * @author Robotia
 */
class ThermiteMap : Map<*, *> {
    private val map = HashMap<Int, Array<Array<Any>>>()
    private val original = HashMap<Any, Any>()
    private var size = 0

    init {
        for (i in 0..256) {
            map[i] = Array(16) { arrayOfNulls(16) }
        }
    }

    override fun size(): Int {
        return original.size
    }

    override fun isEmpty(): Boolean {
        return original.isEmpty()
    }

    override fun containsKey(key: Any): Boolean {
        if (!isValid(key)) return false
        val coords = getCoords(key)
        return map[coords[1]][coords[0]][coords[2]] != null
    }

    override fun containsValue(value: Any): Boolean {
        return original.containsValue(value)
    }

    override operator fun get(key: Any): Any? {
        if (!isValid(key)) return null
        val coords = getCoords(key)
        return map[coords[1]][coords[0]][coords[2]]
    }

    override fun put(key: Any, value: Any): Any? {
        if (!isValid(key)) return null
        val coords = getCoords(key)

        val instance = original.put(key, value)
        map[coords[1]][coords[0]][coords[2]] = value
        size++
        return instance
    }

    override fun remove(key: Any): Any? {
        if (!isValid(key)) return null
        val coords = getCoords(key)

        val instance = original.remove(key)
        map[coords[1]][coords[0]][coords[2]] = null
        size--
        return instance
    }

    override fun putAll(m: Map<*, *>) {
        this.original.putAll(m)
    }

    override fun clear() {
        original.clear()
        map.clear()
    }

    override fun keySet(): Set<*> {
        return this.original.keys
    }

    override fun values(): Collection<*> {
        return original.values
    }

    override fun entrySet(): Set<*> {
        return this.original.entries
    }

    companion object {

        fun isValid(o: Any): Boolean {
            return o is ChunkPosition
        }

        fun getCoords(o: Any): IntArray {
            val cp = o as ChunkPosition
            return intArrayOf(cp.chunkPosX, cp.chunkPosY, cp.chunkPosZ)
        }
    }

}

