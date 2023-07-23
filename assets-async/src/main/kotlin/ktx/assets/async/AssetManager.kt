@file:Suppress("PackageDirectoryMismatch")

// This extension methods have to appear in the libGDX package in order to access package-private fields.

package com.badlogic.gdx.assets

/** Attempts to cancel loading of asset identified by [fileName]. For internal use. */
fun AssetManager.cancelLoading(fileName: String) {
  loadQueue.removeAll { it.fileName == fileName }
  tasks.forEach { if (it.assetDesc.fileName == fileName) it.cancel = true }
  assetDependencies.remove(fileName)
}

/** Attempts to cancel loading of assets identified by [fileNames]. For internal use. */
fun AssetManager.cancelLoading(fileNames: Set<String>) {
  loadQueue.removeAll { it.fileName in fileNames }
  tasks.forEach { if (it.assetDesc.fileName in fileNames) it.cancel = true }
  fileNames.forEach(assetDependencies::remove)
}
