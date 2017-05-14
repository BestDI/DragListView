package com.tong.drag.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tong.drag.R;
import com.tong.drag.adapter.DragListViewAdapter;
import com.tong.drag.view.DragListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity
{

    private DragListView mDragListView;

    // 初始化数据
    private static ArrayList<String> mDatas = new ArrayList<>(
            Arrays.asList("adbd", "ebcd!", "@@cds", "$$sdf", "^&235"));

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDragListView = (DragListView) findViewById(R.id.dragListView);
        DragAdapter dragAdapter = new DragAdapter();
        mDragListView.setAdapter(dragAdapter);

    }

    /**
     * 数据适配器
     *
     * @see DragListViewAdapter
     */
    private class DragAdapter extends DragListViewAdapter
    {

        @Override
        public int getListCount()
        {
            return mDatas.size();
        }

        @Override
        public Object getListItem(int position)
        {
            if (position >= mDatas.size()) {
                return null;
            }
            return mDatas.get(position);
        }

        @Override
        public long getListItemId(int position)
        {
            return position;
        }

        @Override
        public View getListView(int position, View arg1, ViewGroup arg2)
        {
            TextView textView = new TextView(MainActivity.this);
            textView.setPadding(10, 0, 0, 0);
            textView.setText(mDatas.get(position));
            textView.setTextSize(55);
            return textView;
        }

        @Override
        public void remove(Object item)
        {
            mDatas.remove(item);
        }

        @Override
        public void up(int position)
        {
            if (position == 0) {
                return;
            }

            String upLinkMan = mDatas.get(position);
            String downLinkMan = mDatas.get(position - 1);
            mDatas.set(position, downLinkMan);
            mDatas.set(position - 1, upLinkMan);
            this.notifyDataSetChanged();
        }

        @Override
        public void down(int position)
        {
            if (position == mDatas.size() - 1) {
                return;
            }

            String downLinkMan = mDatas.get(position);
            String upLinkMan = mDatas.get(position + 1);
            mDatas.set(position, upLinkMan);
            mDatas.set(position + 1, downLinkMan);
            this.notifyDataSetChanged();
        }

        @Override
        public void deleteAll()
        {
            mDatas.clear();
            this.notifyDataSetChanged();
        }

        @Override
        public void updateData(List<Object> list)
        {

        }

        @Override
        public void deleteItem(int position)
        {
            if (position > mDatas.size() - 1) {
                return;
            }
            mDatas.remove(position);
            this.notifyDataSetChanged();
        }

        @Override
        public void insert(Object item, int index)
        {
            String contact = (String) item;
            mDatas.add(index, contact);
            this.notifyDataSetChanged();
        }

        @Override
        public List<Object> getListObject()
        {
            List<Object> listObject = new ArrayList<Object>();
            for (String contact : mDatas) {
                listObject.add(contact);
            }

            return listObject;
        }

        @Override
        public Object getListItemContent()
        {
            return null;
        }
    }
}
