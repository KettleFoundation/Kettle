package kettle

class BlockCoords {
    val x: Int
    val y: Int
    val z: Int
    val key: Long
    private val hash: Int

    constructor(x: Int, y: Int, z: Int) {
        this.x = x
        this.y = y
        this.z = z

        key = y.toLong() shl 56 or (z.toLong() and 0xFFFFFFF shl 28) or (x and 0xFFFFFFF).toLong()
        hash = (key xor key.ushr(32)).toInt()
    }

    constructor(coords: BlockCoords) {
        this.x = coords.x
        this.y = coords.y
        this.z = coords.z
        this.key = coords.key
        this.hash = coords.hash
    }

    override fun equals(obj: Any?): Boolean {
        if (obj === this)
            return true
        if (obj !is BlockCoords)
            return false
        val coords = obj as BlockCoords?
        return x == coords!!.x && y == coords.y && z == coords.z
    }

    override fun hashCode(): Int {
        return hash
    }
}
