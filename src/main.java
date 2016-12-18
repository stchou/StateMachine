import com.bean.order.Order;
import com.evenstate.order.OrderStateMachine;

/**
 * 订单状态测试
 */
public class main {

    public static void main(String[] args) {
        // 初始化需要操作修改的bean
        Order order = new Order();
        // 尝试各种修改
        OrderStateMachine.getInstance(order)
                .auditPass();
    }
}
