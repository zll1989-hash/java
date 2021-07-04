
/**
 * 订单调用
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    /**
     * 获取订单信息
     * @param response
     */
    @GetMapping("/getOrder")
    public void getOrder(String userName, HttpServletResponse response) {
        OrderService orderService = SpringBeanFactory.getBean(OrderService.class);
        try {
            PrintWriter pw = response.getWriter();
            pw.println(orderService.getOrder(userName, "order00001"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
