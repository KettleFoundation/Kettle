package thermos.chaud

import java.util.*

class HashedArrayList<TileEntity> : ArrayList<TileEntity>() {

    private val hashed = Collections.synchronizedSet(LinkedHashSet<TileEntity>())

    override fun add(arg0: TileEntity?): Boolean {
        val flag = hashed.add(arg0)

        if (flag)
            super.add(arg0)

        return flag
    }

    override fun add(arg0: Int, arg1: TileEntity?) {
        val flag = hashed.add(arg1)

        if (flag)
            super.add(arg0, arg1)
    }

    override fun addAll(arg0: Collection<*>): Boolean {
        val flag = hashed.addAll(arg0)

        if (flag)
            super.addAll(arg0)

        return flag
    }

    override fun addAll(arg0: Int, arg1: Collection<*>): Boolean {
        val flag = hashed.addAll(arg1)

        if (flag)
            super.addAll(arg0, arg1)

        return flag
    }

    override fun clear() {
        this.hashed.clear()
        super.clear()
    }

    override operator fun contains(arg0: Any?): Boolean {
        return hashed.contains(arg0)
    }

    override fun containsAll(arg0: Collection<*>): Boolean {
        return hashed.containsAll(arg0)
    }

    override fun get(arg0: Int): TileEntity {
        return super.get(arg0)
    }

    override fun indexOf(arg0: Any?): Int {
        return super.indexOf(arg0)
    }

    override fun isEmpty(): Boolean {
        return super.isEmpty()
    }

    override fun iterator(): Iterator<TileEntity> {
        return HashedArrayIterator(super.iterator(), this.hashed)
    }

    override fun lastIndexOf(arg0: Any?): Int {
        return if (this.hashed.contains(arg0)) {
            super.lastIndexOf(arg0)
        } else {
            -1
        }
    }

    override fun listIterator(): ListIterator<*> {
        return this.listIterator()
    }

    override fun listIterator(arg0: Int): ListIterator<*> {
        return super.listIterator(arg0)
    }

    override fun remove(arg0: Any?): Boolean {
        val flag = this.hashed.remove(arg0)
        if (flag)
            super.remove(arg0)
        return flag
    }

    override fun remove(arg0: Int): TileEntity? {
        val te = super.removeAt(arg0)

        if (te != null)
            hashed.remove(te)

        return te
    }

    override fun removeAll(arg0: Collection<*>): Boolean {
        val flag = this.hashed.removeAll(arg0)

        if (flag) {
            super.clear()
            super.addAll(this.hashed)
        }

        return flag
    }

    override fun retainAll(arg0: Collection<*>): Boolean {
        val flag = this.hashed.retainAll(arg0)

        if (flag)
            super.retainAll(arg0)

        return flag
    }

    override operator fun set(arg0: Int, arg1: TileEntity?): TileEntity? {
        val te = super.set(arg0, arg1)

        if (te != null)
            this.hashed.remove(arg1)

        return te
    }

    override fun size(): Int {
        return super.size
    }

    override fun subList(arg0: Int, arg1: Int): List<TileEntity> {
        return super.subList(arg0, arg1)
    }

    override fun toArray(): Array<Any> {
        return super.toTypedArray()
    }

    override fun toArray(arg0: Array<Any>): Array<Any> {
        return super.toTypedArray<Any>()
    }

    internal inner class HashedArrayIterator<TileEntity>(aritr: MutableIterator<*>, var teset: MutableSet<TileEntity>) : Iterator<TileEntity> {
        var aritr: MutableIterator<TileEntity>

        private var last: TileEntity? = null

        init {
            this.aritr = aritr
        }

        override fun hasNext(): Boolean {
            return aritr.hasNext()
        }

        override fun next(): TileEntity? {
            last = aritr.next()
            return last
        }

        override fun remove() {
            aritr.remove()
            teset.remove(last)
        }
    }

    internal inner class HashedArrayListIterator<TileEntity>(var aritr: MutableListIterator<TileEntity>, teset: HashSet<TileEntity>) : ListIterator<TileEntity> {
        var teset: HashSet<TileEntity>? = null

        private var lastRet: TileEntity? = null
        override fun add(arg0: TileEntity) {
            val flag = teset!!.add(arg0)
            if (flag)
                this.aritr.add(arg0)
        }

        override fun hasNext(): Boolean {
            return aritr.hasNext()
        }

        override fun hasPrevious(): Boolean {
            return aritr.hasPrevious()
        }

        override fun next(): TileEntity? {
            lastRet = aritr.next()
            return lastRet
        }

        override fun nextIndex(): Int {
            return aritr.nextIndex()
        }

        override fun previous(): TileEntity? {
            lastRet = aritr.previous()
            return lastRet
        }

        override fun previousIndex(): Int {
            return aritr.previousIndex()
        }

        override fun remove() {
            aritr.remove()
            teset!!.remove(lastRet)

        }

        override fun set(arg0: TileEntity) {
            aritr.set(arg0)
            teset!!.remove(lastRet)
            teset!!.add(arg0)

        }
    }
}
