package com.example.mezereon.Module;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Mezereon on 2017/8/23.
 */

@Module
public class AppModule {

    @Provides
    @Singleton
    Retrofit provideRetrofit() {
        Gson gson = new GsonBuilder()
                .create();
        return new Retrofit.Builder().client(new OkHttpClient())
                .baseUrl("http://mezereon.gotoip2.com/bustrackapi/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }
}
