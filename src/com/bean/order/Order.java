package com.bean.order;

import com.evenstate.order.OrderStateEnum;

/**
 * 订单bean
 * @author zhoushengtao
 * @since 2016/12/18.
 */
public class Order {
    private int orderNo = OrderStateEnum.TOBE_SUBSCRIBE.getOrderNo();
    private String orderInfo;

    public int getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(int orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(String orderInfo) {
        this.orderInfo = orderInfo;
    }
}
