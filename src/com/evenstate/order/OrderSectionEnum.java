package com.evenstate.order;

/***
 * 订单环节枚举
 *
 * @author zhoushengtao
 * @since 2016-12-18 16:48:10
 */
public enum OrderSectionEnum {
    /**预约环节*/
    SUBSCRIBE(10),
    /**审核环节*/
    AUDIT(20),
    /**返佣环节*/
    REBATE(30);

    /** 环节入口订单编号 */
    private int entranceOrderNo;

    /**
     * 构造
     * @param entranceOrderNo 订单环节入口编号
     */
    OrderSectionEnum(int entranceOrderNo) {
        this.entranceOrderNo = entranceOrderNo;
    }

    /**
     * 获取该环节的入口状态
     * @return 入口状态值
     */
    public int getEntranceOrderNo() {
        return entranceOrderNo;
    }

}