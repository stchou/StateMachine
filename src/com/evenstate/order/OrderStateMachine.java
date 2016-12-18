package com.evenstate.order;

import com.bean.order.Order;

import java.util.Observable;

/**
 * 订单狀態機操作单例
 *
 * @author zhoushengtao
 * @since 2016/12/17.
 */
public class OrderStateMachine extends Observable {
    /** 订单环节操作单例 */
    private static OrderStateMachine sOrderSection;
    /** 当前状态 */
    private OrderStateEnum mState = null;
    /** 订单状态上下文 */
    private OrderStateContext mContext;


    /**
     * 隐藏构造
     */
    private OrderStateMachine(Order order) {
        mContext = new OrderStateContext(order,"testinfo");
    }

    /**
     * 单例
     * @param order 订单
     * @return orderSection
     */
    public static OrderStateMachine getInstance(Order order) {
        if (sOrderSection == null) {
            sOrderSection = new OrderStateMachine(order);
        }
        sOrderSection.setState(OrderStateEnum.getState(order.getOrderNo()));
        sOrderSection.mContext.setOrder(order);
        sOrderSection.mContext.setOrderSection(sOrderSection);
        assert(sOrderSection.getState() != null);
        return sOrderSection;
    }

    /**
     * 修改
     */
    public void modify() {
        mState.getOrderAction().modify(mContext);
    }

    /**
     * 取消
     */
    public void cancel() {
        mState.getOrderAction().cancel(mContext);
    }

    /**
     * 审核通过
     */
    public void auditPass() {
        mState.getOrderAction().auditPass(mContext);
    }

    /**
     * 审核失败
     */
    public void auditFailure() {
        mState.getOrderAction().auditFailure(mContext);
    }
    /**
     *  獲取當前狀態
     */
    private OrderStateEnum getState() {
        return mState;
    }
    /**
     *  設置新狀態
     * @param newState 新狀態
     */
    private void setState(OrderStateEnum newState) {
        mState = newState;
    }

    /**
     * 切换新状态
     * @param newState 新状态
     */
    public void transNewState(OrderStateEnum newState) {
        if (null != mState && null != newState) {
            // 上层状态状态切换，判断状态入口是否正确
            if(mState.getOrderSectionEnum() != newState.getOrderSectionEnum()) {
                if (newState.getOrderSectionEnum().getEntranceOrderNo() == newState.getOrderNo()) {
                    mState = newState;
                }
            } else {
                mState = newState;
            }
            notifyObservers(newState);
        }
    }
}
