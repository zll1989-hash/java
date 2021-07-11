

/**
 * 基于zk实现分布式锁
 */
public class ZkLock {

    private ZooKeeper zooKeeper;

    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    private ZkLock() {
        try {
            zooKeeper = new ZooKeeper("192.1.1.101:2181,192.1.1.102:2181,192.1.1.103:2181", 5000, new ZkWatcher());
            System.out.println(zooKeeper.getState());
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("与zk建立连接=====>"+zooKeeper.getState());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ZkLock getInstance() {
        return Singleton.getInstance();
    }

    private class ZkWatcher implements Watcher {
        @Override
        public void process(WatchedEvent event) {
            System.out.println("接收到监听事件=====》"+event);
            if (Event.KeeperState.SyncConnected == event.getState()) {
                countDownLatch.countDown();
            }
        }
    }


    public void lock(Integer id) {
        String path = "/xdclass-product-lock-" + id;
        //创建临时节点,如果创建成功的话，就表示获取锁，如果失败，则不断尝试
        try {
            zooKeeper.create(path,"".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            System.out.println("成功获取到锁");
        } catch (Exception e) {
            while (true) {
                try {
                    Thread.sleep(500L);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                try {
                    zooKeeper.create(path,"".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                } catch (Exception e1) {
                    continue;
                }
                break;
            }
        }
    }

    /**
     * 释放锁，直接删除zk节点
     * @param id
     */
    public void unLock(Integer id) {
        String path = "/xdclass-product-lock-" + id;
        try {
            zooKeeper.delete(path,-1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }


    private static class Singleton {

        private static ZkLock instance;
        static {
            instance = new ZkLock();
        }

        private static ZkLock getInstance() {
            return instance;
        }

    }


}