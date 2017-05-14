package com.tong.drag.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * 可拖动ListView的数据适配器，实现方法主要为up、down说持有的移动方法
 *
 * @author Tong Ww
 */
public abstract class DragListViewAdapter extends BaseAdapter
{

    /** 数据条目数 */
    public abstract int getListCount();

    /** 获取条目对象 */
    public abstract Object getListItem(int position);

    /** 条目ID，一般可以用postion */
    public abstract long getListItemId(int position);

    /** 获取条目视图 */
    public abstract View getListView(int position, View arg1, ViewGroup arg2);

    /** 移除条目 */
    public abstract void remove(Object item);

    /** 上移数据 */
    public abstract void up(int position);

    /** 下移数据 */
    public abstract void down(int position);

    /** 删除所有数据 */
    public abstract void deleteAll();

    /** 更新数据 */
    public abstract void updateData(List<Object> list);

    /** 删除条目 */
    public abstract void deleteItem(int position);

    /** 插入数据 */
    public abstract void insert(Object item, int index);

    /** 获取数据对象 */
    public abstract List<Object> getListObject();

    /** 获取替代条目对象 */
    public abstract Object getListItemContent();

    @Override
    public int getCount()
    {
        return getListCount();
    }

    @Override
    public Object getItem(int position)
    {
        return getListItem(position);
    }

    @Override
    public long getItemId(int position)
    {
        return getListItemId(position);
    }

    @Override
    public View getView(int position, View arg1, ViewGroup arg2)
    {
        return getListView(position, arg1, arg2);
    }

}