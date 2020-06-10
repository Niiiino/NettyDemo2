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

        //�¼������̳߳�
        EventLoopGroup group = new NioEventLoopGroup(1);

        Bootstrap b = new Bootstrap();

        try {
            ChannelFuture f = b.group( group )
                    //ָ��������IOģ��
                    .channel( NioSocketChannel.class )
                    //�����channel���¼���ʱ�򽻸������������������
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
            //sync������������ ������������������ϻ���û���ϣ�Ϊֹ
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
