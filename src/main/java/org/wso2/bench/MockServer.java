package org.wso2.bench;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.concurrent.DefaultExecutorServiceFactory;
import io.netty.util.concurrent.ExecutorServiceFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class MockServer {

    static final boolean SSL = System.getProperty("ssl") != null;

    static int queues = 1;
    static final MockServerThread[] handlers = new MockServerThread[queues];

    public static void main(String[] args) throws Exception {

        if(args.length < 1) {
            System.out.println("Usage: java -jar netty5-mock-backend-1.0-jar-with-dependencies.jar /path/to/properties/file /path/to/response/file");
            System.exit(0);
        }

        Properties props = null;
        try {
            props = getProperties(args[0]);
        } catch (Exception e) {
            System.out.println("Error " + e.getMessage());
            System.exit(0);
        }

        int PORT = 8080, HTTPS_PORT = 8443, BOSS = 1, WORKER = 4, DELAY = 0, APPTHREADS = 2;
        String RESP_CONTENT_TYPE = "text/plain; charset=UTF-8";
        if(props!=null) {
            PORT = Integer.parseInt(props.getProperty("http_port"));
            HTTPS_PORT = Integer.parseInt(props.getProperty("https_port"));
            WORKER = Integer.parseInt(props.getProperty("worker_count"));
            DELAY = Integer.parseInt(props.getProperty("response_delay"));
            BOSS = Integer.parseInt(props.getProperty("boss_count"));
            APPTHREADS = Integer.parseInt(props.getProperty("app_threads"));
            RESP_CONTENT_TYPE = props.getProperty("response_content_type");
        }

        for (int i=0; i<queues; i++) {
            MockServerThread handler = new MockServerThread();
            handlers[i] = handler;
            handler.start();
        }

        // Configure SSL.
        final SslContext sslCtx;
        if (SSL) {
            PORT = HTTPS_PORT;
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslCtx = SslContext.newServerContext(ssc.certificate(), ssc.privateKey());
        } else {
            sslCtx = null;
        }

        ExecutorServiceFactory workerExecutorServiceFactory = new DefaultExecutorServiceFactory("sample-workers");
        workerExecutorServiceFactory.newExecutorService(WORKER);

        ExecutorServiceFactory bossExecutorServiceFactory = new DefaultExecutorServiceFactory("sample-bosses");
        bossExecutorServiceFactory.newExecutorService(BOSS);

        // Configure the server.
        EventLoopGroup bossGroup = new NioEventLoopGroup(BOSS, bossExecutorServiceFactory);
        EventLoopGroup workerGroup = new NioEventLoopGroup(WORKER, workerExecutorServiceFactory);
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    //.handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new MockServerInitializer(sslCtx, DELAY, RESP_CONTENT_TYPE, handlers));

            Channel ch = b.bind(PORT).sync().channel();

            System.err.println("Open your web browser and navigate to " +
                    (SSL? "https" : "http") + "://127.0.0.1:" + PORT + '/');

            ch.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }


    private static Properties getProperties(String propPath) throws Exception {
        InputStream inputStream = new FileInputStream(propPath);
        Properties props = new Properties();
        props.load(inputStream);
        return props;
    }

}
