
对于电商类的系统、游戏和公司内部流程系统来说，最复杂的莫过于处理其中的状态扭转。

如我公司的订单购买系统：
预约=>审核=>打款=>上传凭条=>凭条审核=>打款核验=>返佣对账=>返佣凭条审核=>返佣

这期间，每一个状态有包括了，“通过” / “拒绝”的操作。

通常来说，面对这样的需求，最容易想到的解决方案就是，定义不同的枚举值，不同状态之间的扭转就使用 **if-else** 或者 **switch-case** 来做判断。如下面所示，根据前端传递过来的不同操作值 orderOp来做状态操作判断，进行不同的状态处理。

```java
switch (orderOp) {
    case CONFIRM:
        orderStateManager.comfirmOrder();
        break;
    case REBATE:
        orderStateManager.rebate();
        break;
    case FAIL_REBATE:
        orderStateManager.failRebate();
        break;
    case PAYINFO_NOT_PASS:
        orderStateManager.failPaymentVoucher();
        break;
    case PAYINFO_PASS:
        orderStateManager.passPaymentVoucher();
        break;
    case ORDER_NOT_PASS:
        orderStateManager.refuseOrder();
        break;
    case ORDER_UPLOAD_COMMISSION:
        orderStateManager.uploadCommissionOrder();
        break;
    case ORDER_DELETE:
        orderStateManager.deteleOrder();
        break;
    case ORDER_CANCEL:
        orderStateManager.cancelOrder();
        break;
    default:
        throw new CustomerBaseException(CodeEnum.OP_NOT_PERMISSION);
}
```

对于短期来说，这个逻辑并没有什么不对，但是，随着需求不断的增加，操作权限的安全校验分离，它的跳转就会变的不可维护。然后，我们的系统状态扭转，就像似一个泥球，如下图所示：

![](http://img.blog.csdn.net/20161217125222430?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQveXp6c3Q=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

## 状态设计重新思考

### 缺陷
我们分析一下上面的代码逻辑缺陷：
1. **多余的orderOp操作**

将所有的状态扭转操作向前端暴露，如果能够猜对状态的枚举值，理论上是可以从任意状态跳转到任意状态。

2. **过多的状态判断**

过多的状态，会不断的增加switch-case和if-else判断，代码上难以阅读，后期开发风险过大。

3. **无法控制权限**

其实对于每一个状态的操作流程来说，并不是都具有跳转到下一个状态的权限的。比如，只有先购买才能付款、先付款才能发货等等。然而，我们在权限审核的时候，不可能每一个state都去review。

### 对症下药
根据存在的问题，寻找对应的解决方案。

1. **分析每一个状态的操作方式**

所有的操作都只有两种角色
- **操作者**
    - 修改状态属性
    - 取消
- **审核者**
    - 通过
    - 不通过

其实，这么一看每一个状态的操作还可以列举出来的，我们称之为：有限状态。

2. **定义状态扭转**

状态的跳转不再由swich-case来做操作，在一个固定的地方定义下一个操作，和与操作匹配的下一个状，一目了然。

3. **状态分层**

状态越来越多，我们可以对这么多状态进行一个归类。如，
- **购买前**
    - 预约状态
    - 预约审核状态
    - 付款
    - 已付款
- **购买中**
    - 待发货 
    - 待收货
    - 待评价
- **购买后**
    - 售后投诉
    - 投诉处理

对于每一个上层状态来说，我们不用过多的关心它的子状态在其中如何扭转。只需要定义，如只有在“购买前-已付款”状态才能跳转到“购买中”。“购买前”状态中的子状态如何跳转到购买前不用思考。

即如下所示：


![](http://img.blog.csdn.net/20161217125234555?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQveXp6c3Q=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)


其实对于整个设计模式来说，这种数据机构有一个专有的名词————状态机。状态机是AI中常用的一种架构，有很多中实现方式。而且实现简单，甚至用一个switch-case就可以了；然而对于多流程结构化的状态操作是有一个专有的设计方式：层次化状态机（HFSM）。

## 开始重构


1. **定义状态**


分别定义订单中的每一个状态的枚举，并设置归属的上层状态
```java

/**
 * 订单状态枚举
 *
 * @author zhoushengtao
 * @since 2016/12/18.
 */
public enum OrderStateEnum {
    SUBSCRIBE(0, new OrderSubscribeAction(), OrderSectionEnum.SUBSCRIBE),
    CONFIRM(10, new OrderConfirmAction(), OrderSectionEnum.AUDIT),
    UPLOAD_PAYINFO(15, new OrderUploadPayinfoAction(), OrderSectionEnum.REBATE);
//    PAYINFO_PASS(20),
//    ORDER_UPLOAD_COMMISSION(25),
//    REBATE(30),
//    FAIL_REBATE(-100),
//    ORDER_NOT_PASS(-200),
//    PAYINFO_NOT_PASS(-300),
//    ORDER_DELETE(-400),
//    ORDER_CANCEL(-500);

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

}
```

为了方便区分，我们又叫**上层**为一个“**环节**”
```java

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
```

2. **将订单类型切换制定统一入口**
```java


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
```

3. **抽象每一个枚举的操作**
```java

/**
 * 确认订单状态
 *
 * @author zhoushengtao
 * @since 2016/12/18.
 */
public class OrderConfirmAction extends IOrderAction {
    @Override
    public void modify(OrderStateContext context) {
    }

    @Override
    public void cancel(OrderStateContext context) {
    }

    @Override
    public void auditPass(OrderStateContext context) {
        context.getOrderSection().setState(OrderStateEnum.CONFIRM);
    }

    @Override
    public void auditFailure(OrderStateContext context) {
    }
}
```

4. **测试操作**
```java
    public static void main(String[] args) {
        // 初始化需要操作修改的bean
        Order order = new Order();
        // 尝试各种修改
        OrderStateMachine.getInstance(order)
                .auditPass();
    }
```

此代码demo是从项目里面剥离出来的，没有什么实际的逻辑，韬哥我放到github上:https://github.com/stchou/StateMachine
共大家参考学习，写的不对的地方请多包涵。


## 状态机 - 总结 

### 设计要点
1. 要为状态定义入口 / 出口
2. 要详细设计每一个状态，列出层次化状态结构，利于分工理解
3. 每一个子状态操作逻辑少，不易出错
4. 状态枚举定义，将枚举值、操作、跳转限制合为一体，清晰明了。

### 缺点
1. 大规模支持较差
2. 高并发容易出错（需要加入操作队列）
3. 没有书写状态切换log（后期排查很重要）
4. 没有动态化设置逻辑，游戏设计中往往是让游戏策划可以动态的修改状态的切换逻辑。

有问题，或者想吐槽的，请加韬哥微信：

/* 
* @author zhoushengtao(周圣韬) 
* @since 2016年12月11日 凌晨1:39:20 
* @weixin stchou_zst 
* @blog http://blog.csdn.net/yzzst 
/ 
![这里写图片描述](http://img.blog.csdn.net/20161218184425552?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQveXp6c3Q=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)
