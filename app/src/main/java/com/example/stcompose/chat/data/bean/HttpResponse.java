package com.example.stcompose.chat.data.bean;

/**
 * author : heyueyang time   : 2023/05/18 desc   : version: 1.0
 */
public class HttpResponse<T> {

  private int errorCode;
  private String errorMsg;
  private T data;

  public int getCode() {
    return errorCode;
  }

  public String getMsg() {
    return errorMsg;
  }

  public T getData() {
    return data;
  }

  public void setErrorCode(int errorCode) {
    this.errorCode = errorCode;
  }

  public void setErrorMsg(String errorMsg) {
    this.errorMsg = errorMsg;
  }

  public void setData(T data) {
    this.data = data;
  }
}