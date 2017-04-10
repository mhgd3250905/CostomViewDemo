package com.skkk.ww.costomviewdemo;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private String TAG=this.getClass().getSimpleName();
    private RecyclerView rvHeader;
    private List<String> mDataList;
    private HeaderAdapter adapter;
    private RefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_header_main);

        refreshLayout = (RefreshLayout) findViewById(R.id.hl);
        rvHeader= (RecyclerView) findViewById(R.id.rv_header);
        rvHeader.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        mDataList=new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            mDataList.add("here is item "+i);
        }
        adapter = new HeaderAdapter(MainActivity.this,mDataList);
        rvHeader.setAdapter(adapter);

        adapter.setOnItemClickListener(new HeaderAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(View view, int pos) {
                if (refreshLayout.isRefreshing()){
                    refreshLayout.cancelRefresh();
                }else {
                    refreshLayout.startRefreshing();
                }
            }
        });

        refreshLayout.setOnHeaderRefreshListener(new RefreshLayout.OnHeaderRefreshListener() {
            @Override
            public void onRefreshListener() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        refreshLayout.cancelRefresh();
                    }
                }).start();
            }
        });
    }


    /**
     * RecyclerView数据适配器
     */
    static class HeaderAdapter extends RecyclerView.Adapter<HeaderAdapter.MyViewHolder>{
        private Context context;
        private List<String> dataList;
        public OnItemClickListener onItemClickListener;
        public interface OnItemClickListener{
            void onItemClickListener(View view,int pos);
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }

        public HeaderAdapter(Context context, List<String> dataList) {
            this.context = context;
            this.dataList = dataList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder viewHolder=
                    new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_recycler,parent,false));
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            holder.tvItem.setText(dataList.get(position));
            if (onItemClickListener!=null){
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos=position;
                        onItemClickListener.onItemClickListener(v,pos);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder{
            private TextView tvItem;

            public MyViewHolder(View itemView) {
                super(itemView);
                tvItem= (TextView) itemView.findViewById(R.id.tv_item);
            }
        }
    }
}
