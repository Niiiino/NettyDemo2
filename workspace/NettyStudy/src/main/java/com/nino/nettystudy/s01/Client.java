package com.nino.nettystudy.s01;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.channel.group.ChannelGroupFutureListener;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.ServerSocket;

/**
 * @author Nino
 * @date 2020-06-10 11:31
 */
public class Client {
    public static void main(String[] args) throws Exception {

        //事件处理线程池
        EventLoopGroup group = new NioEventLoopGroup(1);

        Bootstrap b = new Bootstrap();

        try {
            ChannelFuture f = b.group( group )
                    //指定用哪种IO模型
                    .channel( NioSocketChannel.class )
                    //当这个channel有事件的时候交给下面这个处理器处理
                    .handler( new ClientChannelInitializer() )
                    .connect( "localhost", 8888 );

            f.addListener( new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (!future.isSuccess()){
                        System.out.println("not connected!");
                    }else {
                        System.out.println("connected!");
                    }
                }
        });
            //sync方法用于阻塞 必须等上面出结果（连上或者没连上）为止
            f.sync();
            System.out.println("11111");

        }finally {
            group.shutdownGracefully();
        }
    }
}

class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        System.out.println(ch);
    }
}
