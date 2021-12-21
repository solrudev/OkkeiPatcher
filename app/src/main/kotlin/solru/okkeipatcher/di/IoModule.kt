package solru.okkeipatcher.di

import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import solru.okkeipatcher.io.services.impl.TLSSocketFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

private const val BASE_URL = "https://raw.githubusercontent.com/ForrrmerBlack/okkei-patcher/master/"

@InstallIn(SingletonComponent::class)
@Module(includes = [IoBindModule::class])
object IoModule {

	@Provides
	@Reusable
	fun provideIoDispatcher() = Dispatchers.IO

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

	@Provides
	@Singleton
	fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit = Retrofit.Builder()
		.client(okHttpClient)
		.addConverterFactory(MoshiConverterFactory.create(moshi))
		.baseUrl(BASE_URL)
		.build()
}