package io.github.andyradionov.tasksedge.di;

import android.arch.lifecycle.ViewModelProvider;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.github.andyradionov.tasksedge.data.database.FirebaseRepository;
import io.github.andyradionov.tasksedge.ui.common.ViewModelFactory;

/**
 * @author Andrey Radionov
 */
@Module
public class AppModule {

    @Singleton
    @Provides
    FirebaseRepository provideFirebaseRepository() {
        return new FirebaseRepository();
    }

    @Singleton
    @Provides
    ViewModelProvider.Factory provideViewModelFactory(FirebaseRepository repository) {
        return new ViewModelFactory(repository);
    }
}
