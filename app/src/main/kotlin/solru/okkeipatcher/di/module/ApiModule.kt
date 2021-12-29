package solru.okkeipatcher.di.module

import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import solru.okkeipatcher.api.OkkeiPatcherService
import solru.okkeipatcher.api.patchdata.DefaultPatchDataService
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
	fun provideOkkeiPatcherService(retrofit: Retrofit): OkkeiPatcherService =
		retrofit.create(OkkeiPatcherService::class.java)

	@Provides
	@Singleton
	fun provideDefaultPatchDataService(retrofit: Retrofit): DefaultPatchDataService =
		retrofit.create(DefaultPatchDataService::class.java)
}