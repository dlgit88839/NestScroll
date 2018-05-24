package com.dongliang.nestscrollapp.nestscroll;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dongliang.nestscrollapp.R;
/**
 * NestScrollParent 需要和具体的布局一起使用
 * created by dongliang
 *  2018/5/24  11:00
 */
public class NestScrollParent extends RelativeLayout implements NestedScrollingParent {
    private NestedScrollingParentHelper nestedScrollingParentHelper;
    private int mWidth, mHeight;
    private View mView;  //顶部view 即 childview
    private ArgbEvaluator argbEvaluator;
    private TextView tv1;  //展开时显示的textview
    private TextView tv2;  //收缩时显示的textview

    public NestScrollParent(Context context) {
        super(context);
        init(context);
    }

    public NestScrollParent(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public NestScrollParent(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        nestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        argbEvaluator = new ArgbEvaluator();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
//        if (target instanceof MyNestedScrollChild) {
//            return true;
//        }
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (mView == null) {
            mView = getChildAt(0);
            tv1 = mView.findViewById(R.id.tv1);
            tv2 = mView.findViewById(R.id.tv2);
        }
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {
        nestedScrollingParentHelper.onNestedScrollAccepted(child, target, nestedScrollAxes);
    }

    /**
     * 手指松开后 根据当前已经滚动的距离进行动画
     * created by dongliang
     * 2018/5/24  10:59
     */
    @Override
    public void onStopNestedScroll(View target) {
        nestedScrollingParentHelper.onStopNestedScroll(target);
        int maxY = mView.getHeight() - tv2.getHeight();
        float progress = Math.abs(mView.getTop()) / (float) maxY;
        if (progress > 0 && progress < 0.5) {
            startAnimation(mView.getTop(), 0);
        } else if (progress >= 0.5 && progress < 1) {
            startAnimation(mView.getTop(), -maxY);
        }
    }

    /**
     * 前三个是输入参数 consumed是输出参数 【0】是消耗掉的x 【1】是消耗掉的y
     * created by dongliang
     * 2018/5/24  10:58
     */

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        Log.e("parent", "preScroll" + dy);
        if (zoomin((int) (dy * 0.5)) || zoomout((int) (dy * 0.5))) {//如果需要显示或隐藏图片，即需要自己(parent)滚动
//            scrollBy(0, -dy);//滚动
            Log.e("parent", "消耗" + dy);
            consumed[1] = dy;//告诉child我消费了多少
        }
    }


    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {

    }

    //返回值：是否消费了fling
    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {

        return false;
    }

    //返回值：是否消费了fling
    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {


        return false;
    }


    @Override
    public int getNestedScrollAxes() {
        return nestedScrollingParentHelper.getNestedScrollAxes();
    }

    /**
     * 头部布局缩小
     * created by dongliang
     * 2018/5/22  17:59
     */
    private boolean zoomout(int y) {
        if (y > 0) {       //向上滑动
            int maxY = mView.getHeight() - tv2.getHeight();
            if (Math.abs(mView.getTop()) < maxY) {
                int delatY = Math.abs(mView.getTop() - y) - maxY > 0 ? -maxY - mView.getTop() : -y;
                mView.offsetTopAndBottom(delatY);
                View view = getChildAt(1);
                view.layout(0, mView.getBottom(), getWidth(), getHeight());
                float progress = Math.abs(mView.getTop()) / (float) maxY;
                //根据滚动的进度对子View的位置大小等进行调整
                changeViewByProgress(progress);
                return true;
            }
        }

        return false;
    }

    /**
     * 头部布局放大
     * created by dongliang
     * 2018/5/22  18:01
     */
    private boolean zoomin(int y) {
        if (y < 0) {
            RecyclerView view = (RecyclerView) getChildAt(1);//想下滑动
            int maxY = mView.getHeight() - tv2.getHeight();
            if (mView.getTop() < 0 && getScollYDistance(view) == 0) {
                int delatY = mView.getTop() - y > 0 ? -mView.getTop() : -y;
                Log.e("delatY", delatY + "");
                mView.offsetTopAndBottom(delatY);      //改变titleView
                view.layout(0, mView.getBottom(), getWidth(), getHeight());//改变recyleview

                float progress = Math.abs(mView.getTop()) / (float) maxY;
                //根据滚动的进度对子View的位置大小等进行调整
                changeViewByProgress(progress);

                return true;
            }

        }


        return false;
    }

    /**
     * 获取RecyclerView的滑动距离 仅限于LinearLayoutManager
     * created by dongliang
     * 2018/5/24  10:51
     */
    private int getScollYDistance(RecyclerView recyclerView) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int position = layoutManager.findFirstVisibleItemPosition();
        View firstVisiableChildView = layoutManager.findViewByPosition(position);
        int itemHeight = firstVisiableChildView.getHeight();
        return (position) * itemHeight - firstVisiableChildView.getTop();
    }

    /**
     * 根据滑动进度对 view透明度位置等进行更改
     * created by dongliang
     * 2018/5/24  10:52
     */


    private void changeViewByProgress(float progress) {
        //修改透明度
        tv1.setAlpha(1 - progress * 2);
        tv2.setAlpha(progress);

        //修改tv2的可见状态
        if (progress > 0.2) {
            tv2.setVisibility(VISIBLE);
        } else {
            tv2.setVisibility(INVISIBLE);
        }
        int x = getWidth() / 2 - tv2.getWidth() / 2;

        //tv2水平位移
        tv2.offsetLeftAndRight((int) (x * progress - tv2.getLeft()));
        //改变背景颜色
        mView.setBackgroundColor((int) argbEvaluator.evaluate(
                progress,
                getResources().getColor(R.color.colorAccent),
                getResources().getColor(R.color.colorPrimaryDark)));
    }

    /**
     * 手指松开后根据进度进行动画
     * created by dongliang
     * 2018/5/24  10:57
     */
    private void startAnimation(int begin, int end) {
        Log.e("animation", "begin" + begin + "end" + end);
        ValueAnimator valueAnimator = ValueAnimator.ofInt(begin, end);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                mView.offsetTopAndBottom(value - mView.getTop());
                int maxY = mView.getHeight() - tv2.getHeight();
                View view = getChildAt(1);
                view.layout(0, mView.getBottom(), getWidth(), getHeight());
                float progress = Math.abs(mView.getTop()) / (float) maxY;
                //根据滚动的进度对子View的位置大小等进行调整
                changeViewByProgress(progress);
            }
        });
        valueAnimator.setDuration(100);
        valueAnimator.start();

    }
}
