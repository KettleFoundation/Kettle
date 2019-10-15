package net.techcable.tacospigot;

import it.unimi.dsi.fastutil.ints.AbstractInt2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.*;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.BiConsumer;
import java.util.function.ObjIntConsumer;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A concurrent map of integers to objects with non-locking read operations, and locking write operations.
 * <p>
 * This map is backed by a array, and therefore the amount of overhead this map incurs is equal to the largest key/integer.
 * Therefore you should <i>not use this map if your keys will be large or scattered!</i>
 * However, if the keys are dense and small, this may actually take less memory than a HashMap.
 * </p>
 *
 * @param <V> the type of vales
 */
@ThreadSafe
public final class ArrayMap<V> extends AbstractInt2ObjectMap<V> {
    private static final AtomicIntegerFieldUpdater<ArrayMap> SIZE_UPDATER = AtomicIntegerFieldUpdater.newUpdater(ArrayMap.class, "size");
    private volatile int size;
    @Nonnull
    private volatile AtomicReferenceArray<V> values = new AtomicReferenceArray<>(0);
    private volatile ObjectSet<Int2ObjectMap.Entry<V>> entrySet;
    private volatile ObjectCollection<V> valuesCollection;

    @Override
    public ObjectSet<Int2ObjectMap.Entry<V>> int2ObjectEntrySet() {
        return entrySet != null ? entrySet : (entrySet = new AbstractObjectSet<Int2ObjectMap.Entry<V>>() {
            @Override
            @Nonnull
            public ObjectIterator<Int2ObjectMap.Entry<V>> iterator() {
                return new EntryIterator<Int2ObjectMap.Entry<V>>() {
                    @Override
                    public Int2ObjectMap.Entry<V> next() {
                        return nextEntry();
                    }
                };
            }

            @Override
            public int size() {
                return size;
            }
        });
    }

    @Override
    @Nonnull
    public ObjectCollection<V> values() {
        return valuesCollection != null ? valuesCollection : (valuesCollection = new AbstractObjectCollection<V>() {

            @Override
            @Nonnull
            public ObjectIterator<V> iterator() {
                return new EntryIterator<V>() {
                    @Override
                    @Nonnull
                    public V next() {
                        return nextEntry().getValue();
                    }
                };
            }

            @Override
            public int size() {
                return size;
            }
        });
    }

    @Override
    public V get(int i) {
        AtomicReferenceArray<V> values = this.values;
        return i >= 0 && i < values.length() ? values.get(i) : null;
    }

    @Override
    public synchronized V put(int key, V value) {
        checkNotNull(value, "Null value");
        AtomicReferenceArray<V> values = this.values;
        if (key < 0) {
            throw new IllegalArgumentException(negativeKey(key));
        } else if (key >= values.length()) {
            expandAndPut(key, value); // Put in a separate method for inlining (its a unlikely slow-case)
            return null;
        } else {
            V oldValue = values.getAndSet(key, value);
            if (oldValue == null) SIZE_UPDATER.incrementAndGet(this); // New entry
            return oldValue;
        }
    }

    private void expandAndPut(int key, V value) {
        assert Thread.holdsLock(this);
        AtomicReferenceArray<V> values = this.values;
        AtomicReferenceArray<V> copy = new AtomicReferenceArray<>(key + 1);
        for (int i = 0; i < values.length(); i++) {
            copy.set(i, values.get(i));
        }
        copy.set(key, value);
        this.values = copy;
        SIZE_UPDATER.incrementAndGet(this);
    }

    @Override
    public synchronized V remove(int key) {
        AtomicReferenceArray<V> values = this.values;
        if (key < 0) {
            throw new IllegalArgumentException(negativeKey(key));
        } else if (key >= values.length()) {
            return null;
        } else {
            V oldValue = values.getAndSet(key, null);
            if (oldValue != null)
                SIZE_UPDATER.decrementAndGet(this); // Entry was there before, but now we're removing it
            return oldValue;
        }
    }

    @Override
    public boolean containsKey(int i) {
        AtomicReferenceArray<V> values = this.values;
        return i >= 0 && i < values.length() && values.get(i) != null;
    }

    @Override
    public void forEach(BiConsumer<? super Integer, ? super V> action) {
        forEachPrimitive((value, key) -> action.accept(key, value));
    }

    public void forEachPrimitive(ObjIntConsumer<V> action) {
        AtomicReferenceArray<V> values = this.values;
        for (int index = 0; index < values.length(); index++) {
            V value = values.get(index);
            if (value != null) {
                action.accept(value, index);
            }
        }
    }

    private String negativeKey(int key) {
        return "Can't add a negative key " + key + " to a ArrayMap!";
    }

    @Override
    public int size() {
        return size;
    }

    private abstract class EntryIterator<T> implements ObjectIterator<T> {
        private int index = 0;

        @Override
        public int skip(int toSkip) {
            if (toSkip > values.length()) toSkip = values.length();
            index = toSkip;
            return toSkip;
        }

        @Override
        public boolean hasNext() {
            AtomicReferenceArray<V> values = ArrayMap.this.values;
            while (index < values.length()) {
                V value = values.get(index);
                if (value != null) {
                    return true;
                } else {
                    index++;
                }
            }
            return false;
        }

        /* default */ Int2ObjectMap.Entry<V> nextEntry() {
            AtomicReferenceArray<V> values = ArrayMap.this.values;
            while (index < values.length()) {
                int key = index++;
                V value = values.get(key);
                if (value != null) {
                    return new Entry(key, value);
                }
            }
            throw new NoSuchElementException();
        }
    }

    public class Entry implements Int2ObjectMap.Entry<V> {
        private final int key;
        private V value;

        public Entry(int key, V value) {
            this.key = key;
            this.value = checkNotNull(value, "Null value");
        }

        @Override
        public int getIntKey() {
            return key;
        }

        @Override
        public Integer getKey() {
            return getIntKey();
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            return put(key, this.value = checkNotNull(value, "Null value"));
        }
    }
}
