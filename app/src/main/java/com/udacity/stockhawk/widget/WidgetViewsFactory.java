package com.udacity.stockhawk.widget;


import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import timber.log.Timber;

public class WidgetViewsFactory implements RemoteViewsService.RemoteViewsFactory {


    private static final int STOCK_LOADER = 0;
    private Context context = null;
    private int appWidgetId;
    private final DecimalFormat percentageFormat;
    private final DecimalFormat dollarFormat;
    private Cursor cursor;
    public static final String[] PROJECTION = new String[]{Contract.Quote.COLUMN_SYMBOL,
            Contract.Quote.COLUMN_HISTORY};


    public WidgetViewsFactory(Context ctxt, Intent intent) {
        this.context = ctxt;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        cursor = getCursor();
        Timber.e(String.valueOf(cursor.getCount()));

        percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
        percentageFormat.setMaximumFractionDigits(2);
        percentageFormat.setMinimumFractionDigits(2);
        percentageFormat.setPositivePrefix("+");

        dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);

    }

    private Cursor getCursor() {
        return context.getContentResolver().query(
                Contract.Quote.URI,
                Contract.Quote.QUOTE_COLUMNS.toArray(new String[]{}),
                null, null, Contract.Quote.COLUMN_SYMBOL
        );
    }

    @Override
    public void onCreate() {
        // no-op
    }

    @Override
    public void onDestroy() {
        // no-op
    }

    @Override
    public int getCount() {
        int count = 0;
        if (cursor != null) {
            count = cursor.getCount();
        }
        Timber.e(String.valueOf(count));
        return count;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        cursor.moveToPosition(position);
        RemoteViews row = new RemoteViews(context.getPackageName(),
                R.layout.row);

        row.setTextViewText(R.id.symbol, cursor.getString(Contract.Quote.POSITION_SYMBOL));
        row.setTextViewText(R.id.price, dollarFormat.format(cursor.getFloat(Contract.Quote.POSITION_PRICE)));

        float percentageChange = cursor.getFloat(Contract.Quote.POSITION_PERCENTAGE_CHANGE);


        row.setInt(
                R.id.change,
                "setBackgroundResource",
                percentageChange > 0 ? R.drawable.percent_change_pill_green : R.drawable.percent_change_pill_red
        );

        String percentage = percentageFormat.format(percentageChange / 100);
        row.setTextViewText(R.id.change, percentage);


        return (row);
    }

    @Override
    public RemoteViews getLoadingView() {
        return (null);
    }

    @Override
    public int getViewTypeCount() {
        return (1);
    }

    @Override
    public long getItemId(int position) {
        return (position);
    }

    @Override
    public boolean hasStableIds() {
        return (true);
    }

    @Override
    public void onDataSetChanged() {
        // no-op
    }

}