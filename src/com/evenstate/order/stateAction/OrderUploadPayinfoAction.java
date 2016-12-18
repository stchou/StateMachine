package com.evenstate.order.stateAction;

import com.evenstate.order.OrderStateContext;
import com.evenstate.order.OrderStateEnum;

/**
 * 上传凭条操作
 * @author zhoushengtao
 * @since 2016/12/18.
 */
public class OrderUploadPayinfoAction extends IOrderAction {
    @Override
    public void modify(OrderStateContext context) {
        context.getOrder().setOrderInfo("change info");
        context.getOrderSection().transNewState(OrderStateEnum.CONFIRM);
    }

    @Override
    public void cancel(OrderStateContext context) {

    }

    @Override
    public void auditPass(OrderStateContext context) {

    }

    @Override
    public void auditFailure(OrderStateContext context) {

    }
}
