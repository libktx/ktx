@file:Suppress("PackageDirectoryMismatch")

package com.badlogic.gdx.maps.tiled.renderers

/**
 * This file is used as a workaround for the tiledMapRenderer extensions. They need
 * to call [BatchTiledMapRenderer.beginRender] and [BatchTiledMapRenderer.endRender] methods
 * which are protected methods. Since this file matches the package of the [BatchTiledMapRenderer],
 * we can call protected methods of it and use our wrapper functions for public API extensions.
 */

@PublishedApi
internal fun BatchTiledMapRenderer.beginInternal() = this.beginRender()

@PublishedApi
internal fun BatchTiledMapRenderer.endInternal() = this.endRender()
