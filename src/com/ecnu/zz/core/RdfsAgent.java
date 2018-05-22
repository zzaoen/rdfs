package com.ecnu.zz.core;

import com.ecnu.zz.utils.AgentLogUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author : Bruce Zhao
 * @email  : zhzh402@163.com
 * @date   : 2018/3/26 12:20
 * @desc   : 使用JBoss Marshalling的netty server
 */
public class RdfsAgent {

    public static void main(String[] args) throws Exception {
        int port = 8080;
        Storage.add("192.168.0.100");
        Storage.add("192.168.100.100");
        AgentLogUtil.rebuildAgentDirTree(); //重建agent维护的目录树结构


        new RdfsAgent().bind(8080);
    }

    public void bind(int port) throws Exception{
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try{
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ProtobufDecoderHandler());

            ChannelFuture future = bootstrap.bind(port).sync();

            future.channel().closeFuture().sync();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private class ProtobufDecoderHandler extends ChannelInitializer<SocketChannel>{

        @Override
        protected void initChannel(SocketChannel socketChannel) throws Exception {
            socketChannel.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingDecoder()); //in
            socketChannel.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingEncoder()); //out
            socketChannel.pipeline().addLast(new AgentHandler()); //in
        }
    }

}
