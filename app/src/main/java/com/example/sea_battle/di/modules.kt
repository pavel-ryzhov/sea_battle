package com.example.sea_battle.di

import android.content.Context
import com.example.sea_battle.navigation.Navigator
import com.example.sea_battle.navigation.NavigatorImpl
import com.example.sea_battle.data.preferences.AppPreferences
import com.example.sea_battle.data.preferences.AppPreferencesImpl
import com.example.sea_battle.data.services.client.ClientService
import com.example.sea_battle.data.services.client.ClientServiceImpl
import com.example.sea_battle.data.services.game.GameService
import com.example.sea_battle.data.services.game.GameServiceImpl
import com.example.sea_battle.data.services.server.ServerService
import com.example.sea_battle.data.services.server.ServerServiceImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class ProvidesModuleSingletonComponent {

    @Provides
    fun providesAppPreferences(@ApplicationContext context: Context): AppPreferences {
        return AppPreferencesImpl(context)
    }
}
@Module
@InstallIn(ActivityComponent::class)
class ProvidesModuleActivityComponent {
    @Provides
    fun providesNavigator(@ActivityContext context: Context): Navigator {
        return NavigatorImpl(context)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class BindsModule {
    @Binds
    abstract fun bindsServerService(
        serverServiceImpl: ServerServiceImpl
    ): ServerService
    @Binds
    abstract fun bindsClientService(
        clientServiceImpl: ClientServiceImpl
    ): ClientService
    @Binds
    abstract fun bindsGameService(
        gameServiceImpl: GameServiceImpl
    ): GameService
}