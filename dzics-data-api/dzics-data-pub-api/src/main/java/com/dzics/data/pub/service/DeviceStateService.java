package com.dzics.data.pub.service;

public interface DeviceStateService<T> {
    T getDeivceState(String orderNo, String lineNo);
}
