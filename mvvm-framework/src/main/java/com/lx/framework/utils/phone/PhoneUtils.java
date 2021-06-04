package com.lx.framework.utils.phone;

import android.content.Intent;
import android.net.Uri;

import com.lx.framework.utils.Utils;

public class PhoneUtils {
    public static void callPhone(String phoneNumber) {
        call(Intent.ACTION_CALL, "tel:",phoneNumber);
    }

    public static void sendSMS(String phoneNumber) {
        call(Intent.ACTION_SENDTO, "smsto:",phoneNumber);
    }

    private static void call(String actionCall, String s,String phoneNumber) {
        Intent intent_call = new Intent(actionCall, Uri.parse(s + phoneNumber));
        Utils.getContext().startActivity(intent_call);
    }
}
