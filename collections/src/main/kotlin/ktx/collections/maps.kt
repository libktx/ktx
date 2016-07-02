package ktx.collections

import com.badlogic.gdx.utils.ObjectMap

/**
 * Default LibGDX map size used by most constructors.
 */
val defaultMapSize = 51

/**
 * @param key the passed value will be linked with this key.
 * @param value will be stored in the map, accessible by the passed key.
 * @return old value associated with the key or null if none.
 */
operator fun <Key, Value> ObjectMap<Key, Value>.set(key: Key, value: Value): Value? = this.put(key, value)

/**
 * @param initialCapacity initial capacity of the map. Will be resized if necessary.
 * @param loadFactor decides how many elements the map might contain in relation to its total capacity before it is resized.
 * @param keyProvider will consume each value in this iterable. The results will be treated as map keys for the values.
 * @return values copied from this iterable stored in a LibGDX map, mapped to the keys returned by the provider.
 */
inline fun <Key, Value> Iterable<Value>.toGdxMap(initialCapacity: Int = defaultMapSize, loadFactor: Float = defaultLoadFactor,
                                                 keyProvider: (Value) -> Key): ObjectMap<Key, Value> {
  val map = ObjectMap<Key, Value>(initialCapacity, loadFactor)
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
                                                      keyProvider: (Type) -> Key): ObjectMap<Key, Value> {
  val map = ObjectMap<Key, Value>(initialCapacity, loadFactor)
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
                                              keyProvider: (Value) -> Key): ObjectMap<Key, Value> {
  val map = ObjectMap<Key, Value>(initialCapacity, loadFactor)
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
                                                   keyProvider: (Type) -> Key): ObjectMap<Key, Value> {
  val map = ObjectMap<Key, Value>(initialCapacity, loadFactor)
  this.forEach { map[keyProvider(it)] = valueProvider(it) }
  return map
}
