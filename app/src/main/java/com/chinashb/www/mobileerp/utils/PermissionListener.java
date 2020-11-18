package com.chinashb.www.mobileerp.utils;

import android.support.annotation.NonNull;

public interface PermissionListener {

    /**
     * 通过授权
     * @param permission
     */
    void permissionAccept(@NonNull String[] permission);

    /**
     * 拒绝授权
     * @param permission
     */
    void permissionRefused(@NonNull String[] permission);
}
