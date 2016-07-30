/*　             ∧∧ ∩
        　　　 (`•ω•)/
        　　　⊂　　ノ
        　　　　(つノ
        　　　　 (ノ
        　＿＿_／(＿＿_
        ／　　(＿＿＿／
        ￣￣￣￣￣
*/
package com.mosl.refreshandload;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

//test git commit  ahahah commit 
import com.mosl.refreshandload.widget.FooterLoadHolder;
import com.mosl.refreshandload.widget.HeaderRefreshHolder;
import com.mosl.refreshandload.widget.RefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity

{

    @Bind(R.id.ryView)
    RecyclerView ryView;
    @Bind(R.id.refreshLayout)
    RefreshLayout refreshLayout;
    private List<TestEntity> datas = new ArrayList<>();
    private HomeAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        refreshLayout.addHeaderView(new HeaderRefreshHolder(this));
        refreshLayout.addFooterView(new FooterLoadHolder(this));
        refreshLayout.setListener(new RefreshLayout.RefreshListener()
        {
            @Override
            public void onRefresh(View releasedChild)
            {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshUi(true);
                    }
                },3000);

            }

            @Override
            public void onLoadMore(View releasedChild)
            {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshUi(false);
                    }
                },3000);
            }

        });
        adapter = new HomeAdapter();
        ryView.setLayoutManager(new LinearLayoutManager(this));
        ryView.setAdapter(adapter);


        refreshLayout.refresh();
//        presenter.getWechat();
//        presenter.down();
    }

    public void refreshUi(boolean needFresh)
    {

        refreshLayout.onComplete(ryView);
        if (needFresh)
            datas.clear();
        for(int i=0;i<10;i++){
            TestEntity testEntity = new TestEntity();
            testEntity.title = "测试"+i;
            datas.add(testEntity);
        }

        adapter.notifyDataSetChanged();
    }
    class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder>
    {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                    MainActivity.this).inflate(R.layout.main_item, parent,
                    false));
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position)
        {
            holder.tv.setText(datas.get(position).title);
        }

        @Override
        public int getItemCount()
        {
            return datas.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder
        {

            TextView tv;

            public MyViewHolder(View view)
            {
                super(view);
                tv = (TextView) view.findViewById(R.id.tvTitle);
            }
        }
    }

    @Override
    protected void onDestroy()
    {
        ButterKnife.unbind(this);
        super.onDestroy();
    }
}
