package com.tong.drag.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.tong.drag.adapter.DragListViewAdapter;

/***
 * 自定义可拖动的ListView，需要配合自定义的Adapter一起使用
 *
 * @author tongwenwu
 * @since 2017-2-14
 */
public class DragListView extends ListView
{

    /** 被拖拽的项，其实就是一个ImageView */
    private ImageView dragImageView;

    /** 拖拽对象 */
    private Object moveitem;

    /** 手指按下的时候列表项在listview中的位置 */
    private int dragPosition;

    /** 用于交换列表项用 */
    private int savePosition;

    /** 手指拖动的时候，当前拖动项在列表中的位置 */
    private int movePosition;

    /** 在当前数据项中的位置 */
    private int dragPoint;

    /** 当前视图和屏幕的距离(这里只使用了y方向上) */
    private int dragOffset;

    /** windows窗口控制类 */
    private WindowManager windowManager;

    /** 用于控制拖拽项的显示的参数 */
    private WindowManager.LayoutParams windowParams;

    private View itemView;

    private DragListViewAdapter mSelectAdapter;

    private int mEventY = 0;

    private Object mReplace;

    private Context context;

    public DragListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.context = context;
    }

    @Override
    public void setAdapter(ListAdapter adapter)
    {
        super.setAdapter(adapter);
        mSelectAdapter = (DragListViewAdapter) adapter;
        mReplace = mSelectAdapter.getListItemContent();
    }

    // 拦截touch事件，其实就是加一层控制
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            int x = (int) ev.getX();
            int y = (int) ev.getY();
            dragPosition = pointToPosition(x, y);
            // 判断当前xy值是否在item上如果在返回改item的position否则 返回INVALID_POSITION（-1）
            if (dragPosition == AdapterView.INVALID_POSITION) {
                return super.onInterceptTouchEvent(ev);
            }
            savePosition = dragPosition;
            movePosition = dragPosition;
            moveitem = mSelectAdapter.getItem(dragPosition);
            itemView = getChildAt(dragPosition - getFirstVisiblePosition());
            // // 判断当前View时候是一个ViewGroup
            // if (itemView instanceof ViewGroup) {
            //     itemView = (ViewGroup) itemView;// 获取当前点击的view
            // }
            dragPoint = y - itemView.getTop();// 点击坐标-view的上边界
            dragOffset = (int) (ev.getRawY() - y);// 整个屏幕中的y坐标-listview中的y坐标,即偏移量

            itemView.setDrawingCacheEnabled(true);
            Bitmap bm = Bitmap.createBitmap(itemView.getDrawingCache());
            mSelectAdapter.remove(moveitem);
            mSelectAdapter.insert(mReplace, dragPosition);
            startDrag(bm, y);
            return true;
        }
        return false;
    }

    /**
     * 触摸事件
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev)
    {
        int action = ev.getAction();
        if (dragImageView != null && dragPosition != INVALID_POSITION) {
            switch (action) {
                case MotionEvent.ACTION_UP:
                    stopDrag();
                    insertLastData(movePosition);
                    mEventY = 0;
                    itemView.destroyDrawingCache();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int moveY = (int) ev.getY();
                    mEventY = moveY;
                    onDrag(moveY, (int) ev.getRawY());
                    break;
                case MotionEvent.ACTION_DOWN:
                    break;
            }
            return true;
        }
        else {
            return super.onTouchEvent(ev);
        }
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        if (mEventY != 0) {
            if (mEventY != 0 && mEventY <= 0) {
                setSelectionFromTop(getFirstVisiblePosition(), getChildAt(0).getTop() + 3);
            }
            else if (mEventY >= getHeight()) {
                setSelectionFromTop(getFirstVisiblePosition(), getChildAt(0).getTop() - 3);
            }
        }
    }

    /**
     * 准备拖动，初始化拖动项的图像
     *
     * @param bm 缓存的图像
     * @param y  竖轴
     */
    @SuppressLint ("WrongConstant")
    public void startDrag(Bitmap bm, int y)
    {
        stopDrag();

        windowParams = new WindowManager.LayoutParams();
        windowParams.gravity = Gravity.TOP;
        windowParams.x = 0;

        windowParams.y = y - dragPoint + dragOffset;
        windowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        windowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                             WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
                             WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                             WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        windowParams.format = PixelFormat.TRANSLUCENT;
        windowParams.windowAnimations = 0;

        ImageView imageView = new ImageView(this.context);
        imageView.setImageBitmap(bm);
        // 设置移动缓存条目的背景
        imageView.setBackgroundColor(Color.alpha(Color.GRAY));
        windowManager = (WindowManager) this.context.getSystemService("window");
        windowManager.addView(imageView, windowParams);
        dragImageView = imageView;
    }

    /**
     * 停止拖动，移除拖动项的图像
     */
    public void stopDrag()
    {
        if (dragImageView != null) {
            windowManager.removeView(dragImageView);
            dragImageView = null;
            itemView.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    /**
     * 插入数据
     *
     * @param position
     */
    private void insertLastData(int position)
    {
        Object dragItem = mSelectAdapter.getItem(position);
        mSelectAdapter.remove(dragItem);
        mSelectAdapter.insert(moveitem, position);
    }

    /**
     * 拖动执行，在Move方法中执行
     */
    public void onDrag(int y, int rawY)
    {
        if (y < 0) { // 超出上边界，设为最小值位置0
            y = 0;
        }
        else if (y > getHeight()) {
            y = getHeight();

        }
        postInvalidate();
        if (dragImageView != null) {
            windowParams.alpha = 0.8f;
            // windowParams.y = y - dragPoint + dragOffset;
            windowParams.y = rawY - dragPoint;
            windowManager.updateViewLayout(dragImageView, windowParams);
        }
        // 为了避免滑动到分割线的时候，返回-1的问题
        int tempPosition = pointToPosition(0, y);
        if (tempPosition != INVALID_POSITION) {
            movePosition = tempPosition;
            onDrop();
        }

    }

    /**
     * 根据拖动的位置在列表中放下
     */
    public void onDrop()
    {
        if (movePosition > savePosition) {// 手势向下移动
            ChangeItemDown(savePosition, movePosition);
            savePosition = movePosition;
        }
        else if (movePosition < savePosition) {// 手势向上移动
            ChangeItemUp(savePosition, movePosition);
            savePosition = movePosition;
        }
    }

    /**
     * 向下拖动
     *
     * @param save 驻留位置
     * @param move 移动位置
     */
    private void ChangeItemDown(int save, int move)
    {
        Object item = mSelectAdapter.getItem(move);
        mSelectAdapter.remove(mSelectAdapter.getItem(save));
        mSelectAdapter.remove(item);
        mSelectAdapter.insert(item, save);
        mSelectAdapter.insert(mReplace, move);
    }

    /**
     * 向上拖动
     *
     * @param save 驻留位置
     * @param move 移动位置
     */
    private void ChangeItemUp(int save, int move)
    {
        Object item = mSelectAdapter.getItem(move);
        mSelectAdapter.remove(mSelectAdapter.getItem(save));
        mSelectAdapter.remove(item);
        mSelectAdapter.insert(mReplace, move);
        mSelectAdapter.insert(item, save);
    }
}