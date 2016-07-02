@file:Suppress("NOTHING_TO_INLINE")

package ktx.collections

import com.badlogic.gdx.utils.IntSet
import com.badlogic.gdx.utils.ObjectSet

/**
 * Default LibGDX set size used by most constructors.
 */
val defaultSetSize = 51
/**
 * Default LibGDX map and set load factor used by most constructors.
 */
val defaultLoadFactor = 0.8f

/**
 * @param initialCapacity initial capacity of the set. Will be resized if necessary.
 * @param loadFactor decides under what load the set is resized.
 * @return a new [ObjectSet].
 */
fun <Type> gdxSetOf(initialCapacity: Int = defaultSetSize, loadFactor: Float = defaultLoadFactor): ObjectSet<Type> =
    ObjectSet(initialCapacity, loadFactor)

/**
 * @param elements will be initially stored in the set.
 * @param initialCapacity initial capacity of the set. Will be resized if necessary.
 * @param loadFactor decides under what load the set is resized.
 * @return a new [ObjectSet].
 */
inline fun <Type> gdxSetOf(vararg elements: Type, initialCapacity: Int = defaultSetSize, loadFactor: Float = defaultLoadFactor):
    ObjectSet<Type> {
  val set = ObjectSet<Type>(initialCapacity, loadFactor)
  set.addAll(*elements)
  return set
}

/**
 * A method wrapper over [ObjectSet.size] variable compatible with nullable types.
 * @return current amount of elements in the set.
 */
inline fun <Type> ObjectSet<Type>?.size(): Int = this?.size ?: 0

/**
 * @return true if the set is null or has no elements.
 */
inline fun <Type> ObjectSet<Type>?.isEmpty(): Boolean = this == null || this.size == 0

/**
 * @return true if the set is not null and contains at least one element.
 */
inline fun <Type> ObjectSet<Type>?.isNotEmpty(): Boolean = this != null && this.size > 0

/**
 * @param elements will be iterated over and added to the set.
 */
fun <Type> ObjectSet<Type>.addAll(elements: Iterable<Type>) =
    elements.forEach { this.add(it) }


/**
 * @param elements will be iterated over and removed from the set.
 */
fun <Type> ObjectSet<Type>.removeAll(elements: Iterable<Type>) =
    elements.forEach { this.remove(it) }


/**
 * @param elements will be iterated over and removed from the set.
 */
fun <Type> ObjectSet<Type>.removeAll(elements: Array<out Type>) =
    elements.forEach { this.remove(it) }


/**
 * Allows to append elements to sets with pleasant, chainable `set + element0 + element1` syntax.
 * @param element will be added to the set.
 * @return this set.
 */
operator fun <Type> ObjectSet<Type>.plus(element: Type): ObjectSet<Type> {
  this.add(element)
  return this
}

/**
 * Allows to quickly add all elements of another iterable to this set with a pleasant, chainable operator syntax.
 * @param elements will be added to the set.
 * @return this set.
 */
operator fun <Type> ObjectSet<Type>.plus(elements: Iterable<Type>): ObjectSet<Type> {
  this.addAll(elements)
  return this
}

/**
 * Allows to quickly add all elements of an array to this set with a pleasant, chainable operator syntax.
 * @param elements will be added to the set.
 * @return this set.
 */
operator fun <Type> ObjectSet<Type>.plus(elements: Array<out Type>): ObjectSet<Type> {
  this.addAll(elements, 0, elements.size)
  return this
}

/**
 * Allows to remove elements from sets with pleasant, chainable `set - element0 - element1` syntax.
 * @param element will be removed from the set.
 * @return this set.
 */
operator fun <Type> ObjectSet<Type>.minus(element: Type): ObjectSet<Type> {
  this.remove(element)
  return this
}

/**
 * Allows to quickly remove all elements of another iterable from this set with a pleasant, chainable operator syntax.
 * @param elements will be removed from the set.
 * @return this set.
 */
operator fun <Type> ObjectSet<Type>.minus(elements: Iterable<Type>): ObjectSet<Type> {
  this.removeAll(elements)
  return this
}

/**
 * Allows to quickly remove all elements of an array from this set with a pleasant, chainable operator syntax.
 * @param elements will be removed from the set.
 * @return this set.
 */
operator fun <Type> ObjectSet<Type>.minus(elements: Array<out Type>): ObjectSet<Type> {
  this.removeAll(elements)
  return this
}

/**
 * Allows to iterate over the array with access to [MutableIterator], which allows to remove elements from the collection
 * during iteration.
 * @param action will be invoked for each set element. Allows to remove elements during iteration. The first function
 *      argument is the element from the set, the second is the set iterator. The iterator is guaranteed to be the same
 *      instance during one iteration.
 */
inline fun <Type> ObjectSet<Type>.iterate(action: (Type, MutableIterator<Type>) -> Unit) {
  val iterator = iterator()
  while (iterator.hasNext()) action.invoke(iterator.next(), iterator)
}

/**
 * @param ordered if false, methods that remove elements may change the order of other elements in the array,
 *      which avoids a memory copy.
 * @param initialCapacity initial size of the backing array. Defaults to set size.
 * @return values copied from this set stored in a LibGDX array.
 */
inline fun <reified Type : Any> ObjectSet<Type>.toGdxArray(ordered: Boolean = true, initialCapacity: Int = this.size):
    com.badlogic.gdx.utils.Array<Type> {
  val array = com.badlogic.gdx.utils.Array<Type>(ordered, initialCapacity, Type::class.java)
  array.addAll(this)
  return array
}

/**
 * @param initialCapacity initial capacity of the set. Will be resized if necessary.
 * @param loadFactor decides how many elements the set might contain in relation to its total capacity before it is resized.
 * @return values copied from this iterable stored in a LibGDX set.
 */
fun <Type> Iterable<Type>.toGdxSet(initialCapacity: Int = defaultSetSize, loadFactor: Float = defaultLoadFactor):
    ObjectSet<Type> {
  val set = ObjectSet<Type>(initialCapacity, loadFactor)
  set.addAll(this)
  return set
}

/**
 * @param initialCapacity initial capacity of the set. Will be resized if necessary. Defaults to this array size.
 * @param loadFactor decides how many elements the set might contain in relation to its total capacity before it is resized.
 * @return values copied from this iterable stored in a LibGDX set.
 */
fun <Type> Array<Type>.toGdxSet(initialCapacity: Int = this.size, loadFactor: Float = defaultLoadFactor):
    ObjectSet<Type> = gdxSetOf(*this, initialCapacity = initialCapacity, loadFactor = loadFactor)

/**
 * @param initialCapacity initial capacity of the set. Will be resized if necessary. Defaults to this array size.
 * @param loadFactor decides how many elements the set might contain in relation to its total capacity before it is resized.
 * @return values copied from this iterable stored in an optimized LibGDX int set.
 */
fun IntArray.toGdxSet(initialCapacity: Int = this.size, loadFactor: Float = defaultLoadFactor): IntSet {
  val set = IntSet(initialCapacity, loadFactor)
  set.addAll(this, 0, this.size)
  return set
}
