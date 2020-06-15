package com.nino.nettystudy.s02;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.ReferenceCountUtil;

/**
 * @author Nino
 * @date 2020-06-15 10:29
 */
public class Clients02 {

    private Channel channel = null;

    public void connect() {

        //�¼������̳߳�
        EventLoopGroup group = new NioEventLoopGroup( 1 );

        Bootstrap b = new Bootstrap();

        try {
            ChannelFuture f = b.group( group )
                    //ָ��������IOģ��
                    .channel( NioSocketChannel.class )
                    //�����channel���¼���ʱ�򽻸������������������
                    .handler( new ClientChannelInitializers02() )
                    .connect( "localhost", 8888 );

            f.addListener( new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (!future.isSuccess()) {
                        System.out.println( "not connected!" );
                    } else {
                        System.out.println( "connected!" );
                        channel = future.channel();
                    }
                }
            } );
            //sync������������ ������������������ϻ���û���ϣ�Ϊֹ
            f.sync();
            System.out.println( "1111" );
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        Clients02 c = new Clients02();
        c.connect();
    }

    public void send(String msg){
        ByteBuf buf = Unpooled.copiedBuffer( msg.getBytes() );
        channel.writeAndFlush( buf );
    }

    public void closeConnect() {
        this.send( "byebye" );
        channel.close();
        System.out.println("�ͻ����˳�����");
    }
}
class ClientChannelInitializers02 extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pl = ch.pipeline();
        pl.addLast( new com.nino.nettystudy.s02.ClientHandler() );
    }
}

class ClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = null;
        try {
            buf = (ByteBuf) msg;
            //�����м����ֽ�
            byte[] bytes = new byte[buf.readableBytes()];
            buf.getBytes( buf.readerIndex(), bytes );
            //תΪString���Ͷ�����
            String msgAccepted = new String(bytes);
            ClientFrame.INSTANCE.updateText(msgAccepted);
            //System.out.println( new String( bytes ) );
            //System.out.println(buf);
            //buf.refCnt()��ʾbuf�����м�������,����ͷ��˾�Ӧ��û�������ˣ����Ӧ����0
            //System.out.println(buf.refCnt());
        } finally {
            if (buf != null) {
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
