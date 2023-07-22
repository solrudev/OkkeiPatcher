/*
 * Okkei Patcher
 * Copyright (C) 2023 Ilya Fomichev
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

import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import ru.solrudev.okkeipatcher.data.network.ConnectivityInterceptor
import ru.solrudev.okkeipatcher.data.network.TLSSocketFactory
import ru.solrudev.okkeipatcher.data.network.api.OkkeiPatcherApi
import ru.solrudev.okkeipatcher.data.network.api.patch.DefaultPatchApi
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

private const val BASE_URL = "https://okkeipatcher.solrudev.ru/api/"

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {

	@Provides
	@Singleton
	fun provideOkkeiPatcherApi(retrofit: Retrofit) = retrofit.create<OkkeiPatcherApi>()

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
	fun provideOkHttpClient(connectivityInterceptor: ConnectivityInterceptor): OkHttpClient {
		val tlsSocketFactory = TLSSocketFactory()
		return OkHttpClient.Builder()
			.sslSocketFactory(tlsSocketFactory, tlsSocketFactory.trustManager)
			.followRedirects(true)
			.followSslRedirects(true)
			.readTimeout(0, TimeUnit.SECONDS)
			.writeTimeout(0, TimeUnit.SECONDS)
			.addInterceptor(connectivityInterceptor)
			.build()
	}

	@Provides
	@Singleton
	fun provideMoshi(): Moshi = Moshi.Builder().build()
}