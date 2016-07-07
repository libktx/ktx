package ktx.collections

import com.badlogic.gdx.utils.Pool
import ktx.collections.NodePool.clear
import ktx.collections.PooledList.Node
import java.util.NoSuchElementException

/**
 * LinkedList equivalent for LibGDX applications that caches its nodes and iterator to generate as little garbage as
 * possible. As opposed to LibGDX PooledLinkedList, this list implements [Iterable] and has more Kotlin-friendly syntax
 * for extra utility. This class does not offer operations that usually should not be performed on linked lists - like
 * removal by element - but it contains some extra methods that can be used during iteration like [insertAfter], [remove]
 * or [insertBefore].
 *
 * Unfortunately, since there is no common interface for LibGDX collections (and java.util interfaces are avoided for
 * a good reason), this list shares no interface with the other collections. However, this class comes with its own
 * utility methods.
 *
 * This collection is excellent for queues or storage of objects that often have to be removed during iteration for
 * little cost. By design, it provides no random access to elements - it should be used when iteration is very often.
 *
 * @author MJ
 * @param nodePool provides and manages [Node] instances.
 */
class PooledList<T>(val nodePool: Pool<Node<T>>) : Iterable<T> {
  /**
   * Current amount of elements in this list.
   */
  var size = 0
    private set
  private val main = Node<T>()
  private val iterator = PooledListIterator(this)

  /**
   * Returns true if the list has no elements.
   */
  val isEmpty: Boolean
    get() = size == 0

  /**
   * Returns true if the list has at least one element.
   */
  val isNotEmpty: Boolean
    get() = size != 0

  /**
   * Returns the first element in the list, provided it is not null and the list is not empty. If used as a setter,
   * adds the value as the first element of the list.
   */
  var first: T
    get() = if (isEmpty) throw NoSuchElementException() else main.next.element!!
    set(value) {
      addBefore(value, main.next)
    }

  /**
   * Returns the last element in the list, provided it is not null and the list is not empty. If used as a setter,
   * adds the value as the last element of the list.
   */
  var last: T
    get() = if (isEmpty) throw NoSuchElementException() else main.previous.element!!
    set(value) {
      addBefore(value, main)
    }

  /**
   * @param element will be added to the list as the last element. Note that you can explicitly add the element as last
   *    or first in the list using [first] or [last] properties setters.
   * @see first
   * @see last
   */
  fun add(element: T) {
    addBefore(element, main)
  }

  /**
   * @param elements will be iterated over and added to the list one by one.
   */
  fun addAll(elements: Array<out T>) = elements.forEach { add(it) }

  /**
   * @param elements will be iterated over and added to the list one by one.
   */
  fun addAll(elements: Iterable<T>) = elements.forEach { add(it) }

  /**
   * @param element will be added to the list as the last element.
   * @return this list for chaining.
   */
  operator fun plus(element: T): PooledList<T> {
    add(element)
    return this
  }

  /**
   * @param elements will be iterated over and added to the list one by one.
   * @return this list for chaining.
   */
  operator fun plus(elements: Array<out T>): PooledList<T> {
    addAll(elements)
    return this
  }

  /**
   * @param elements will be iterated over and added to the list one by one.
   * @return this list for chaining.
   */
  operator fun plus(elements: Iterable<T>): PooledList<T> {
    addAll(elements)
    return this
  }

  /**
   * @return element that was previously first. Can be null.
   * @throws IllegalStateException if list is empty.
   */
  fun removeFirst(): T = remove(main.next)

  /**
   * @return element that was previously last. Can be null.
   * @throws IllegalStateException if list is empty.
   */
  fun removeLast(): T = remove(main.previous)

  /**
   * @param element might be stored in the list.
   * @return true if the passed element equals any stored value. Note that this method requires iteration over the whole
   *    list and is not considered cheap.
   */
  operator fun contains(element: T): Boolean = any { it == element }

  /**
   * @return internally cached and reused [PooledListIterator] instance. Note that this list cannot be iterated over
   *    in nested loops using this method.
   */
  override fun iterator(): MutableIterator<T> = iterator.reset()

  /**
   * @return inverted internally cached and reused [PooledListIterator] instance, iterating from the last element to the
   *    first. Note that this list cannot be iterated over in nested loops using this method.
   */
  fun reversedIterator(): MutableIterator<T> = iterator.reset(reversed = true)

  /**
   * @return a new instance of [PooledListIterator], allowing to iterate over list elements. Use only when you need to
   *    iterate over the list in a nested loop; prefer [iterator] and [reversedIterator] otherwise (both cache its
   *    iterator instance).
   */
  fun newIterator(reversed: Boolean = false): MutableIterator<T> = PooledListIterator(this).reset(reversed)

  /**
   * @param apply will be applied to each element in the list. Iteration will be in the reversed order - from the last
   *    element to the first. Note that this method reuses list's main iterator instance.
   */
  inline fun forEachReversed(apply: (T) -> Unit) {
    for (element in reversedIterator()) apply(element)
  }

  /**
   * @param reversed if true, the iteration will begin from the last element and end with the first.
   * @param apply will be applied to each element in the list. The action gets a direct reference to the list's
   *    [PooledListIterator] instance, which can be used to modify the list.
   */
  inline fun iterate(reversed: Boolean = false, apply: (T, PooledListIterator<T>) -> Unit) {
    val iterator = (if (reversed) reversedIterator() else iterator()) as PooledListIterator
    while (iterator.hasNext()) apply(iterator.next(), iterator)
  }

  /**
   * Removes element currently selected by the iterator. This utility method can be used ONLY during iteration with one
   * of the iterator-caching methods: [iterator] or [reversedIterator]. Note that [iterator] is internally used by
   * for-each loops and utilities like [forEach] method.
   */
  fun remove() = iterator.remove()

  /**
   * Adds element before the value currently selected by the iterator. This utility method can be used ONLY during
   * iteration with one of the iterator-caching methods: [iterator] or [reversedIterator]. Note that [iterator] is
   * internally used by for-each loops and utilities like [forEach] method.
   * @param element will be added to the list before the current iteration element. If the iterator is reversed, this
   *    will be the next element in the iteration.
   */
  fun insertBefore(element: T) {
    if (iterator.current === main) throw IllegalStateException()
    addBefore(element, iterator.current)
  }

  /**
   * Adds element after the value currently selected by the iterator. This utility method can be used ONLY during
   * iteration with one of the iterator-caching methods: [iterator] or [reversedIterator]. Note that [iterator] is
   * internally used by for-each loops and utilities like [forEach] method.
   * @param element will be added to the list after the current iteration element. If the iterator is not reversed, this
   *    will be the next element in the iteration.
   */
  fun insertAfter(element: T) {
    if (iterator.current === main) throw IllegalStateException()
    addBefore(element, iterator.current.next)
  }

  /**
   * Iterates over the list to remove all of its elements and free its nodes to the pool. If you do not care about the
   * garbage generated by this list, call [purge] to immediately clear the list.
   * @see purge
   */
  fun clear() {
    while (isNotEmpty) removeLast()
  }

  /**
   * Immediately clears the list, without returning the nodes to the node pool. If you do not want the list to generate
   * garbage at the cost of potentially longer list cleaning time, use [clear] method.
   * @see clear
   */
  fun purge() {
    main.next = main
    main.previous = main
    size = 0
  }

  /**
   * Storage class for list elements.
   * @author MJ
   */
  class Node<T> {
    var element: T? = null
    var previous = this
    var next = this

    fun reset() {
      element = null
      previous = this
      next = this
    }
  }

  /**
   * Allows to iterate over [PooledList] elements in both regular and reversed order.
   * @author MJ
   * @see remove
   * @see insertBefore
   * @see insertAfter
   */
  class PooledListIterator<T>(var list: PooledList<T>) : MutableIterator<T>, Iterable<T> {
    internal var current = list.main

    var reversed = false

    override fun hasNext(): Boolean = list.main !== if (reversed) current.previous else current.next
    override fun next(): T {
      current = if (reversed) {
        current.previous
      } else {
        current.next
      }
      return current.element!!
    }

    override fun remove() {
      val node = current
      current = if (reversed) {
        current.next
      } else {
        current.previous
      }
      list.remove(node)
    }

    /**
     * @param reversed if true, will iterate from the last element to the first.
     * @return this iterator for chaning.
     */
    fun reset(reversed: Boolean = false): PooledListIterator<T> {
      current = list.main
      this.reversed = reversed
      return this
    }

    /**
     * @return this instance of [PooledList] iterator.
     */
    override fun iterator(): Iterator<T> = this
  }

  // Internal API.

  private fun addBefore(element: T, node: Node<T>): Node<T> {
    val newNode = nodePool.obtain()
    newNode.element = element
    newNode.next = node
    newNode.previous = node.previous
    newNode.previous.next = newNode
    newNode.next.previous = newNode
    size++
    return newNode
  }

  private fun remove(node: Node<T>): T {
    if (node == main) throw IllegalStateException()
    val element = node.element
    node.previous.next = node.next
    node.next.previous = node.previous
    node.reset()
    nodePool.free(node)
    size--
    return element!!
  }
}

/**
 * Default and main [PooledList] [Node] pool. Provides and manages node instances. Has no max value - will store nearly
 * unlimited freed [Node] instances and should be cleared manually if necessary.
 * @author MJ
 * @see clear
 */
object NodePool : Pool<Node<Any>>() {
  override fun newObject(): Node<Any> = Node()

  /**
   * Utility method that allows to use this pool for any kind of objects.
   */
  @Suppress("CAST_NEVER_SUCCEEDS")
  fun <T> pool(): Pool<Node<T>> = this as Pool<Node<T>>
}

/**
 * @param pool provides and manages [Node] instances.
 * @return a new [PooledList] instance, which caches its nodes and iterator, and is an equivalent to java.util.LinkedList.
 * @see NodePool
 */
fun <Type> gdxListOf(pool: Pool<Node<Type>> = NodePool.pool<Type>()): PooledList<Type> = PooledList(pool)

/**
 * @param elements will be added to the list.
 * @param pool provides and manages [Node] instances.
 * @return a new [PooledList] instance, which caches its nodes and iterator, and is an equivalent to java.util.LinkedList.
 * @see NodePool
 */
fun <Type> gdxListOf(vararg elements: Type, pool: Pool<Node<Type>> = NodePool.pool<Type>()): PooledList<Type> {
  val list = PooledList(pool)
  list.addAll(elements)
  return list
}

/**
 * Allows to convert an [Iterable] into a [PooledList] - KTX LinkedList equivalent that caches its nodes and iterator.
 * @param pool provides and manages [Node] instances.
 * @return a new instance of [PooledList] storing the elements from the iterable.
 */
fun <Type> Iterable<Type>.toGdxList(pool: Pool<Node<Type>> = NodePool.pool<Type>()): PooledList<Type> {
  val list = PooledList(pool)
  list.addAll(this)
  return list
}

/**
 * Allows to convert an [Array] into a [PooledList] - KTX LinkedList equivalent that caches its nodes and iterator.
 * @param pool provides and manages [Node] instances.
 * @return a new instance of [PooledList] storing the elements from the array.
 */
fun <Type> Array<out Type>.toGdxList(pool: Pool<Node<Type>> = NodePool.pool<Type>()): PooledList<Type> {
  val list = PooledList(pool)
  list.addAll(this)
  return list
}
