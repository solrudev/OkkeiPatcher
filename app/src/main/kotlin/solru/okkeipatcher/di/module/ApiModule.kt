package solru.okkeipatcher.di.module

import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import solru.okkeipatcher.data.network.api.OkkeiPatcherApi
import solru.okkeipatcher.data.network.api.patch.DefaultPatchApi
import javax.inject.Singleton

private const val BASE_URL = "https://raw.githubusercontent.com/ForrrmerBlack/okkei-patcher/test/"

@InstallIn(SingletonComponent::class)
@Module
object ApiModule {

	@Provides
	@Singleton
	fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit = Retrofit.Builder()
		.client(okHttpClient)
		.addConverterFactory(MoshiConverterFactory.create(moshi))
		.baseUrl(BASE_URL)
		.build()

	@Provides
	@Singleton
	fun provideOkkeiPatcherService(retrofit: Retrofit): OkkeiPatcherApi =
		retrofit.create(OkkeiPatcherApi::class.java)

	@Provides
	@Singleton
	fun provideDefaultPatchDataService(retrofit: Retrofit): DefaultPatchApi =
		retrofit.create(DefaultPatchApi::class.java)
}