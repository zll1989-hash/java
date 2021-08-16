package com.sankuai.inf.leaf.server.Algo;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * @Author Lucien
 * @create 2021/6/22
 */
public class TestArray {

    public class ListNode {
     int val;
     ListNode next;
     ListNode() {}
     ListNode(int val) { this.val = val; }
     ListNode(int val, ListNode next) { this.val = val; this.next = next; }
  }



    public int[] twoSum(int[] nums, int target) {
        int len = nums.length;
        HashMap<Integer, Integer> hashMap = new HashMap<>(len - 1);
        hashMap.put(nums[0], 0);
        for (int i = 1; i < len; i++) {
            if(hashMap.containsKey(target-nums[i])){
                return new int[]{i,hashMap.get(target-nums[i])};
            }
            hashMap.put(nums[i],i);
        }
        throw new IllegalArgumentException("no two sum solution");
    }

    public ListNode swapPairs(ListNode head) {
        // 已有的链表加一个头部 head node
        ListNode resultHead = new ListNode();
        resultHead.next = head;

        // curNode 遍历链表时用
        ListNode curNode = resultHead;

        // 开始遍历链表
        while (curNode != null && curNode.next != null && curNode.next.next != null) {
            ListNode f = curNode;
            ListNode s = curNode.next;
            ListNode t = s.next;

            // 两两交换链表结点
            f.next = t;
            s.next = t.next;
            t.next = s;
            // 标杆位后移2位
            curNode = curNode.next.next;
        }
        return resultHead.next;
    }

    // 合并数据
    public ListNode mergeTwoLists(ListNode l1, ListNode l2) {
        ListNode prehead = new ListNode(-1);
        ListNode prev = prehead;
        while (l1 != null && l2 != null) {
            if (l1.val <= l2.val) {
                prev.next = l1;
                l1 = l1.next;
            } else {
                prev.next = l2;
                l2 = l2.next;
            }
            prev = prev.next;
        }

        // 合并后 l1 和 l2 最多只有一个还未被合并完，我们直接将链表末尾指向未合并完的链表即可
        prev.next = l1 == null ? l2 : l1;
        return prehead.next;
    }

    public static String getLocalIp() {
        try {
            Enumeration<NetworkInterface> faces = NetworkInterface.getNetworkInterfaces();
            while (faces.hasMoreElements()) {
                NetworkInterface face = faces.nextElement();
                if (face.isLoopback() || face.isVirtual() || !face.isUp()) {
                    continue;
                }
                Enumeration<InetAddress> address = face.getInetAddresses();
                while (address.hasMoreElements()) {
                    InetAddress addr = address.nextElement();
                    if (!addr.isLoopbackAddress() && addr.isSiteLocalAddress() && !addr.isAnyLocalAddress()) {
                        return addr.getHostAddress();
                    }
                }
            }
        }
        catch (Exception e) {
            // ignore
        }

        return "127.0.0.1";
    }

    public static void main(String[] args) throws ParseException {

        //  detailIds = Arrays.asList(orderDetailIds.split(",")).stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toSet());

        String ip   =  getLocalIp();
        System.out.println("===ip==="+ip);

        Long id = null ;
        if (StringUtils.isEmpty(id)){
            System.out.println(" 输出 id ");
        }

        Set<Long> detailIds = new HashSet<>();
        String ids = "12345,6789";
        detailIds = Arrays.asList(ids.split(",")).stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toSet());

        System.out.println(detailIds.size());

        StringBuilder aStr = new StringBuilder("Hello World!");
        System.out.println(aStr.toString());

        List<Integer> integers  = new ArrayList<>();
        integers.add(1);
        //integers.add(2);

        List<Integer> skuTotalNumArray = new ArrayList<>();
        skuTotalNumArray.add(1);
        skuTotalNumArray.add(2);

        boolean dd = determineDifferentLists(skuTotalNumArray,integers);

        System.out.println(dd);

        Integer one =1;
        int b = 1;
        Long c = 1L;
        Long d = 1L;
        if(d.equals(c)){
            System.out.println("peter");
        }

//        //String BASESQL_QUERY = "cloud_wms_stock  LEFT JOIN cloud_wms_owner_stock on  cloud_wms_owner_stock.entity_id = cloud_wms_stock.entity_id  AND cloud_wms_owner_stock.tenant_id = cloud_wms_stock.tenant_id   AND cloud_wms_owner_stock.is_usable = 1    JOIN sku ON sku.sku_id = cloud_wms_stock.entity_id   AND sku.is_enabled = 1   %s LEFT JOIN (SELECT cloud_wms_sale_stock_lock.entity_id AS sku_id,cloud_wms_sale_stock_lock.warehouse_id AS warehouse_id,sum(abs( cloud_wms_sale_stock_lock.lock_number )) AS lock_number,sum(abs( cloud_wms_sale_stock_lock.out_of_stock_num )) AS out_number,cloud_wms_sale_stock_lock.tenant_id AS tenant_id,cloud_wms_sale_stock_lock.is_usable AS is_usable FROMcloud_wms_sale_stock_lock WHEREis_usable = 1 AND tenant_id = ?AND cloud_wms_sale_stock_lock.owner_id = ? GROUP BYcloud_wms_sale_stock_lock.entity_id,cloud_wms_sale_stock_lock.warehouse_id ) AS stock_lock ON stock_lock.sku_id = cloud_wms_stock.entity_id AND stock_lock.warehouse_id = cloud_wms_stock.warehouse_id AND stock_lock.tenant_id = cloud_wms_stock.tenant_id %s   JOIN product ON product.product_id = sku.product_id   AND sku.tenant_id = product.tenant_id   AND product.is_usable = 1   %s  LEFT JOIN warehouse_bin on warehouse_bin.warehouse_bin_id = cloud_wms_stock.warehouse_bin_id  AND warehouse_bin.tenant_id = cloud_wms_stock.tenant_id   AND warehouse_bin.is_usable = 1   WHERE  cloud_wms_stock.tenant_id = ?   AND cloud_wms_owner_stock.owner_id = ?   AND cloud_wms_stock.number > 0   AND cloud_wms_stock.warehouse_id = ?   AND cloud_wms_stock.warehouse_area_id = ?   AND cloud_wms_stock.is_usable = 1";
//
//        String BASESQL_QUERY = " cloud_wms_stock " +
//                " LEFT JOIN cloud_wms_owner_stock on " +
//                " cloud_wms_owner_stock.entity_id = cloud_wms_stock.entity_id " +
//                " AND cloud_wms_owner_stock.tenant_id = cloud_wms_stock.tenant_id  " +
//                " AND cloud_wms_owner_stock.is_usable = 1  " +
//                "  JOIN sku ON sku.sku_id = cloud_wms_stock.entity_id  " +
//                " AND sku.is_enabled = 1  " +
//                " %s " +
//                "  JOIN product ON product.product_id = sku.product_id  " +
//                " AND sku.tenant_id = product.tenant_id  " +
//                " AND product.is_usable = 1  " +
//                " %s " +
//                " LEFT JOIN warehouse_bin on warehouse_bin.warehouse_bin_id = cloud_wms_stock.warehouse_bin_id " +
//                " AND warehouse_bin.tenant_id = cloud_wms_stock.tenant_id  " +
//                " AND warehouse_bin.is_usable = 1  " +
//                " WHERE " +
//                " cloud_wms_stock.tenant_id = ?  " +
//                " AND cloud_wms_owner_stock.owner_id = ?  " +
//                " AND cloud_wms_stock.number > 0  " +
//                " AND cloud_wms_stock.warehouse_id = ?  " +
//                " AND cloud_wms_stock.warehouse_area_id = ?  " +
//                " AND cloud_wms_stock.is_usable = 1 ";
//
//        StringBuilder sql = new StringBuilder(String.format(BASESQL_QUERY, String.format(" and ( instr( sku.%s, '%s' )> 0 ) ", "sku_no", ""), ""));
//
//        System.out.println(sql.toString());
//
//        Integer a = 8;
//        String b = "8";
//
//        if(a.toString().equals(b)){
//            System.out.println("lllllll");
//        }else{
//            System.out.println("2222222");
//        }

    }


    public int maxArea(int[] height) {
        int i = 0;
        int j = height.length-1;
        int ans = 0;
        while (i < j) {
            ans = max(ans, min(height[i], height[j]) * (j - i));
            if (height[i] == height[j]){
                i++;
                j--;
            }
            else if (height[i] < height[j]) {
                i++;
            }else {
                j--;
            }
        }
        return ans;
    }


    public static Boolean determineDifferentLists(Collection<Integer> sourceList, Collection<Integer> targetList) {
        if (CollectionUtils.isEmpty(sourceList) || CollectionUtils.isEmpty(targetList)) {
            return false;
        }
        List<String> sourceListStrList = sourceList.stream().map(String::valueOf).collect(Collectors.toList());
        List<String> targetListStrList = targetList.stream().map(String::valueOf).collect(Collectors.toList());
        sourceListStrList.sort(Comparator.comparing(String::hashCode));
        targetListStrList.sort(Comparator.comparing(String::hashCode));
        return sourceListStrList.toString().equals(targetListStrList.toString());
    }


}
