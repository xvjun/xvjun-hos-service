package com.xvjun.bigdata.hos.server.error;


import com.xvjun.bigdata.hos.common.HosException;

public class HosServerException extends HosException {

    private int code;
    private String message;

    public HosServerException(int code, String message, Throwable cause){
        super(message,cause);
        this.code=code;
        this.message=message;
    }

    public HosServerException(int code, String message){
        super(message,null);
        this.code=code;
        this.message=message;
    }

    public int getCode() {
        return code;
    }


    public String getMessage() {
        return message;
    }

    @Override
    public int errorCode() {
        return this.code;
    }
}
