package com.skkk.ww.costomviewdemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
        rvHeader.setItemAnimator(new DefaultItemAnimator());
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

//                if (refreshLayout.isRefreshing()){
//                    refreshLayout.cancelRefresh();
//                }else {
//                    refreshLayout.startRefreshing();
//                }
                startActivity(new Intent(MainActivity.this,Main2Activity.class));
            }

            @Override
            public void onDragButtonClickListener(View view, int pos) {
                mDataList.remove(pos);
                adapter.notifyItemRemoved(pos);
                adapter.notifyItemRangeChanged(0,adapter.getItemCount());
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
            void onDragButtonClickListener(View view,int pos);
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
                holder.llShow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos=position;
                        onItemClickListener.onItemClickListener(v,pos);
                    }
                });
                holder.ivDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos=position;
                        onItemClickListener.onDragButtonClickListener(v,pos);
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
            private LinearLayout llShow;
            private LinearLayout llHide;
            private ImageView ivDelete;

            public MyViewHolder(View itemView) {
                super(itemView);
                tvItem= (TextView) itemView.findViewById(R.id.tv_item);
                llShow= (LinearLayout) itemView.findViewById(R.id.ll_show);
                llHide= (LinearLayout) itemView.findViewById(R.id.ll_hide);
                ivDelete= (ImageView) itemView.findViewById(R.id.iv_delete);
            }
        }
    }
}
