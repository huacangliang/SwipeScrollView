package com.lazymc.swipescrollview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    RecyclerView rv_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rv_content = (RecyclerView) findViewById(R.id.rv_content);
        rv_content.setLayoutManager(new LinearLayoutManager(this));
        rv_content.setAdapter(new MyAdapter());
    }

    static class MyAdapter extends RecyclerView.Adapter<VH> {
        int size = 10;

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_swipe, parent, false));
        }

        @Override
        public void onBindViewHolder(VH holder, int position) {
            holder.itemView.findViewById(R.id.tv_collect).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "收藏", Toast.LENGTH_LONG).show();
                }
            });
            holder.itemView.findViewById(R.id.tv_follower).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "关注", Toast.LENGTH_LONG).show();
                }
            });
            holder.itemView.findViewById(R.id.tv_del).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "删除", Toast.LENGTH_LONG).show();
                    size--;
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return size;
        }
    }

    static class VH extends RecyclerView.ViewHolder {

        public VH(View itemView) {
            super(itemView);
        }
    }
}
