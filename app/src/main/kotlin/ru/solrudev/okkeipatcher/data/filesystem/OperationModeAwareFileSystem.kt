/*
 * Okkei Patcher
 * Copyright (C) 2026 Ilya Fomichev
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ru.solrudev.okkeipatcher.data.filesystem

import com.topjohnwu.superuser.nio.FileSystemManager.MODE_READ_ONLY
import com.topjohnwu.superuser.nio.FileSystemManager.MODE_READ_WRITE
import okio.FileHandle
import okio.FileMetadata
import okio.FileSystem
import okio.IOException
import okio.Path
import okio.Path.Companion.toOkioPath
import okio.Sink
import okio.Source
import okio.sink
import okio.source
import ru.solrudev.okkeipatcher.di.LocalFileSystem
import java.io.File
import java.io.FileNotFoundException
import java.io.InterruptedIOException
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import javax.inject.Inject

class OperationModeAwareFileSystem @Inject constructor(
	@LocalFileSystem private val localFileSystem: FileSystem,
	private val fileSystemManagerProvider: FileSystemManagerProvider
) : FileSystem() {

	override fun canonicalize(path: Path): Path {
		val fs = fileSystemManagerProvider.get() ?: return localFileSystem.canonicalize(path)
		val canonicalFile = fs.getFile(path.toString()).canonicalFile
		if (!canonicalFile.exists()) throw FileNotFoundException("no such file")
		return canonicalFile.toOkioPath()
	}

	override fun metadataOrNull(path: Path): FileMetadata? {
		val fs = fileSystemManagerProvider.get() ?: return localFileSystem.metadataOrNull(path)
		val file = fs.getFile(path.toString())
		val isRegularFile = file.isFile
		val isDirectory = file.isDirectory
		val lastModifiedAtMillis = file.lastModified()
		val size = file.length()

		if (!isRegularFile &&
			!isDirectory &&
			lastModifiedAtMillis == 0L &&
			size == 0L &&
			!file.exists()
		) {
			return null
		}

		return FileMetadata(
			isRegularFile = isRegularFile,
			isDirectory = isDirectory,
			symlinkTarget = null,
			size = size,
			createdAtMillis = null,
			lastModifiedAtMillis = lastModifiedAtMillis,
			lastAccessedAtMillis = null,
		)
	}

	override fun list(dir: Path): List<Path> {
		val fs = fileSystemManagerProvider.get() ?: return localFileSystem.list(dir)
		val file = fs.getFile(dir.toString())
		return list(file, dir, throwOnFailure = true)!!
	}

	override fun listOrNull(dir: Path): List<Path>? {
		val fs = fileSystemManagerProvider.get() ?: return localFileSystem.listOrNull(dir)
		val file = fs.getFile(dir.toString())
		return list(file, dir, throwOnFailure = false)
	}

	@Suppress("FoldInitializerAndIfToElvis")
	private fun list(file: File, dir: Path, throwOnFailure: Boolean): List<Path>? {
		val entries = file.list()
		if (entries == null) {
			if (throwOnFailure) {
				if (!file.exists()) throw FileNotFoundException("no such file: $dir")
				throw IOException("failed to list $dir")
			} else {
				return null
			}
		}
		val result = entries.mapTo(mutableListOf()) { dir / it }
		result.sort()
		return result
	}

	override fun openReadOnly(file: Path): FileHandle {
		val fs = fileSystemManagerProvider.get() ?: return localFileSystem.openReadOnly(file)
		return RemoteFileSystemHandle(readWrite = false, channel = fs.openChannel(file.toString(), MODE_READ_ONLY))
	}

	override fun openReadWrite(file: Path, mustCreate: Boolean, mustExist: Boolean): FileHandle {
		val fs = fileSystemManagerProvider.get() ?: return localFileSystem.openReadWrite(file)
		require(!mustCreate || !mustExist) {
			"Cannot require mustCreate and mustExist at the same time."
		}
		if (mustCreate) file.requireCreate()
		if (mustExist) file.requireExist()
		return RemoteFileSystemHandle(readWrite = true, channel = fs.openChannel(file.toString(), MODE_READ_WRITE))
	}

	override fun source(file: Path): Source {
		val fs = fileSystemManagerProvider.get() ?: return localFileSystem.source(file)
		val remoteFile = fs.getFile(file.toString())
		return remoteFile.newInputStream().source()
	}

	override fun sink(file: Path, mustCreate: Boolean): Sink {
		val fs = fileSystemManagerProvider.get() ?: return localFileSystem.sink(file)
		if (mustCreate) file.requireCreate()
		val remoteFile = fs.getFile(file.toString())
		return remoteFile.newOutputStream().sink()
	}

	override fun appendingSink(file: Path, mustExist: Boolean): Sink {
		val fs = fileSystemManagerProvider.get() ?: return localFileSystem.appendingSink(file, mustExist)
		if (mustExist) file.requireExist()
		val remoteFile = fs.getFile(file.toString())
		return remoteFile.newOutputStream(true).sink()
	}

	override fun createDirectory(dir: Path, mustCreate: Boolean) {
		val fs = fileSystemManagerProvider.get() ?: return localFileSystem.createDirectory(dir, mustCreate)
		if (!fs.getFile(dir.toString()).mkdir()) {
			val alreadyExist = metadataOrNull(dir)?.isDirectory == true
			if (alreadyExist) {
				if (mustCreate) {
					throw IOException("$dir already exists.")
				} else {
					return
				}
			}
			throw IOException("failed to create directory: $dir")
		}
	}

	override fun atomicMove(source: Path, target: Path) {
		val fs = fileSystemManagerProvider.get() ?: return localFileSystem.atomicMove(source, target)
		// Note that on Windows, this will fail if [target] already exists.
		val renamed = fs.getFile(source.toString()).renameTo(fs.getFile(target.toString()))
		if (!renamed) throw IOException("failed to move $source to $target")
	}

	override fun delete(path: Path, mustExist: Boolean) {
		val fs = fileSystemManagerProvider.get() ?: return localFileSystem.delete(path, mustExist)
		if (Thread.interrupted()) {
			// If the current thread has been interrupted.
			throw InterruptedIOException("interrupted")
		}
		val file = fs.getFile(path.toString())
		val deleted = file.delete()
		if (!deleted) {
			if (file.exists()) throw IOException("failed to delete $path")
			if (mustExist) throw FileNotFoundException("no such file: $path")
		}
	}

	override fun createSymlink(source: Path, target: Path) {
		val fs = fileSystemManagerProvider.get() ?: return localFileSystem.createSymlink(source, target)
		val created = fs.getFile(source.toString()).createNewSymlink(target.toString())
		if (!created) throw IOException("$target already exists.")
	}

	override fun toString() = "FileSystemWrapper"

	// We have to implement existence verification non-atomically on the JVM because there's no API
	// to do so.
	private fun Path.requireExist() {
		if (!exists(this)) throw IOException("$this doesn't exist.")
	}

	private fun Path.requireCreate() {
		if (exists(this)) throw IOException("$this already exists.")
	}
}

private class RemoteFileSystemHandle(
	readWrite: Boolean,
	private val channel: FileChannel
) : FileHandle(readWrite) {

	@Synchronized
	override fun protectedRead(
		fileOffset: Long,
		array: ByteArray,
		arrayOffset: Int,
		byteCount: Int
	): Int {
		channel.position(fileOffset)
		var bytesRead = 0
		while (bytesRead < byteCount) {
			val buffers = arrayOf(ByteBuffer.wrap(array))
			val readResult = channel.read(buffers, arrayOffset, byteCount - bytesRead).toInt()
			if (readResult == -1) {
				if (bytesRead == 0) return -1
				break
			}
			bytesRead += readResult
		}
		return bytesRead
	}

	@Synchronized
	override fun protectedWrite(
		fileOffset: Long,
		array: ByteArray,
		arrayOffset: Int,
		byteCount: Int
	) {
		channel.position(fileOffset)
		val buffers = arrayOf(ByteBuffer.wrap(array))
		channel.write(buffers, arrayOffset, byteCount)
	}

	@Synchronized
	override fun protectedFlush() {
		channel.force(false)
	}

	@Synchronized
	override fun protectedResize(size: Long) {
		val currentSize = size()
		val delta = size - currentSize
		if (delta > 0) {
			protectedWrite(currentSize, ByteArray(delta.toInt()), 0, delta.toInt())
		} else {
			channel.truncate(size)
		}
	}

	@Synchronized
	override fun protectedSize(): Long {
		return channel.size()
	}

	@Synchronized
	override fun protectedClose() {
		channel.close()
	}
}