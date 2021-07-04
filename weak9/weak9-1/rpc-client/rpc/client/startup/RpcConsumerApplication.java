

/**
 * 客户端启动入口
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.itcast"})
public class RpcConsumerApplication implements ApplicationRunner {

    @Autowired
    private RpcClientRunner rpcClientRunner;

    public static void main(String[] args) {
        SpringApplication.run(RpcConsumerApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        rpcClientRunner.run();
    }
}

