package com.lx.framework.binding.viewadapter.viewpager;

import com.lx.framework.binding.command.BindingCommand;
import com.lx.framework.entity.ViewPagerDataWrapper;

import androidx.databinding.BindingAdapter;
import androidx.viewpager.widget.ViewPager;

/**
 * Created by lx on 2017/6/18.
 */
public class ViewAdapter {
    @BindingAdapter(value = {"onPageScrolledCommand", "onPageSelectedCommand", "onPageScrollStateChangedCommand"}, requireAll = false)
    public static void onScrollChangeCommand(final ViewPager viewPager,
                                             final BindingCommand<ViewPagerDataWrapper> onPageScrolledCommand,
                                             final BindingCommand<Integer> onPageSelectedCommand,
                                             final BindingCommand<Integer> onPageScrollStateChangedCommand) {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            private int state;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (onPageScrolledCommand != null) {
                    onPageScrolledCommand.execute(new ViewPagerDataWrapper(position, positionOffset, positionOffsetPixels, state));
                }
            }

            @Override
            public void onPageSelected(int position) {
                if (onPageSelectedCommand != null) {
                    onPageSelectedCommand.execute(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                this.state = state;
                if (onPageScrollStateChangedCommand != null) {
                    onPageScrollStateChangedCommand.execute(state);
                }
            }
        });

    }
}
