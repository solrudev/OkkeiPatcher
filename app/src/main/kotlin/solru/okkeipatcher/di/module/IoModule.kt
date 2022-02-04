package solru.okkeipatcher.di.module

import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import solru.okkeipatcher.io.TLSSocketFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module(includes = [IoBindModule::class, DispatchersModule::class])
object IoModule {

	@Provides
	@Singleton
	fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder().apply {
		val tlsSocketFactory = TLSSocketFactory()
		sslSocketFactory(tlsSocketFactory, tlsSocketFactory.trustManager)
		followRedirects(true)
		followSslRedirects(true)
		readTimeout(0, TimeUnit.SECONDS)
		writeTimeout(0, TimeUnit.SECONDS)
	}.build()

	@Provides
	@Singleton
	fun provideMoshi(): Moshi = Moshi.Builder().build()
}