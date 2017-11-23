package rk.device.launcher.bean;

/**
 * Created by hanbin on 2017/11/23.
 */

public class BaseResult<T> {
    private int resuslt;
    private int code;
    private String message;
    private T data;

    public int getResuslt() {
        return resuslt;
    }

    public void setResuslt(int resuslt) {
        this.resuslt = resuslt;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
