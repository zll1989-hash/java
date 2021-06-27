
/**
 * 订单mapper接口
 */
@Mapper
public interface OrderMapper {
    //新增
    @Insert("insert into t_order(price,user_id,status) values(#{price},#{userId},#{status})")
    public int inserOrder(Order order);
    //根据id查询多个订单
    @Select({"<script>"+
                    "select "+
                    " * "+
                    " from t_order t"+
                    " where t.order_id in "+
                    "<foreach collection='orderIds' item='id' open='(' separator=',' close=')'>" +
                    "#{id}" +
                    "</foreach>" +
              "</script>"})
    public List<Map> selectListByOrderIds(@Param("orderIds") List<Long> orderIds);
}
