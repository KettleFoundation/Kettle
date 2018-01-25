package thermos.thermite

import java.util.Random
import java.util.concurrent.atomic.AtomicLong

/**
 * A subclass of java.util.random that implements the Xorshift random number
 * generator
 *
 * - it is 30% faster than the generator from Java's library - it produces
 * random sequences of higher quality than java.util.Random - this class also
 * provides a clone() function
 *
 * Usage: XSRandom rand = new XSRandom(); //Instantiation x = rand.nextInt();
 * //pull a random number
 *
 * To use the class in legacy code, you may also instantiate an XSRandom object
 * and assign it to a java.util.Random object: java.util.Random rand = new
 * XSRandom();
 *
 * for an explanation of the algorithm, see
 * http://demesos.blogspot.com/2011/09/pseudo-random-number-generators.html
 *
 * @author Wilfried Elmenreich University of Klagenfurt/Lakeside Labs
 * http://www.elmenreich.tk
 *
 * This code is released under the GNU Lesser General Public License Version 3
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 */
class ThermiteRandom
/**
 * Creates a new pseudo random number generator, starting with the specified
 * seed, using `setSeed(seed);`.
 *
 * @param seed the initial seed
 */
@JvmOverloads constructor(private var seed: Long = seedUniquifier() xor System.nanoTime()) : Random() {
    internal var haveNextNextGaussian = false
    internal var nextNextGaussian = 0.0
    override fun nextBoolean(): Boolean {
        return next(1) != 0
    }

    override fun nextDouble(): Double {
        return (((next(26).toLong() shl 27) + next(27)) * DOUBLE_UNIT).toDouble()
    }

    /**
     * Returns the current state of the seed, can be used to clone the object
     *
     * @return the current seed
     */
    @Synchronized
    fun getSeed(): Long {
        return seed
    }

    /**
     * Sets the seed for this pseudo random number generator. As described
     * above, two instances of the same random class, starting with the same
     * seed, produce the same results, if the same methods are called.
     *
     * @param seed the new seed
     */
    @Synchronized
    override fun setSeed(seed: Long) {
        this.seed = seed
    }

    /**
     * @return Returns an XSRandom object with the same state as the original
     */
    fun clone(): ThermiteRandom {
        return ThermiteRandom(getSeed())
    }

    /**
     * Implementation of George Marsaglia's elegant Xorshift random generator
     * 30% faster and better quality than the built-in java.util.random see also
     * see http://www.javamex.com/tutorials/random_numbers/xorshift.shtml
     *
     * @param nbits
     * @return
     */
    public override fun next(nbits: Int): Int {
        var x = seed
        x = x xor (x shl 21)
        x = x xor x.ushr(35)
        x = x xor (x shl 4)
        seed = x
        x = x and (1L shl nbits) - 1
        return x.toInt()
    }

    @Synchronized
    override fun nextGaussian(): Double {
        // See Knuth, ACP, Section 3.4.1 Algorithm C.
        if (haveNextNextGaussian) {
            haveNextNextGaussian = false
            return nextNextGaussian
        } else {
            var v1: Double
            var v2: Double
            var s: Double
            do {
                v1 = 2 * nextDouble() - 1 // between -1 and 1
                v2 = 2 * nextDouble() - 1 // between -1 and 1
                s = v1 * v1 + v2 * v2
            } while (s >= 1 || s == 0.0)
            val multiplier = StrictMath.sqrt(-2 * StrictMath.log(s) / s)
            nextNextGaussian = v2 * multiplier
            haveNextNextGaussian = true
            return v1 * multiplier
        }
    }

    /**
     * Returns a pseudorandom, uniformly distributed `int` value between 0
     * (inclusive) and the specified value (exclusive), drawn from this random
     * number generator's sequence. The general contract of `nextInt` is
     * that one `int` value in the specified range is pseudorandomly
     * generated and returned. All `bound` possible `int` values are
     * produced with (approximately) equal probability. The method
     * `nextInt(int bound)` is implemented by class `Random` as if
     * by:
     * <pre> `public int nextInt(int bound) {
     * if (bound <= 0)
     * throw new IllegalArgumentException("bound must be positive");
     *
     * if ((bound & -bound) == bound)  // i.e., bound is a power of 2
     * return (int)((bound * (long)next(31)) >> 31);
     *
     * int bits, val;
     * do {
     * bits = next(31);
     * val = bits % bound;
     * } while (bits - val + (bound-1) < 0);
     * return val;
     * }`</pre>
     *
     *
     * The hedge "approx
     * imately" is used in the foregoing description only because the next
     * method is only approximately an unbiased source of independently chosen
     * bits. If it were a perfect source of randomly chosen bits, then the
     * algorithm shown would choose `int` values from the stated range
     * with perfect uniformity.
     *
     *
     * The algorithm is slightly tricky. It rejects values that would result in
     * an uneven distribution (due to the fact that 2^31 is not divisible by n).
     * The probability of a value being rejected depends on n. The worst case is
     * n=2^30+1, for which the probability of a reject is 1/2, and the expected
     * number of iterations before the loop terminates is 2.
     *
     *
     * The algorithm treats the case where n is a power of two specially: it
     * returns the correct number of high-order bits from the underlying
     * pseudo-random number generator. In the absence of special treatment, the
     * correct number of *low-order* bits would be returned. Linear
     * congruential pseudo-random number generators such as the one implemented
     * by this class are known to have short periods in the sequence of values
     * of their low-order bits. Thus, this special case greatly increases the
     * length of the sequence of values returned by successive calls to this
     * method if n is a small power of two.
     *
     * @param bound the upper bound (exclusive). Must be positive.
     * @return the next pseudorandom, uniformly distributed `int` value
     * between zero (inclusive) and `bound` (exclusive) from this random
     * number generator's sequence
     * @throws IllegalArgumentException if bound is not positive
     * @since 1.2
     */
    override fun nextInt(bound: Int): Int {
        if (bound < 0) {
            throw RuntimeException("BadBound")
        }

        var r = next(31)
        val m = bound - 1
        if (bound and m == 0)
        // i.e., bound is a power of 2
        {
            r = (bound * r.toLong() shr 31).toInt()
        } else {
            var u = r
            r = u % bound
            while (u - r + m < 0) {
                u = next(31)
            }
        }
        return r
    }

    override fun nextInt(): Int {
        return next(32)
    }

    companion object {

        private val serialVersionUID = 6208727693524452904L
        private val seedUniquifier = AtomicLong(8682522807148012L)

        private fun seedUniquifier(): Long {
            // L'Ecuyer, "Tables of Linear Congruential Generators of
            // Different Sizes and Good Lattice Structure", 1999
            while (true) {
                val current = seedUniquifier.get()
                val next = current * 181783497276652981L
                if (seedUniquifier.compareAndSet(current, next)) {
                    return next
                }
            }
        }

        private val DOUBLE_UNIT = 0x1.0p-53 // 1.0 / (1L << 53)
    }
}/*
     MODIFIED BY: Robotia
     Modification: Implemented Random class seed generator
     */
/**
 * Creates a new pseudo random number generator. The seed is initialized to
 * the current time, as if by
 * `setSeed(System.currentTimeMillis());`.
 */