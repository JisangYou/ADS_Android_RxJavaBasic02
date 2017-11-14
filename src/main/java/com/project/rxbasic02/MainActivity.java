package com.project.rxbasic02;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {

    RecyclerView recyler;
    CustomAdapter adapter;
    List<String> months = new ArrayList<>();
    String monthString[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyler = findViewById(R.id.recycler);
        adapter = new CustomAdapter();
        recyler.setAdapter(adapter);
        recyler.setLayoutManager(new LinearLayoutManager(this));

        DateFormatSymbols dfs = new DateFormatSymbols();
        monthString = dfs.getMonths();

        // 1. 발행자
        Observable<String> observable = Observable.create(
                e -> {
                    try {
                        for (String month : monthString) {
                            e.onNext(month);
                            Thread.sleep(1000);
                        }
                        e.onComplete();
                    } catch (Exception ex) {
                        throw ex;
                    }
                }
        );

        //2. 구독자
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                str -> {
                    months.add(str);
                    adapter.setDataAndRefresh(months);
                }
        );
    }
}

class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.Holder> {
    List<String> data = new ArrayList<>();

    public void setDataAndRefresh(List<String> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        holder.text1.setText(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView text1;

        public Holder(View itemView) {
            super(itemView);
            text1 = itemView.findViewById(android.R.id.text1);
        }
    }
}