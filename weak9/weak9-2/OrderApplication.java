
@SpringBootApplication

//开启服务发现
@EnableDiscoveryClient

//开启Feign支持
@EnableFeignClients

public class OrderApplication {

    public static void main(String [] args){

        SpringApplication.run(OrderApplication.class,args);
    }



    @Bean
    @LoadBalanced
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }


}
