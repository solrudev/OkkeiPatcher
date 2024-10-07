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
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory

class TLSSocketFactory(private val sslSocketFactory: SSLSocketFactory) : SSLSocketFactory() {

	override fun getDefaultCipherSuites(): Array<String> = sslSocketFactory.defaultCipherSuites
	override fun getSupportedCipherSuites(): Array<String> = sslSocketFactory.supportedCipherSuites

	@Throws(IOException::class)
	override fun createSocket() = enableTLSOnSocket(sslSocketFactory.createSocket())

	@Throws(IOException::class)
	override fun createSocket(s: Socket, host: String, port: Int, autoClose: Boolean) =
		enableTLSOnSocket(sslSocketFactory.createSocket(s, host, port, autoClose))

	@Throws(IOException::class, UnknownHostException::class)
	override fun createSocket(host: String, port: Int) = enableTLSOnSocket(sslSocketFactory.createSocket(host, port))

	@Throws(IOException::class, UnknownHostException::class)
	override fun createSocket(
		host: String,
		port: Int,
		localHost: InetAddress,
		localPort: Int
	) = enableTLSOnSocket(sslSocketFactory.createSocket(host, port, localHost, localPort))

	@Throws(IOException::class)
	override fun createSocket(host: InetAddress, port: Int) =
		enableTLSOnSocket(sslSocketFactory.createSocket(host, port))

	@Throws(IOException::class)
	override fun createSocket(
		address: InetAddress,
		port: Int,
		localAddress: InetAddress,
		localPort: Int
	) = enableTLSOnSocket(sslSocketFactory.createSocket(address, port, localAddress, localPort))

	private fun enableTLSOnSocket(socket: Socket): Socket {
		if (socket is SSLSocket) {
			socket.enabledProtocols = arrayOf("TLSv1.1", "TLSv1.2")
		}
		return socket
	}
}