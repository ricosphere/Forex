package com.rico.forex;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    private SwipeRefreshLayout _swipeRefreshLayout1;
    private RecyclerView _recyclerView1;
    private TextView _timestampTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _swipeRefreshLayout1 = findViewById(R.id.swipeRefreshLayout1);
        _recyclerView1 = findViewById(R.id.recyclerView1);
        _timestampTextView = findViewById(R.id.timestampTextView);

        initSwipeRefreshLayout();
        bindRecyclerView();
    }

    private void initSwipeRefreshLayout() {
        _swipeRefreshLayout1.setOnRefreshListener(this::bindRecyclerView);
    }

    private void bindRecyclerView() {
        String url = "https://openexchangerates.org/api/latest.json?app_id=4ca38744665a48368b3b9a72b8b7d507";
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

        asyncHttpClient.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonString = new String(responseBody);
                try {
                    JSONObject root = new JSONObject(jsonString);
                    JSONObject rates = root.getJSONObject("rates");
                    long timestamp = root.getLong("timestamp");

                    setTimestamp(timestamp);

                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
                    ForexAdapter adapter = new ForexAdapter(rates);

                    _recyclerView1.setLayoutManager(layoutManager);
                    _recyclerView1.setAdapter(adapter);
                    _swipeRefreshLayout1.setRefreshing(false);

                } catch (JSONException e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(MainActivity.this,
                        responseBody != null ? new String(responseBody) : error.getMessage(),
                        Toast.LENGTH_LONG).show();
                _swipeRefreshLayout1.setRefreshing(false);
            }
        });
    }

    private void setTimestamp(long timestamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        format.setTimeZone(TimeZone.getTimeZone("Asia/Jakarta"));
        String dateTime = format.format(new Date(timestamp * 1000));
        _timestampTextView.setText("Tanggal dan Waktu (UTC): " + dateTime);
    }
}
