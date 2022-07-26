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

private const val BASE_URL = "https://api.okkeipatcher.ml/api/"

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {

	@Provides
	@Singleton
	fun provideOkkeiPatcherApi(
		retrofit: Retrofit
	) = retrofit.create<OkkeiPatcherApi>()

	@Provides
	@Singleton
	fun provideDefaultPatchApi(
		retrofit: Retrofit
	) = retrofit.create<DefaultPatchApi>()

	@Provides
	@Singleton
	fun provideRetrofit(
		okHttpClient: OkHttpClient,
		moshi: Moshi
	): Retrofit = Retrofit.Builder()
		.client(okHttpClient)
		.addConverterFactory(MoshiConverterFactory.create(moshi))
		.baseUrl(BASE_URL)
		.build()

	@Provides
	@Singleton
	fun provideOkHttpClient(
		connectivityInterceptor: ConnectivityInterceptor
	): OkHttpClient = OkHttpClient.Builder().apply {
		val tlsSocketFactory = TLSSocketFactory()
		sslSocketFactory(tlsSocketFactory, tlsSocketFactory.trustManager)
		followRedirects(true)
		followSslRedirects(true)
		readTimeout(0, TimeUnit.SECONDS)
		writeTimeout(0, TimeUnit.SECONDS)
		addInterceptor(connectivityInterceptor)
	}.build()

	@Provides
	@Singleton
	fun provideMoshi(): Moshi = Moshi.Builder().build()
}