@file:Suppress("NOTHING_TO_INLINE")

package ktx.collections

import com.badlogic.gdx.utils.ObjectSet
import com.badlogic.gdx.utils.Array as GdxArray
import com.badlogic.gdx.utils.BooleanArray as GdxBooleanArray
import com.badlogic.gdx.utils.FloatArray as GdxFloatArray
import com.badlogic.gdx.utils.IntArray as GdxIntArray

/**
 * Default LibGDX array size used by most constructors.
 */
const val defaultArraySize = 16

/**
 * @param ordered if false, methods that remove elements may change the order of other elements in the array,
 *      which avoids a memory copy.
 * @param initialCapacity initial size of the backing array.
 * @return a new instance of [Array].
 */
inline fun <reified Type : Any> gdxArrayOf(ordered: Boolean = true, initialCapacity: Int = defaultArraySize): GdxArray<Type> =
    GdxArray(ordered, initialCapacity, Type::class.java)

/**
 * @param elements will be initially stored in the array.
 * @return a new instance of [Array].
 */
inline fun <Type : Any> gdxArrayOf(vararg elements: Type): GdxArray<Type> = GdxArray(elements)

/**
 * A method wrapper over [Array.size] variable compatible with nullable types.
 * @return current amount of elements in the array.
 */
inline fun <Type> GdxArray<Type>?.size(): Int = this?.size ?: 0

/**
 * @return true if the array is null or has no elements.
 */
inline fun <Type> GdxArray<Type>?.isEmpty(): Boolean = this == null || this.size == 0

/**
 * @return true if the array is not null and contains at least one element.
 */
inline fun <Type> GdxArray<Type>?.isNotEmpty(): Boolean = this != null && this.size > 0

/**
 * @return currently last element in the array or null if empty.
 */
fun <Type> GdxArray<Type>.getLast(): Type? = if (this.size == 0) null else this.get(this.size - 1)

/**
 * @return previously last element that was removed from the array or null if empty.
 */
fun <Type> GdxArray<Type>.removeLast(): Type? {
  if (this.size == 0) {
    return null
  }
  val index = --this.size
  val previous = this.items[index]
  this.items[index] = null
  return previous
}

/**
 * @param index index of the element in the array.
 * @param alternative returned if index is out of bounds or the element is null.
 * @return a non-null value of stored element or the alternative.
 */
fun <Type> GdxArray<Type>.getOrElse(index: Int, alternative: Type): Type {
  if (index >= this.size) return alternative
  val element = this[index]
  return element ?: alternative
}

/**
 * @param elements will be iterated over and added to the array.
 */
fun <Type> GdxArray<Type>.addAll(elements: Iterable<Type>) =
    elements.forEach { this.add(it) }


/**
 * @param elements will be iterated over and removed from the array.
 * @param identity if true, values will be compared by references. If false, equals method will be invoked.
 */
fun <Type> GdxArray<Type>.removeAll(elements: Iterable<Type>, identity: Boolean = false) =
    elements.forEach { this.removeValue(it, identity) }


/**
 * @param elements will be iterated over and removed from the array.
 * @param identity if true, values will be compared by references. If false, equals method will be invoked.
 */
fun <Type> GdxArray<Type>.removeAll(elements: Array<out Type>, identity: Boolean = false) =
    elements.forEach { this.removeValue(it, identity) }


/**
 * Allows to append elements to arrays with pleasant, chainable `array + element0 + element1` syntax.
 * @param element will be added to the array.
 * @return this array.
 */
operator fun <Type> GdxArray<Type>.plus(element: Type): GdxArray<Type> {
  this.add(element)
  return this
}

/**
 * Allows to quickly add all elements of another iterable to this array with a pleasant, chainable operator syntax.
 * @param elements will be added to the array.
 * @return this array.
 */
operator fun <Type> GdxArray<Type>.plus(elements: Iterable<Type>): GdxArray<Type> {
  this.addAll(elements)
  return this
}

/**
 * Allows to quickly add all elements of a native array to this array with a pleasant, chainable operator syntax.
 * @param elements will be added to the array.
 * @return this array.
 */
operator fun <Type> GdxArray<Type>.plus(elements: Array<out Type>): GdxArray<Type> {
  this.addAll(elements, 0, elements.size)
  return this
}

/**
 * Allows to remove elements from arrays with pleasant, chainable `array - element0 - element1` syntax.
 * @param element will be removed from the array.
 * @return this array.
 */
operator fun <Type> GdxArray<Type>.minus(element: Type): GdxArray<Type> {
  this.removeValue(element, false)
  return this
}

/**
 * Allows to quickly remove all elements of another iterable from this array with a pleasant, chainable operator syntax.
 * @param elements will be removed from the array.
 * @return this array.
 */
operator fun <Type> GdxArray<Type>.minus(elements: Iterable<Type>): GdxArray<Type> {
  this.removeAll(elements)
  return this
}

/**
 * Allows to quickly remove all elements of a native array from this array with a pleasant, chainable operator syntax.
 * @param elements will be removed from the array.
 * @return this array.
 */
operator fun <Type> GdxArray<Type>.minus(elements: Array<out Type>): GdxArray<Type> {
  this.removeAll(elements)
  return this
}

/**
 * Allows to check if an array contains an element using the "in" operator.
 * @param element might be in the array.
 * @return true if the element is equal to any value stored in the array.
 */
operator fun <Type> GdxArray<Type>.contains(element: Type): Boolean = this.contains(element, false)

/**
 * Allows to iterate over the array with access to [MutableIterator], which allows to remove elements from the collection
 * during iteration.
 * @param action will be invoked for each array element. Allows to remove elements during iteration. The first function
 *      argument is the element from the array, the second is the array iterator. The iterator is guaranteed to be the
 *      same instance during one iteration.
 */
inline fun <Type> GdxArray<Type>.iterate(action: (Type, MutableIterator<Type>) -> Unit) {
  val iterator = iterator()
  while (iterator.hasNext()) action.invoke(iterator.next(), iterator)
}

/**
 * @param initialCapacity initial capacity of the set. Will be resized if necessary. Defaults to array size.
 * @param loadFactor decides how many elements the set might contain in relation to its total capacity before it is resized.
 * @return values copied from this array stored in a LibGDX set.
 */
fun <Type : Any> GdxArray<Type>.toGdxSet(initialCapacity: Int = this.size, loadFactor: Float = defaultLoadFactor):
    ObjectSet<Type> {
  val set = ObjectSet<Type>(initialCapacity, loadFactor)
  set.addAll(this)
  return set
}

/**
 * @param ordered if false, methods that remove elements may change the order of other elements in the array,
 *      which avoids a memory copy.
 * @param initialCapacity initial size of the backing array.
 * @return values copied from this iterable stored in a LibGDX array.
 */
inline fun <reified Type : Any> Iterable<Type>.toGdxArray(ordered: Boolean = true, initialCapacity: Int = defaultArraySize):
    GdxArray<Type> {
  val array = GdxArray<Type>(ordered, initialCapacity, Type::class.java)
  array.addAll(this)
  return array
}

/**
 * @param ordered if false, methods that remove elements may change the order of other elements in the array,
 *      which avoids a memory copy.
 * @param initialCapacity initial size of the backing array. Defaults to this array size.
 * @return values copied from this array stored in a LibGDX array.
 */
inline fun <reified Type : Any> Array<Type>.toGdxArray(ordered: Boolean = true, initialCapacity: Int = this.size):
    GdxArray<Type> {
  val array = GdxArray<Type>(ordered, initialCapacity, Type::class.java)
  array.addAll(this, 0, this.size)
  return array
}

/**
 * @param ordered if false, methods that remove elements may change the order of other elements in the array,
 *      which avoids a memory copy.
 * @param initialCapacity initial size of the backing array. Defaults to this array size.
 * @return values copied from this array stored in an optimized LibGDX int array.
 */
fun IntArray.toGdxArray(ordered: Boolean = true, initialCapacity: Int = this.size): GdxIntArray {
  val array = GdxIntArray(ordered, initialCapacity)
  array.addAll(this, 0, this.size)
  return array
}

/**
 * @param ordered if false, methods that remove elements may change the order of other elements in the array,
 *      which avoids a memory copy.
 * @param initialCapacity initial size of the backing array. Defaults to this array size.
 * @return values copied from this array stored in an optimized LibGDX float array.
 */
fun FloatArray.toGdxArray(ordered: Boolean = true, initialCapacity: Int = this.size): GdxFloatArray {
  val array = GdxFloatArray(ordered, initialCapacity)
  array.addAll(this, 0, this.size)
  return array
}

/**
 * @param ordered if false, methods that remove elements may change the order of other elements in the array,
 *      which avoids a memory copy.
 * @param initialCapacity initial size of the backing array. Defaults to this array size.
 * @return values copied from this array stored in an optimized LibGDX boolean array.
 */
fun BooleanArray.toGdxArray(ordered: Boolean = true, initialCapacity: Int = this.size): GdxBooleanArray {
  val array = GdxBooleanArray(ordered, initialCapacity)
  array.addAll(this, 0, this.size)
  return array
}
