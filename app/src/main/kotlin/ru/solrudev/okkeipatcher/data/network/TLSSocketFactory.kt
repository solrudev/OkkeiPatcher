/*
 * Okkei Patcher
 * Copyright (C) 2023-2024 Ilya Fomichev
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

package ru.solrudev.okkeipatcher.data.network

import java.io.IOException
import java.net.InetAddress
import java.net.Socket
import java.net.UnknownHostException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.net.ssl.*

class TLSSocketFactory : SSLSocketFactory() {

	val trustManager: X509TrustManager
		get() = trustManagers[0] as X509TrustManager

	private val delegate: SSLSocketFactory
	private lateinit var trustManagers: Array<TrustManager>

	init {
		generateTrustManagers()
		val context = SSLContext.getInstance("TLS")
		context.init(null, trustManagers, null)
		delegate = context.socketFactory
	}

	@Throws(KeyStoreException::class, NoSuchAlgorithmException::class)
	private fun generateTrustManagers() {
		val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
		trustManagerFactory.init(null as KeyStore?)
		val trustManagers = trustManagerFactory.trustManagers
		check(trustManagers.size == 1 && trustManagers[0] is X509TrustManager) {
			"Unexpected default trust managers: ${trustManagers.contentToString()}"
		}
		this.trustManagers = trustManagers
	}

	override fun getDefaultCipherSuites(): Array<String> = delegate.defaultCipherSuites
	override fun getSupportedCipherSuites(): Array<String> = delegate.supportedCipherSuites

	@Throws(IOException::class)
	override fun createSocket() = enableTLSOnSocket(delegate.createSocket())

	@Throws(IOException::class)
	override fun createSocket(s: Socket, host: String, port: Int, autoClose: Boolean) =
		enableTLSOnSocket(delegate.createSocket(s, host, port, autoClose))

	@Throws(IOException::class, UnknownHostException::class)
	override fun createSocket(host: String, port: Int) = enableTLSOnSocket(delegate.createSocket(host, port))

	@Throws(IOException::class, UnknownHostException::class)
	override fun createSocket(
		host: String,
		port: Int,
		localHost: InetAddress,
		localPort: Int
	) = enableTLSOnSocket(delegate.createSocket(host, port, localHost, localPort))

	@Throws(IOException::class)
	override fun createSocket(host: InetAddress, port: Int) = enableTLSOnSocket(delegate.createSocket(host, port))

	@Throws(IOException::class)
	override fun createSocket(
		address: InetAddress,
		port: Int,
		localAddress: InetAddress,
		localPort: Int
	) = enableTLSOnSocket(delegate.createSocket(address, port, localAddress, localPort))

	private fun enableTLSOnSocket(socket: Socket): Socket {
		if (socket is SSLSocket) {
			socket.enabledProtocols = arrayOf("TLSv1.1", "TLSv1.2")
		}
		return socket
	}
}