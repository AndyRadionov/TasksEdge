package io.github.andyradionov.tasksedge.di;

import javax.inject.Singleton;

import dagger.Component;
import io.github.andyradionov.tasksedge.notifications.NotificationIntentService;
import io.github.andyradionov.tasksedge.ui.common.BaseActivity;
import io.github.andyradionov.tasksedge.widget.WidgetUpdateService;

/**
 * @author Andrey Radionov
 */

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {

    void inject(BaseActivity baseActivity);
    void inject(NotificationIntentService notificationIntentService);
    void inject(WidgetUpdateService widgetUpdateService);
}
