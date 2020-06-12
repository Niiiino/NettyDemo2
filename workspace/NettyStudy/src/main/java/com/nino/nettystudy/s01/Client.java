package com.nino.nettystudy.s01;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.channel.group.ChannelGroupFutureListener;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.ReferenceCountUtil;

import java.net.ServerSocket;
import java.nio.ByteBuffer;

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
            f.channel().closeFuture().sync();
        }finally {
            group.shutdownGracefully();
        }
    }
}

class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pl = ch.pipeline();
        pl.addLast( new ClientChildHandler() );
    }
}

class ClientChildHandler extends ChannelInboundHandlerAdapter{

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = null;
        try {
            buf = (ByteBuf) msg;
            //�����м����ֽ�
            byte[] bytes = new byte[buf.readableBytes()];
            buf.getBytes( buf.readerIndex(), bytes );
            //תΪString���Ͷ�����
            System.out.println(new String( bytes ));
            //System.out.println(buf);
            //buf.refCnt()��ʾbuf�����м�������,����ͷ��˾�Ӧ��û�������ˣ����Ӧ����0
            //System.out.println(buf.refCnt());
        } finally {
            if (buf != null){
                ReferenceCountUtil.release( buf );
                //System.out.println(buf.refCnt());
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //channel��һ�����Ͽ��ã�д��һ���ַ��� ����Netty��ByteBuf��װ��NIO��ByteBuffer
        // ByteBuf��ֱ�ӷ��ʵ�OS��Ҳ����Direct Memory  ����Ч�ʺܸ�
        ByteBuf buf = Unpooled.copiedBuffer("hello".getBytes());
        //��Ϊ��ֱ�ӷ���OS�����Բ�������������Ҫ�Լ��ͷţ�Ҳ����Flush�Զ��ͷ�
        //�����NIO��ByteBuffer���ʵľ���Java������������������������Լ�����
        ctx.writeAndFlush( buf );
    }
}