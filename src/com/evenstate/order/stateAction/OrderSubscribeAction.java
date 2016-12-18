package com.evenstate.order.stateAction;

import com.evenstate.order.OrderStateContext;
import com.evenstate.order.OrderStateEnum;

/**
 * 订单预约操作
 *
 * @author zhoushengtao
 * @since 2016/12/18.
 */
public class OrderSubscribeAction extends IOrderAction {

    @Override
    public void modify(OrderStateContext context) {

    }

    @Override
    public void cancel(OrderStateContext context) {

    }

    @Override
    public void auditPass(OrderStateContext context) {
        context.getOrderSection().transNewState(OrderStateEnum.SUBSCRIBE);
    }

    @Override
    public void auditFailure(OrderStateContext context) {

    }
}
