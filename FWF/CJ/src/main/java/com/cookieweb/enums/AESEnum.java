package com.cookieweb.enums;

import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;

public enum AESEnum {

    FUNC1_ECB_PADDING(0, "无偏移", Mode.ECB, Padding.PKCS5Padding),
    FUNC2_CTS_PADDING(1, "有偏移", Mode.CTS, Padding.PKCS5Padding),
    FUNC3_CBC_PADDING(2, "有偏移", Mode.CBC, Padding.PKCS5Padding),

    ;

    private final int mark;
    private final String tips;
    private final Mode aesMode;
    private final Padding paddingMode;

    public Mode getAesMode(){
        return this.aesMode;
    }

    public Padding getPaddingMode(){
        return this.paddingMode;
    }


    AESEnum(int mark, String tips, Mode aesMode, Padding paddingMode) {
        this.mark = mark;
        this.tips = tips;
        this.aesMode = aesMode;
        this.paddingMode = paddingMode;
    }
}
