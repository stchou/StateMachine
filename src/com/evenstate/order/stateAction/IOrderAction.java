package com.evenstate.order.stateAction;

import com.evenstate.order.OrderStateContext;

/**
 * @author zhoushengtao
 * @since 2016/12/17.
 */
public abstract class IOrderAction {

    public abstract void modify(OrderStateContext context);

    public abstract void cancel(OrderStateContext context);

    public abstract void auditPass(OrderStateContext context);

    public abstract void auditFailure(OrderStateContext context);
}
