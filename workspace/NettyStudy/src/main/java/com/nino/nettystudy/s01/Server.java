package com.nino.nettystudy.s01;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ReferenceCounted;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.net.InetSocketAddress;
import java.net.ServerSocket;

/**
 * @author Nino
 * @date 2020-06-10 15:34
 */
public class Server {

    public static ChannelGroup channels= new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public static void main(String[] args) throws Exception {

        /*ServerSocket ss =new ServerSocket();
        ss.bind( new InetSocketAddress( 8888 ) );

        ss.accept();

        System.out.println("a client accept!");*/

        //bossGruopֻ����ͻ��˵�����
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        //��������Socket�����������ĵ��¼�����workGruop
        EventLoopGroup workGroup = new NioEventLoopGroup(2);

        try {
            ServerBootstrap sb = new ServerBootstrap();
            ChannelFuture f = sb.group( bossGroup, workGroup )
                    .channel( NioServerSocketChannel.class )
                    .childHandler( new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pl = ch.pipeline();
                            pl.addLast( new ServerChildHandler() );
                        }
                    } )
                    .bind( 8888 )
                    //�������sync����������ֻ�гɹ�bind��8888�˿ڲŻ����ִ��
                    //��Ϊ���첽�ģ�����ֻ�ܼ�sync������֤ͬ��
                    .sync();

            System.out.println("sever started!");

            //closeFuture�������ֻ����close����ִ�е�ʱ��Żᱻ���ã�����close��������ֵ��channel future��
            //���û���˵��������������ô���closeFuture��������Զ�ȴ������ֱ�����˵����ټ���ִ��main����
            f.channel().closeFuture().sync();
        } finally {
            workGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }

    }
}

class ServerChildHandler extends ChannelInboundHandlerAdapter{

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Server.channels.add( ctx.channel() );
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf buf = null;

            buf = (ByteBuf) msg;
            //�����м����ֽ�
            byte[] bytes = new byte[buf.readableBytes()];
            buf.getBytes( buf.readerIndex(), bytes );
            //תΪString���Ͷ�����
            System.out.println( new String( bytes ) );
            //���յ���Ϣ�Ժ��ٰѷ���������Ϣд��ȥ
            Server.channels.writeAndFlush( msg );
            //System.out.println(buf);
            //buf.refCnt()��ʾbuf�����м�������,����ͷ��˾�Ӧ��û�������ˣ����Ӧ����0
            //System.out.println(buf.refCnt());

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
