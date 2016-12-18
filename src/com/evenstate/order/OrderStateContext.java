package com.evenstate.order;

import com.bean.order.Order;

/**
 *
 * 存储每一个操作所需要的上下文属性值
 *
 * @author zhoushengtao
 * @since 2016/12/17.
 */
public class OrderStateContext {
    private Order order;
    private String otherInfo;
    private OrderStateMachine orderSection;


    public OrderStateContext(Order order, String otherInfo) {
        this.order = order;
        this.otherInfo = otherInfo;
    }


    public void setOrderSection(OrderStateMachine orderSection) {
        this.orderSection = orderSection;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public OrderStateMachine getOrderSection() {
        return orderSection;
    }

    public String getOtherInfo() {
        return otherInfo;
    }

    public void setOtherInfo(String otherInfo) {
        this.otherInfo = otherInfo;
    }

    public Order getOrder() {
        return order;
    }
}
