@file:Suppress("NOTHING_TO_INLINE", "LoopToCallChain")

package ktx.collections

import com.badlogic.gdx.utils.*
import com.badlogic.gdx.utils.ObjectMap.Entry

/** Alias for [com.badlogic.gdx.utils.ObjectMap]. Added for consistency with other collections and factory methods. */
typealias GdxMap<Key, Value> = ObjectMap<Key, Value>

/**
 * Default LibGDX map size used by most constructors.
 */
const val defaultMapSize = 51

/**
 * @param initialCapacity initial capacity of the map. Will be resized if necessary.
 * @param loadFactor decides under what load the map is resized.
 * @return a new [ObjectMap].
 */
fun <Key, Value> gdxMapOf(initialCapacity: Int = defaultMapSize, loadFactor: Float = defaultLoadFactor): GdxMap<Key, Value> =
    GdxMap(initialCapacity, loadFactor)

/**
 * @param keysToValues will be added to the map.
 * @param initialCapacity initial capacity of the map. Will be resized if necessary.
 * @param loadFactor decides under what load the map is resized.
 * @return a new [ObjectMap].
 */
inline fun <Key, Value> gdxMapOf(vararg keysToValues: Pair<Key, Value>,
                                 initialCapacity: Int = defaultMapSize,
                                 loadFactor: Float = defaultLoadFactor): GdxMap<Key, Value> {
  val map = GdxMap<Key, Value>(initialCapacity, loadFactor)
  keysToValues.forEach { map[it.first] = it.second }
  return map
}

/**
 * A method wrapper over [ObjectMap.size] variable compatible with nullable types.
 * @return current amount of elements in the map.
 */
inline fun GdxMap<*, *>?.size(): Int = this?.size ?: 0

/**
 * @return true if the map is null or has no elements.
 */
inline fun GdxMap<*, *>?.isEmpty(): Boolean = this == null || this.size == 0

/**
 * @return true if the map is not null and contains at least one element.
 */
inline fun GdxMap<*, *>?.isNotEmpty(): Boolean = this != null && this.size > 0

/**
 * @param key a value might be assigned to this key and stored in the map.
 * @return true if a value is associated with passed key. False otherwise.
 */
operator fun <Key> GdxMap<Key, *>.contains(key: Key): Boolean = this.containsKey(key)

/**
 * @param key the passed value will be linked with this key.
 * @param value will be stored in the map, accessible by the passed key.
 * @return old value associated with the key or null if none.
 */
operator fun <Key, Value> GdxMap<Key, Value>.set(key: Key, value: Value): Value? = this.put(key, value)

/**
 * Allows to iterate over the map with Kotlin lambda syntax and direct access to [MutableIterator], which can remove
 * elements during iteration.
 * @param action will be invoked on each key and value pair. Passed iterator is ensured to be the same instance throughout
 *    the iteration. It can be used to remove elements.
 */
inline fun <Key, Value> GdxMap<Key, Value>.iterate(action: (Key, Value, MutableIterator<Entry<Key, Value>>) -> Unit) {
  val iterator = this.iterator()
  while (iterator.hasNext) {
    val next = iterator.next()
    action(next.key, next.value, iterator)
  }
}

/**
 * @return keys from this map stored in an [ObjectSet].
 */
fun <Key> GdxMap<Key, *>.toGdxSet(): ObjectSet<Key> = this.keys().toGdxSet()

/**
 * @param initialCapacity initial capacity of the map. Will be resized if necessary.
 * @param loadFactor decides how many elements the map might contain in relation to its total capacity before it is resized.
 * @param keyProvider will consume each value in this iterable. The results will be treated as map keys for the values.
 * @return values copied from this iterable stored in a LibGDX map, mapped to the keys returned by the provider.
 */
inline fun <Key, Value> Iterable<Value>.toGdxMap(initialCapacity: Int = defaultMapSize, loadFactor: Float = defaultLoadFactor,
                                                 keyProvider: (Value) -> Key): GdxMap<Key, Value> {
  val map = GdxMap<Key, Value>(initialCapacity, loadFactor)
  this.forEach { map[keyProvider(it)] = it }
  return map
}

/**
 * @param initialCapacity initial capacity of the map. Will be resized if necessary.
 * @param loadFactor decides how many elements the map might contain in relation to its total capacity before it is resized.
 * @param valueProvider will consume each value in this iterable. The results will be treated as map values.
 * @param keyProvider will consume each value in this iterable. The results will be treated as map keys for the values.
 * @return values converted from this iterable stored in a LibGDX map, mapped to the keys returned by the provider.
 */
inline fun <Type, Key, Value> Iterable<Type>.toGdxMap(initialCapacity: Int = defaultMapSize,
                                                      loadFactor: Float = defaultLoadFactor,
                                                      valueProvider: (Type) -> Value,
                                                      keyProvider: (Type) -> Key): GdxMap<Key, Value> {
  val map = GdxMap<Key, Value>(initialCapacity, loadFactor)
  this.forEach { map[keyProvider(it)] = valueProvider(it) }
  return map
}

/**
 * @param initialCapacity initial capacity of the map. Will be resized if necessary.
 * @param loadFactor decides how many elements the map might contain in relation to its total capacity before it is resized.
 * @param keyProvider will consume each value in this iterable. The results will be treated as map keys for the values.
 * @return values copied from this array stored in a LibGDX map, mapped to the keys returned by the provider.
 */
inline fun <Key, Value> Array<Value>.toGdxMap(initialCapacity: Int = defaultMapSize, loadFactor: Float = defaultLoadFactor,
                                              keyProvider: (Value) -> Key): GdxMap<Key, Value> {
  val map = GdxMap<Key, Value>(initialCapacity, loadFactor)
  this.forEach { map[keyProvider(it)] = it }
  return map
}

/**
 * @param initialCapacity initial capacity of the map. Will be resized if necessary.
 * @param loadFactor decides how many elements the map might contain in relation to its total capacity before it is resized.
 * @param valueProvider will consume each value in this iterable. The results will be treated as map values.
 * @param keyProvider will consume each value in this iterable. The results will be treated as map keys for the values.
 * @return values converted from this array stored in a LibGDX map, mapped to the keys returned by the provider.
 */
inline fun <Type, Key, Value> Array<Type>.toGdxMap(initialCapacity: Int = defaultMapSize,
                                                   loadFactor: Float = defaultLoadFactor,
                                                   valueProvider: (Type) -> Value,
                                                   keyProvider: (Type) -> Key): GdxMap<Key, Value> {
  val map = GdxMap<Key, Value>(initialCapacity, loadFactor)
  this.forEach { map[keyProvider(it)] = valueProvider(it) }
  return map
}

// Sadly, IdentityMap does NOT extend ObjectMap. It feels like it would require up to 2 overridden methods to set it up,
// but NO. The utilities that apply to ObjectMaps will not work on IdentityMaps, and since its a lot of extra work (and
// boilerplate) for such a pretty obscure class, only some basic functions are added - like factory methods and operators.
/**
 * @param initialCapacity initial capacity of the map. Will be resized if necessary.
 * @param loadFactor decides under what load the map is resized.
 * @return a new [IdentityMap], which compares keys by references.
 */
fun <Key, Value> gdxIdentityMapOf(initialCapacity: Int = defaultMapSize, loadFactor: Float = defaultLoadFactor):
    IdentityMap<Key, Value> = IdentityMap(initialCapacity, loadFactor)

/**
 * @param keysToValues will be added to the map.
 * @param initialCapacity initial capacity of the map. Will be resized if necessary.
 * @param loadFactor decides under what load the map is resized.
 * @return a new [IdentityMap], which compares keys by references.
 */
inline fun <Key, Value> gdxIdentityMapOf(vararg keysToValues: Pair<Key, Value>,
                                         initialCapacity: Int = defaultMapSize,
                                         loadFactor: Float = defaultLoadFactor): IdentityMap<Key, Value> {
  val map = IdentityMap<Key, Value>(initialCapacity, loadFactor)
  keysToValues.forEach { map[it.first] = it.second }
  return map
}

/**
 * @param key a value might be assigned to this key and stored in the map.
 * @return true if a value is associated with passed key. False otherwise.
 */
operator fun <Key> IdentityMap<Key, *>.contains(key: Key): Boolean = this.containsKey(key)

/**
 * @param key the passed value will be linked with this key.
 * @param value will be stored in the map, accessible by the passed key.
 * @return old value associated with the key or null if none.
 */
operator fun <Key, Value> IdentityMap<Key, Value>.set(key: Key, value: Value): Value? = this.put(key, value)

/**
 * Allows to iterate over the map with Kotlin lambda syntax and direct access to [MutableIterator], which can remove elements
 * during iteration.
 * @param action will be invoked on each key and value pair. Passed iterator is ensured to be the same instance throughout
 *    the iteration. It can be used to remove elements.
 */
inline fun <Key, Value> IdentityMap<Key, Value>.iterate(action: (Key, Value, MutableIterator<Entry<Key, Value>>) -> Unit) {
  val iterator = this.iterator()
  while (iterator.hasNext()) {
    val next = iterator.next()
    action(next.key, next.value, iterator)
  }
}

// Some basic support is also provided for optimized LibGDX maps with primitive keys. Again, no common superclass hurts.

/**
 * @param initialCapacity initial capacity of the map. Will be resized if necessary.
 * @param loadFactor decides under what load the map is resized.
 * @return a new [IntIntMap] with primitive int keys and values.
 */
fun gdxIntIntMap(initialCapacity: Int = defaultMapSize, loadFactor: Float = defaultLoadFactor): IntIntMap
    = IntIntMap(initialCapacity, loadFactor)

/**
 * @param key a value might be assigned to this key and stored in the map.
 * @return true if a value is associated with passed key. False otherwise.
 */
operator fun IntIntMap.contains(key: Int): Boolean = this.containsKey(key)

/**
 * @param key the passed value will be linked with this key.
 * @param value will be stored in the map, accessible by the passed key.
 */
operator fun IntIntMap.set(key: Int, value: Int) = this.put(key, value)

/**
 * @param key a value might be assigned to this key and stored in the map.
 * @return value associated with this key if present in this map, 0f otherwise. Use [IntIntMap.get] with second argument
 *    (default value) if you want predictable behavior in case of missing keys.
 * @see IntIntMap.get
 */
operator fun IntIntMap.get(key: Int): Int = this.get(key, 0)

/**
 * @param initialCapacity initial capacity of the map. Will be resized if necessary.
 * @param loadFactor decides under what load the map is resized.
 * @return a new [IntFloatMap] with primitive int keys and primitive float values.
 */
fun gdxIntFloatMap(initialCapacity: Int = defaultMapSize, loadFactor: Float = defaultLoadFactor): IntFloatMap
    = IntFloatMap(initialCapacity, loadFactor)

/**
 * @param key a value might be assigned to this key and stored in the map.
 * @return true if a value is associated with passed key. False otherwise.
 */
operator fun IntFloatMap.contains(key: Int): Boolean = this.containsKey(key)

/**
 * @param key the passed value will be linked with this key.
 * @param value will be stored in the map, accessible by the passed key.
 */
operator fun IntFloatMap.set(key: Int, value: Float) = this.put(key, value)

/**
 * @param key a value might be assigned to this key and stored in the map.
 * @return value associated with this key if present in this map, 0f otherwise. Use [IntFloatMap.get] with second argument
 *    (default value) if you want predictable behavior in case of missing keys.
 * @see IntFloatMap.get
 */
operator fun IntFloatMap.get(key: Int): Float = this.get(key, 0f)

/**
 * @param initialCapacity initial capacity of the map. Will be resized if necessary.
 * @param loadFactor decides under what load the map is resized.
 * @return a new [IntMap] with primitive int keys.
 */
fun <Value> gdxIntMap(initialCapacity: Int = defaultMapSize, loadFactor: Float = defaultLoadFactor): IntMap<Value>
    = IntMap(initialCapacity, loadFactor)

/**
 * @param key a value might be assigned to this key and stored in the map.
 * @return true if a value is associated with passed key. False otherwise.
 */
operator fun IntMap<*>.contains(key: Int): Boolean = this.containsKey(key)

/**
 * @param key the passed value will be linked with this key.
 * @param value will be stored in the map, accessible by the passed key.
 * @return old value associated with the key or null if none.
 */
operator fun <Value> IntMap<Value>.set(key: Int, value: Value): Value? = this.put(key, value)

/**
 * Allows to destruct [ObjectMap.Entry] into key and value components.
 * @return [ObjectMap.Entry.key]
 */
inline operator fun <Key, Value> Entry<Key, Value>.component1() = key!!

/**
 * Allows to destruct [ObjectMap.Entry] into key and value components. Nullable, since [ObjectMap] allows null values.
 * @return [ObjectMap.Entry.value]
 */
inline operator fun <Key, Value> Entry<Key, Value>.component2(): Value? = value

/**
 * Allows to destruct [IntMap.Entry] into key and value components.
 * @return [IntMap.Entry.key]
 */
inline operator fun <Value> IntMap.Entry<Value>.component1() = key

/**
 * Allows to destruct [IntMap.Entry] into key and value components. Nullable, since [IntMap] allows null values.
 * @return [IntMap.Entry.value]
 */
inline operator fun <Value> IntMap.Entry<Value>.component2(): Value? = value

/**
 * Allows to destruct [LongMap.Entry] into key and value components.
 * @return [LongMap.Entry.key]
 */
inline operator fun <Value> LongMap.Entry<Value>.component1() = key

/**
 * Allows to destruct [LongMap.Entry] into key and value components. Nullable, since [LongMap] allows null values.
 * @return [LongMap.Entry.value]
 */
inline operator fun <Value> LongMap.Entry<Value>.component2(): Value? = value

/**
 * Allows to destruct [IntIntMap.Entry] into key and value components.
 * @return [IntIntMap.Entry.key]
 */
inline operator fun IntIntMap.Entry.component1() = key

/**
 * Allows to destruct [IntIntMap.Entry] into key and value components.
 * @return [IntIntMap.Entry.value]
 */
inline operator fun IntIntMap.Entry.component2() = value

/**
 * Allows to destruct [IntFloatMap.Entry] into key and value components.
 * @return [IntFloatMap.Entry.key]
 */
inline operator fun IntFloatMap.Entry.component1() = key

/**
 * Allows to destruct [IntFloatMap.Entry] into key and value components.
 * @return [IntFloatMap.Entry.value]
 */
inline operator fun IntFloatMap.Entry.component2() = value

/**
 * Allows to destruct [ObjectIntMap.Entry] into key and value components.
 * @return [ObjectIntMap.Entry.key]
 */
inline operator fun <Value> ObjectIntMap.Entry<Value>.component1() = key!!

/**
 * Allows to destruct [ObjectIntMap.Entry] into key and value components.
 * @return [ObjectIntMap.Entry.value]
 */
inline operator fun <Value> ObjectIntMap.Entry<Value>.component2() = value

/**
 * Returns a [GdxMap] containing the results of applying the given [transform] function
 * to each entry in the original [GdxMap].
 */
inline fun <Key, Value, R> GdxMap<Key, Value>.map(transform: (Entry<Key, Value>) -> R): GdxMap<Key, R> {
  val destination = GdxMap<Key, R>(this.size)
  for (item in this) {
    destination[item.key] = transform(item)
  }
  return destination
}

/**
 * Returns a [GdxMap] containing only entries matching the given [predicate].
 */
inline fun <Key, Value> GdxMap<Key, Value>.filter(predicate: (Entry<Key, Value>) -> Boolean): GdxMap<Key, Value> {
  val destination = GdxMap<Key, Value>()
  for (item in this) {
    if (predicate(item)) {
      destination[item.key] = item.value
    }
  }
  return destination
}

/**
 * Returns a single [GdxArray] of all elements from all collections in the given [GdxMap].
 */
inline fun <Key, Type, Value : Iterable<Type>> GdxMap<Key, out Value>.flatten(): GdxArray<Type> {
  val destination = GdxArray<Type>()
  for (item in this) {
    destination.addAll(item.value)
  }
  return destination
}

/**
 * Returns a single [GdxArray] of all elements yielded from results of transform function being invoked
 * on each entry of original [GdxMap].
 */
inline fun <Key, Value, R> GdxMap<Key, Value>.flatMap(transform: (Entry<Key, Value>) -> Iterable<R>): GdxArray<R> {
  return this.map(transform).flatten()
}
