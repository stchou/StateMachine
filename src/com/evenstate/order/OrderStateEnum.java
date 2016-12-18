package com.evenstate.order;

import com.evenstate.order.stateAction.IOrderAction;
import com.evenstate.order.stateAction.OrderConfirmAction;
import com.evenstate.order.stateAction.OrderSubscribeAction;
import com.evenstate.order.stateAction.OrderUploadPayinfoAction;

/**
 * 订单状态枚举
 *
 * @author zhoushengtao
 * @since 2016/12/18.
 */
public enum OrderStateEnum {
    TOBE_SUBSCRIBE(0, new OrderSubscribeAction(), OrderSectionEnum.SUBSCRIBE),
    SUBSCRIBE(10, new OrderSubscribeAction(), OrderSectionEnum.SUBSCRIBE),
    CONFIRM(20, new OrderConfirmAction(), OrderSectionEnum.AUDIT),
    UPLOAD_PAYINFO(30, new OrderUploadPayinfoAction(), OrderSectionEnum.REBATE);

    /**订单状态值*/
    private int orderNo;
    /**每一个状态的操作action*/
    private IOrderAction orderAction;
    /***属于的上层状态*/
    private OrderSectionEnum orderSectionEnum;

    /**
     * 构建函数
     * @param orderNo 状态值
     * @param orderAction 操作action
     * @param orderSectionEnum 上层状态
     */
    OrderStateEnum(int orderNo, IOrderAction orderAction, OrderSectionEnum orderSectionEnum) {
        this.orderNo = orderNo;
        this.orderAction = orderAction;
        this.orderSectionEnum = orderSectionEnum;
    }


    public IOrderAction getOrderAction() {
        return orderAction;
    }

    public int getOrderNo() {
        return orderNo;
    }

    public OrderSectionEnum getOrderSectionEnum() {
        return orderSectionEnum;
    }

    public static OrderStateEnum getState(int stateNo) {
        for (OrderStateEnum state : values()) {
            if(state.getOrderNo() == stateNo) {
                return state;
            }
        }
        return null;
    }

}
