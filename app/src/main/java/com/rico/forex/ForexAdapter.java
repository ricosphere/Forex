package com.rico.forex;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class ForexAdapter extends RecyclerView.Adapter<ForexViewHolder> {

    private final JSONObject _rates;
    private final JSONArray _names;

    public ForexAdapter(JSONObject rates) {
        this._rates = rates;
        this._names = rates.names();
    }

    @NonNull
    @Override
    public ForexViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.layout_forex, parent, false);
        return new ForexViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ForexViewHolder holder, int position) {
        try {
            String kode = _names.getString(position);
            holder.kodeTextView.setText(kode);

            double kurs = _rates.getDouble(kode);
            double usd = _rates.getDouble("USD");
            double idr = _rates.getDouble("IDR");
            double baseIDR = usd / kurs * idr;

            DecimalFormat decimalFormat = new DecimalFormat("#,###.##");
            String kursFormatted = decimalFormat.format(baseIDR);

            holder.kursTextView.setText(kursFormatted);

        } catch (JSONException e) {
            Log.e("ForexAdapter", "JSON error: " + e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return _names != null ? _names.length() : 0;
    }
}
