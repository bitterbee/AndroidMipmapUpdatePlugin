package com.netease.tools.model;

/**
 * Created by zyl06 on 2018/7/20.
 */
public enum ImgStatus {
    ADD(0),
    DELETE(1),
    MODIFY(2),
    NO_MODIFY(3),
    UNKNOWN(4);

    private int status;
    ImgStatus(int status) {
        this.status = status;
    }
}
