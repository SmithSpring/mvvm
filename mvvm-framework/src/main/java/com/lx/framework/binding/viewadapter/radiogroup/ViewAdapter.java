package com.lx.framework.binding.viewadapter.radiogroup;

import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.lx.framework.binding.command.BindingCommand;

import androidx.annotation.IdRes;
import androidx.databinding.BindingAdapter;

/**
 * Created by lx on 2017/6/18.
 */
public class ViewAdapter {
    @BindingAdapter(value = {"onCheckedChangedCommand","isRepeat"}, requireAll = false)
    public static void onCheckedChangedCommand(final RadioGroup radioGroup, final BindingCommand<String> bindingCommand,final boolean isRepeat) {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
                if (isRepeat && !radioButton.isPressed()){
                    return;
                }
                bindingCommand.execute(radioButton.getText().toString());
            }
        });
    }
}
