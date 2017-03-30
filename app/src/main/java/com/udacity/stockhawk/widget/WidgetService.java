package com.udacity.stockhawk.widget;

import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.ui.StockDetailsActivity;

public class WidgetService extends RemoteViewsService {

    private static final String[] DATA_COLUMNS = {
            Contract.Quote._ID + "." + Contract.Quote._ID,
            Contract.Quote.COLUMN_PRICE,
            Contract.Quote.COLUMN_SYMBOL,
            Contract.Quote.COLUMN_PERCENTAGE_CHANGE
    };



    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;
            @Override
            public void onCreate() {

            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }

                final long identityToken = Binder.clearCallingIdentity();
                data = getApplicationContext().getContentResolver().query(Contract.Quote.URI,
                        new String[]{ Contract.Quote._ID, Contract.Quote.COLUMN_SYMBOL, Contract.Quote.COLUMN_PRICE,
                                Contract.Quote.COLUMN_PERCENTAGE_CHANGE, Contract.Quote.COLUMN_ABSOLUTE_CHANGE},
                        null,
                        null,
                        null);

                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {

            }

            @Override
            public int getCount() {
                if (data == null) {
                    return 0;
                } else {
                    return data.getCount();
                }
            }

            @Override
            public RemoteViews getViewAt(int i) {
                if (!data.moveToPosition(i)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getApplicationContext().getPackageName(), R.layout.quote_list_item);
                String symbol = data.getString(data.getColumnIndex(Contract.Quote.COLUMN_SYMBOL));
                int percentageChange = data.getColumnIndex(Contract.Quote.COLUMN_PERCENTAGE_CHANGE);

                views.setTextViewText(R.id.stock_symbol, symbol);
                views.setTextViewText(R.id.bid_price, "$" + data.getString(data.getColumnIndex(Contract.Quote.COLUMN_PRICE)));
                views.setTextViewText(R.id.change, data.getString(percentageChange) + "%");

                views.setInt(
                        R.id.change,
                        "setBackgroundResource",
                        percentageChange > 0 ? R.drawable.percent_change_pill_green : R.drawable.percent_change_pill_red
                );

                final Intent fillInIntent = new Intent();
                fillInIntent.putExtra(StockDetailsActivity.SYMBOL_EXTRA_KEY, symbol);
                views.setOnClickFillInIntent(R.id.list_item, fillInIntent);
                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return null;
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int i) {
                if (data != null && data.moveToPosition(i)) {
                    final int QUOTES_ID_COL = 0;
                    return data.getLong(QUOTES_ID_COL);
                }
                return i;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
