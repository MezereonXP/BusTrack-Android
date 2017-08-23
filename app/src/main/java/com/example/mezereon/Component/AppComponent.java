package com.example.mezereon.Component;

import com.example.mezereon.Home.HomeActivity;
import com.example.mezereon.Login.LoginActivity;
import com.example.mezereon.Module.AppModule;

import javax.inject.Singleton;

import dagger.Component;
import retrofit2.Retrofit;

/**
 * Created by Mezereon on 2017/8/23.
 */
@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
    void inject(HomeActivity homeActivity);
    void inject(LoginActivity loginActivity);
    Retrofit retrofit();
}
