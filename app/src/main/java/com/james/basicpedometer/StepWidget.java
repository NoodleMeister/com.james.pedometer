package com.james.basicpedometer;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

public class StepWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(
            Context context,
            AppWidgetManager appWidgetManager,
            int[] appWidgetIds) {

        for (int appWidgetId : appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId);
        }
    }

    public static void updateWidget(
            Context context,
            AppWidgetManager appWidgetManager,
            int appWidgetId) {

        SharedPreferences prefs =
                context.getSharedPreferences("steps", Context.MODE_PRIVATE);

        int steps = prefs.getInt("count", 0);

        RemoteViews views =
                new RemoteViews(context.getPackageName(), R.layout.widget_steps);

        views.setTextViewText(R.id.widgetStepText, String.valueOf(steps));

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}

