package com.lx.framework.binding.viewadapter.banner;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.lx.framework.binding.command.BindingCommand;
import com.lx.framework.entity.OnPageScrolled;
import com.lx.framework.entity.XBannerDataWrapper;
import com.stx.xhb.xbanner.XBanner;

import androidx.databinding.BindingAdapter;
import androidx.viewpager.widget.ViewPager;

/**
 * Created by lx on 2017/6/16.
 */

public class ViewAdapter {
    @BindingAdapter(value = {"loadImageCommand"}, requireAll = false)
    public static void loadImage(final XBanner xBanner, final BindingCommand<XBannerDataWrapper> bindingCommand) {
        xBanner.loadImage(new XBanner.XBannerAdapter() {
            @Override
            public void loadBanner(XBanner banner, Object model, View view, int position) {
                bindingCommand.execute(new XBannerDataWrapper(banner, model, view, position));
            }
        });
    }

    @BindingAdapter(value = {"onPageChangeCommand"}, requireAll = false)
    public static void setOnPageChangeListener(final XBanner xBanner, final BindingCommand<OnPageScrolled> bindingCommand) {
        xBanner.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                bindingCommand.execute(new OnPageScrolled(position, positionOffset, positionOffsetPixels));
            }
        });
    }
}
