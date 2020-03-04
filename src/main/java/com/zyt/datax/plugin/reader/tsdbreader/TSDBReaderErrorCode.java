package com.zyt.datax.plugin.reader.tsdbreader;

import com.alibaba.datax.common.spi.ErrorCode;


public enum TSDBReaderErrorCode implements ErrorCode {

    REQUIRED_VALUE("TSDBReader-00", "缺失必要的值"),
    ILLEGAL_VALUE("TSDBReader-01", "值非法");

    private final String code;
    private final String description;

    TSDBReaderErrorCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public String toString() {
        return String.format("Code:[%s], Description:[%s]. ", this.code, this.description);
    }
}
