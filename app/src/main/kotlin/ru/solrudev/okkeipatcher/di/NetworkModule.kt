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

@file:Suppress("UNUSED")

package ru.solrudev.okkeipatcher.di

import android.content.Context
import android.os.Build
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.tls.HandshakeCertificates
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.data.network.ConnectivityInterceptor
import ru.solrudev.okkeipatcher.data.network.TLSSocketFactory
import ru.solrudev.okkeipatcher.data.network.api.patch.DefaultPatchApi
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

private const val BASE_URL = "https://okkeipatcher.solrudev.ru/api/v1/"

@InstallIn(SingletonComponent::class)
@Module(includes = [NetworkFlavoredModule::class])
object NetworkModule {

	@Provides
	@Singleton
	fun provideDefaultPatchApi(retrofit: Retrofit) = retrofit.create<DefaultPatchApi>()

	@Provides
	@Singleton
	fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
		return Retrofit.Builder()
			.client(okHttpClient)
			.addConverterFactory(MoshiConverterFactory.create(moshi))
			.baseUrl(BASE_URL)
			.build()
	}

	@Provides
	@Singleton
	fun provideOkHttpClient(
		@ApplicationContext context: Context,
		connectivityInterceptor: ConnectivityInterceptor
	): OkHttpClient {
		return OkHttpClient.Builder()
			.fixSsl(context)
			.followRedirects(true)
			.followSslRedirects(true)
			.readTimeout(0, TimeUnit.SECONDS)
			.writeTimeout(0, TimeUnit.SECONDS)
			.addInterceptor(connectivityInterceptor)
			.build()
	}

	private fun OkHttpClient.Builder.fixSsl(context: Context): OkHttpClient.Builder {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			return this
		}
		val rootCertificate = context.resources.openRawResource(R.raw.isrgrootx1).use { inputStream ->
			val certificateFactory = CertificateFactory.getInstance("X.509")
			val certificates = certificateFactory.generateCertificates(inputStream)
			certificates.single() as X509Certificate
		}
		val certificates = HandshakeCertificates.Builder()
			.addTrustedCertificate(rootCertificate)
			.addPlatformTrustedCertificates()
			.build()
		val socketFactory = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
			TLSSocketFactory(certificates.sslSocketFactory())
		} else {
			certificates.sslSocketFactory()
		}
		sslSocketFactory(socketFactory, certificates.trustManager())
		return this
	}

	@Provides
	@Singleton
	fun provideMoshi(): Moshi = Moshi.Builder().build()
}