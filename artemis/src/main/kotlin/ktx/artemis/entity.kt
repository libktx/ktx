package ktx.artemis

import com.artemis.Archetype
import com.artemis.Component
import com.artemis.EntityEdit
import com.artemis.World
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Creates and adds an entity to the [World].
 *
 * @receiver the [World] for creating the entity.
 * @param entityEdit the inlined function with [EntityEdit].
 * @return the entity's ID as [Int].
 */
@OptIn(ExperimentalContracts::class)
inline fun World.entity(entityEdit: EntityEdit.() -> Unit = {}): Int {
  contract { callsInPlace(entityEdit, InvocationKind.EXACTLY_ONCE) }
  val entity = create()

  edit(entity).entityEdit()

  return entity
}

/**
 * Creates and adds an entity to the [World].
 *
 * @receiver the [World] for creating the entity.
 * @param archetype the [Archetype] to add to the entity.
 * @param entityEdit the inlined function with the [EntityEdit].
 * @return the entity's ID as [Int].
 */
@OptIn(ExperimentalContracts::class)
inline fun World.entity(
  archetype: Archetype,
  entityEdit: EntityEdit.() -> Unit = {},
): Int {
  contract { callsInPlace(entityEdit, InvocationKind.EXACTLY_ONCE) }
  val entity = this.create(archetype)
  edit(entity).entityEdit()
  return entity
}

/**
 * Edits an entity.
 *
 * @receiver the [World] for editing the entity.
 * @param entityId the ID of the entity to edit.
 * @param entityEdit the inlined function with the [EntityEdit].
 * @return the [EntityEdit].
 */
@OptIn(ExperimentalContracts::class)
inline fun World.edit(
  entityId: Int,
  entityEdit: EntityEdit.() -> Unit = {},
): EntityEdit {
  contract { callsInPlace(entityEdit, InvocationKind.EXACTLY_ONCE) }
  return edit(entityId).apply(entityEdit)
}

/**
 * Adds or replaces a [Component] of the [EntityEdit].
 *
 * @receiver the [EntityEdit] for creating a [Component].
 * @param T the [Component] to create.
 * @param componentEdit the inlined function with the created [Component].
 * @return this [EntityEdit].
 */
@OptIn(ExperimentalContracts::class)
inline fun <reified T : Component> EntityEdit.with(componentEdit: T.() -> Unit = {}): EntityEdit {
  contract { callsInPlace(componentEdit, InvocationKind.EXACTLY_ONCE) }
  val component = create(T::class.java)
  component.componentEdit()
  return this
}

/**
 * Adds a [Component] to the [EntityEdit].
 * The component gets replaced if it already exists.
 *
 * @receiver the [EntityEdit] for adding the [Component].
 * @param component the [Component] which will be added to the entity.
 */
operator fun EntityEdit.plusAssign(component: Component) {
  add(component)
}

/**
 * Removes a [Component] from the [EntityEdit].
 *
 * @receiver the [EntityEdit] for removing a [Component].
 * @param T the [Component] to remove from the entity.
 * @return this [EntityEdit].
 */
inline fun <reified T : Component> EntityEdit.remove(): EntityEdit = remove(T::class.java)
