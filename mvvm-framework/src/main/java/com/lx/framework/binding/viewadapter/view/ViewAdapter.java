package com.lx.framework.binding.viewadapter.view;

import android.annotation.SuppressLint;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jakewharton.rxbinding4.view.RxView;
import com.lx.framework.binding.command.BindingCommand;

import java.util.concurrent.TimeUnit;

import androidx.databinding.BindingAdapter;
import io.reactivex.rxjava3.functions.Consumer;
import kotlin.Unit;

public class ViewAdapter {
    //防重复点击间隔(秒)
    public static final int CLICK_INTERVAL = 1;

    /**
     * requireAll 是意思是是否需要绑定全部参数, false为否
     * View的onClick事件绑定
     * onClickCommand 绑定的命令,
     * isThrottleFirst 是否开启防止过快点击
     */
    @SuppressLint("CheckResult")
    @BindingAdapter(value = {"onClickCommand", "isThrottleFirst"}, requireAll = false)
    public static void onClickCommand(View view, final BindingCommand<Void> clickCommand, final boolean isThrottleFirst) {
        if (isThrottleFirst) {
            RxView.clicks(view)
                    .subscribe(new Consumer<Unit>() {
                        @Override
                        public void accept(Unit unit) throws Throwable {
                            if (clickCommand != null) clickCommand.execute();
                        }
                    });
        } else {
            RxView.clicks(view)
                    .throttleFirst(CLICK_INTERVAL, TimeUnit.SECONDS)//1秒钟内只允许点击1次
                    .subscribe(new Consumer<Unit>() {
                        @Override
                        public void accept(Unit unit) throws Throwable {
                            if (clickCommand != null) clickCommand.execute();
                        }
                    });
        }
    }

    /**
     * view的onLongClick事件绑定
     */
    @SuppressLint("CheckResult")
    @BindingAdapter(value = {"onLongClickCommand"}, requireAll = false)
    public static void onLongClickCommand(View view, final BindingCommand<Void> clickCommand) {
        RxView.longClicks(view)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) throws Throwable {
                        if (clickCommand != null) clickCommand.execute();
                    }
                });
    }

    /**
     * 回调控件本身
     *
     * @param currentView
     * @param bindingCommand
     */
    @BindingAdapter(value = {"currentView"}, requireAll = false)
    public static void replyCurrentView(View currentView, BindingCommand<View> bindingCommand) {
        if (bindingCommand != null) {
            bindingCommand.execute(currentView);
        }
    }

    /**
     * view是否需要获取焦点
     */
    @BindingAdapter({"requestFocus"})
    public static void requestFocusCommand(View view, final Boolean needRequestFocus) {
        if (needRequestFocus) {
            view.setFocusableInTouchMode(true);
            view.requestFocus();
        } else {
            view.clearFocus();
        }
    }

    /**
     * view的焦点发生变化的事件绑定
     */
    @BindingAdapter({"onFocusChangeCommand"})
    public static void onFocusChangeCommand(View view, final BindingCommand<Boolean> onFocusChangeCommand) {
        RxView.focusChanges(view).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean hasFocus) throws Throwable {
                if (onFocusChangeCommand != null) onFocusChangeCommand.execute(hasFocus);
            }
        });
    }

    /**
     * view的显示隐藏
     */
    @BindingAdapter(value = {"isVisible"}, requireAll = false)
    public static void isVisible(View view, final Boolean visibility) {
        try {
            RxView.visibility(view).accept(visibility);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @BindingAdapter({"onTouchCommand"})
    public static void onTouchCommand(View view, final BindingCommand<MotionEvent> onTouchCommand) {
        RxView.touches(view).subscribe(new Consumer<MotionEvent>() {
            @Override
            public void accept(MotionEvent motionEvent) throws Throwable {
                if (onTouchCommand != null) onTouchCommand.execute(motionEvent);
            }
        });
    }

    @BindingAdapter("android:layout_marginTop")
    public static void setTopMargin(View view, int topMargin) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        layoutParams.setMargins(layoutParams.leftMargin, topMargin,
                layoutParams.rightMargin,layoutParams.bottomMargin);
        view.setLayoutParams(layoutParams);
    }

    @BindingAdapter("android:layout_marginBottom")
    public static void setBottomMargin(View view, int bottomMargin) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin,
                layoutParams.rightMargin,bottomMargin);
        view.setLayoutParams(layoutParams);
    }

    @BindingAdapter("android:layout_marginLeft")
    public static void setLeftMargin(View view, int leftMargin) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        layoutParams.setMargins(leftMargin, layoutParams.topMargin,
                layoutParams.rightMargin,layoutParams.bottomMargin);
        view.setLayoutParams(layoutParams);
    }

    @BindingAdapter("android:layout_marginRight")
    public static void setRightMargin(View view, int rightMargin) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin,
                rightMargin,layoutParams.bottomMargin);
        view.setLayoutParams(layoutParams);
    }

    @BindingAdapter("android:layout_margin")
    public static void setMargin(View view, int margin) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        layoutParams.setMargins(margin, margin,margin,margin);
        view.setLayoutParams(layoutParams);
    }

    @BindingAdapter("android:paddingLeft")
    public static void setPaddingLeft(View view, int paddingLeft) {
        view.setPadding(paddingLeft,
                view.getPaddingTop(),
                view.getPaddingRight(),
                view.getPaddingBottom());
    }

    @BindingAdapter("android:paddingRight")
    public static void setPaddingRight(View view, int paddingRight) {
        view.setPadding(view.getPaddingLeft(),
                view.getPaddingTop(),
                paddingRight,
                view.getPaddingBottom());
    }

    @BindingAdapter("android:paddingTop")
    public static void setPaddingTop(View view, int paddingTop) {
        view.setPadding(view.getPaddingLeft(),
                paddingTop,
                view.getPaddingRight(),
                view.getPaddingBottom());
    }

    @BindingAdapter("android:paddingBottom")
    public static void setPaddingBottom(View view, int paddingBottom) {
        view.setPadding(view.getPaddingLeft(),
                view.getPaddingTop(),
                view.getPaddingRight(),
                paddingBottom);
    }

    @BindingAdapter("android:padding")
    public static void setPadding(View view, int padding) {
        view.setPadding(padding,
                padding,
                padding,
                padding);
    }

    @BindingAdapter("android:textSize")
    public static void setTextSize(TextView textView,float textSize){
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,textSize);
    }
}
