@file:Suppress("NOTHING_TO_INLINE", "LoopToCallChain")

package ktx.collections

import com.badlogic.gdx.utils.IntSet
import com.badlogic.gdx.utils.ObjectSet
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/** Alias for [com.badlogic.gdx.utils.ObjectSet]. Added for consistency with other collections and factory methods. */
typealias GdxSet<Element> = ObjectSet<Element>

/**
 * Default libGDX set size used by most constructors.
 */
const val defaultSetSize = 51

/**
 * Default libGDX map and set load factor used by most constructors.
 */
const val defaultLoadFactor = 0.8f

/**
 * @param initialCapacity initial capacity of the set. Will be resized if necessary.
 * @param loadFactor decides under what load the set is resized.
 * @return a new [ObjectSet].
 */
fun <Type> gdxSetOf(
  initialCapacity: Int = defaultSetSize,
  loadFactor: Float = defaultLoadFactor,
): GdxSet<Type> = GdxSet(initialCapacity, loadFactor)

/**
 * @param elements will be initially stored in the set.
 * @param initialCapacity initial capacity of the set. Will be resized if necessary.
 * @param loadFactor decides under what load the set is resized.
 * @return a new [ObjectSet].
 */
inline fun <Type> gdxSetOf(
  vararg elements: Type,
  initialCapacity: Int = defaultSetSize,
  loadFactor: Float = defaultLoadFactor,
): GdxSet<Type> {
  val set = GdxSet<Type>(initialCapacity, loadFactor)
  set.addAll(*elements)
  return set
}

/**
 * A method wrapper over [ObjectSet.size] variable compatible with nullable types.
 * @return current amount of elements in the set.
 */
inline fun <Type> GdxSet<Type>?.size(): Int = this?.size ?: 0

/**
 * @return true if the set is null or has no elements.
 */
@OptIn(ExperimentalContracts::class)
inline fun <Type> GdxSet<Type>?.isEmpty(): Boolean {
  contract {
    returns(false) implies (this@isEmpty != null)
  }
  return this == null || this.size == 0
}

/**
 * @return true if the set is not null and contains at least one element.
 */
@OptIn(ExperimentalContracts::class)
inline fun <Type> GdxSet<Type>?.isNotEmpty(): Boolean {
  contract {
    returns(true) implies (this@isNotEmpty != null)
  }
  return this != null && this.size > 0
}

/**
 * @param elements will be iterated over and added to the set.
 */
fun <Type> GdxSet<Type>.addAll(elements: Iterable<Type>) = elements.forEach { this.add(it) }

/**
 * @param elements will be iterated over and removed from the set.
 */
fun <Type> GdxSet<Type>.removeAll(elements: Iterable<Type>) = elements.forEach { this.remove(it) }

/**
 * @param elements will be iterated over and removed from the set.
 */
fun <Type> GdxSet<Type>.removeAll(elements: Array<out Type>) = elements.forEach { this.remove(it) }

/**
 * Allows to append elements to sets with `set + element` syntax.
 * @param element will be added to the result set.
 * @return a new [GdxSet] with elements from this set and [element].
 */
operator fun <Type> GdxSet<Type>.plus(element: Type): GdxSet<Type> {
  val result = GdxSet(this)
  result.add(element)
  return result
}

/**
 * Allows to add all elements of another iterable to this set with + operator syntax.
 * @param elements will be added to the result set.
 * @return a new [GdxSet] with elements from this set and [elements].
 */
operator fun <Type> GdxSet<Type>.plus(elements: Iterable<Type>): GdxSet<Type> {
  val result = GdxSet(this)
  result.addAll(elements)
  return result
}

/**
 * Allows to add all elements of an array to this set with + operator syntax.
 * @param elements will be added to the result set.
 * @return a new [GdxSet] with elements from this set and [elements].
 */
operator fun <Type> GdxSet<Type>.plus(elements: Array<out Type>): GdxSet<Type> {
  val result = GdxSet(this)
  result.addAll(elements, 0, elements.size)
  return result
}

/**
 * Allows to append elements to sets with += operator syntax.
 * @param element will be added to the set.
 */
operator fun <Type> GdxSet<Type>.plusAssign(element: Type) {
  add(element)
}

/**
 * Allows to add all elements of another iterable to this set with += operator syntax.
 * @param elements will be added to the set.
 */
operator fun <Type> GdxSet<Type>.plusAssign(elements: Iterable<Type>) {
  this.addAll(elements)
}

/**
 * Allows to add all elements of an array to this set with += operator syntax.
 * @param elements will be added to the set.
 * @return this set.
 */
operator fun <Type> GdxSet<Type>.plusAssign(elements: Array<out Type>) {
  addAll(elements, 0, elements.size)
}

/**
 * Allows to remove elements from sets with `set - element` syntax.
 * @param element will not be added to the new set.
 * @return a new [GdxSet] with elements from this set except for the [element].
 */
operator fun <Type> GdxSet<Type>.minus(element: Type): GdxSet<Type> {
  val result = GdxSet(this)
  result.remove(element)
  return result
}

/**
 * Allows to remove all elements of another iterable from this set with - operator syntax.
 * @param elements will not be added to the new set.
 * @return a new [GdxSet] with elements from this set except for the [elements].
 */
operator fun <Type> GdxSet<Type>.minus(elements: Iterable<Type>): GdxSet<Type> {
  val result = GdxSet(this)
  result.removeAll(elements)
  return result
}

/**
 * Allows to remove all elements of an array from this set with - operator syntax.
 * @param elements will not be added to the new set.
 * @return a new [GdxSet] with elements from this set except for the [elements].
 */
operator fun <Type> GdxSet<Type>.minus(elements: Array<out Type>): GdxSet<Type> {
  val result = GdxSet(this)
  result.removeAll(elements)
  return result
}

/**
 * Allows to remove elements from sets -= operator syntax.
 * @param element will be removed from this set.
 */
operator fun <Type> GdxSet<Type>.minusAssign(element: Type) {
  remove(element)
}

/**
 * Allows to remove all elements of another iterable from this set with -= operator syntax.
 * @param elements will be removed from the set.
 */
operator fun <Type> GdxSet<Type>.minusAssign(elements: Iterable<Type>) {
  removeAll(elements)
}

/**
 * Allows to remove all elements of an array from this set with -= operator syntax.
 * @param elements will be removed from the set.
 */
operator fun <Type> GdxSet<Type>.minusAssign(elements: Array<out Type>) {
  removeAll(elements)
}

/**
 * Allows to iterate over the array with access to [MutableIterator], which allows to remove elements from the collection
 * during iteration.
 * @param action will be invoked for each set element. Allows to remove elements during iteration. The first function
 *      argument is the element from the set, the second is the set iterator. The iterator is guaranteed to be the same
 *      instance during one iteration.
 */
inline fun <Type> GdxSet<Type>.iterate(action: (Type, MutableIterator<Type>) -> Unit) {
  val iterator = iterator()
  while (iterator.hasNext) action(iterator.next(), iterator)
}

/**
 * Returns a [GdxSet] containing the results of applying the given [transform] function
 * to each entry in the original [GdxSet].
 */
inline fun <Type, R> GdxSet<Type>.map(transform: (Type) -> R): GdxSet<R> {
  val destination = GdxSet<R>(this.size)
  for (item in this) {
    destination.add(transform(item))
  }
  return destination
}

/**
 * Returns a [GdxSet] containing only elements matching the given [predicate].
 */
inline fun <Type> GdxSet<Type>.filter(predicate: (Type) -> Boolean): GdxSet<Type> {
  val destination = GdxSet<Type>()
  for (item in this) {
    if (predicate(item)) {
      destination.add(item)
    }
  }
  return destination
}

/**
 * Returns a single [GdxSet] of all elements from all collections in the given [GdxSet].
 */
inline fun <Type, C : Iterable<Type>> GdxSet<out C>.flatten(): GdxSet<Type> {
  val destination = GdxSet<Type>()
  for (item in this) {
    destination.addAll(item)
  }
  return destination
}

/**
 * Returns a single [GdxSet] of all elements yielded from results of transform function being invoked
 * on each element of original [GdxSet].
 */
inline fun <Type, R> GdxSet<Type>.flatMap(transform: (Type) -> Iterable<R>): GdxSet<R> = this.map(transform).flatten()

/**
 * @param ordered if false, methods that remove elements may change the order of other elements in the array,
 *      which avoids a memory copy.
 * @param initialCapacity initial size of the backing array. Defaults to set size.
 * @return values copied from this set stored in a libGDX array.
 */
inline fun <reified Type : Any> GdxSet<Type>.toGdxArray(
  ordered: Boolean = true,
  initialCapacity: Int = this.size,
): GdxArray<Type> {
  val array =
    com.badlogic.gdx.utils
      .Array<Type>(ordered, initialCapacity, Type::class.java)
  array.addAll(this)
  return array
}

/**
 * @param initialCapacity initial capacity of the set. Will be resized if necessary.
 * @param loadFactor decides how many elements the set might contain in relation to its total capacity before it is resized.
 * @return values copied from this iterable stored in a libGDX set.
 */
fun <Type> Iterable<Type>.toGdxSet(
  initialCapacity: Int = defaultSetSize,
  loadFactor: Float = defaultLoadFactor,
): GdxSet<Type> {
  val set = GdxSet<Type>(initialCapacity, loadFactor)
  set.addAll(this)
  return set
}

/**
 * @param initialCapacity initial capacity of the set. Will be resized if necessary. Defaults to this array size.
 * @param loadFactor decides how many elements the set might contain in relation to its total capacity before it is resized.
 * @return values copied from this iterable stored in a libGDX set.
 */
fun <Type> Array<Type>.toGdxSet(
  initialCapacity: Int = this.size,
  loadFactor: Float = defaultLoadFactor,
): GdxSet<Type> = gdxSetOf(*this, initialCapacity = initialCapacity, loadFactor = loadFactor)

/**
 * @param initialCapacity initial capacity of the set. Will be resized if necessary. Defaults to this array size.
 * @param loadFactor decides how many elements the set might contain in relation to its total capacity before it is resized.
 * @return values copied from this iterable stored in an optimized libGDX int set.
 */
fun IntArray.toGdxSet(
  initialCapacity: Int = this.size,
  loadFactor: Float = defaultLoadFactor,
): IntSet {
  val set = IntSet(initialCapacity, loadFactor)
  set.addAll(this, 0, this.size)
  return set
}
