
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class EchoServer {

    private int port;

    public EchoServer(int port){
        this.port = port;
    }

    /**
     * 启动流程
     */
    public void run() throws InterruptedException {

        //配置服务端线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try{
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workGroup)

                    .channel(NioServerSocketChannel.class)

                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new EchoServerHandler());
                        }
                    });

            System.out.println("Echo 服务器启动ing");

            //绑定端口，同步等待成功
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();

            //等待服务端监听端口关闭
            channelFuture.channel().closeFuture().sync();
        }finally {

            //优雅退出，释放线程池
            workGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }





    }


    public static void main(String [] args) throws InterruptedException {
        int port = 8080;
        if(args.length > 0){
            port = Integer.parseInt(args[0]);
        }

        new EchoServer(port).run();

    }

}
