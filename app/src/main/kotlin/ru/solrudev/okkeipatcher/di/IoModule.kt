package ru.solrudev.okkeipatcher.di

import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import ru.solrudev.okkeipatcher.io.ConnectivityInterceptor
import ru.solrudev.okkeipatcher.io.TLSSocketFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module(includes = [IoBindModule::class, DispatchersModule::class])
object IoModule {

	@Provides
	@Singleton
	fun provideOkHttpClient(connectivityInterceptor: ConnectivityInterceptor): OkHttpClient =
		OkHttpClient.Builder().apply {
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