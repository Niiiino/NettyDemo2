package com.nino.nettystudy.s02;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * @author Nino
 * @date 2020-06-10 15:34
 */
public class Servers02 {

    public static ChannelGroup channels= new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public static void main(String[] args) throws Exception {

        /*ServerSocket ss =new ServerSocket();
        ss.bind( new InetSocketAddress( 8888 ) );

        ss.accept();

        System.out.println("a client accept!");*/

        //bossGruop只负责客户端的连接
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        //连上来的Socket上面所产生的的事件交给workGruop
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
                    //这里这个sync阻塞方法是只有成功bind上8888端口才会继续执行
                    //因为是异步的，所以只能加sync让他保证同步
                    .sync();

            System.out.println("sever started!");

            //closeFuture这个方法只有在close方法执行的时候才会被调用（调用close方法返回值是channel future）
            //如果没有人调用这个方法，那么这个closeFuture方法会永远等待在这里，直到有人调用再继续执行main方法
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
        Servers02.channels.add( ctx.channel() );
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
           ByteBuf buf = null;

           buf = (ByteBuf) msg;
            //计算有几个字节
            byte[] bytes = new byte[buf.readableBytes()];
            buf.getBytes( buf.readerIndex(), bytes );
            String s = new String( bytes );
            if (s.equals( "byebye" )){
                System.out.println("客户端要求退出");
                Servers02.channels.remove( ctx.channel() );
                ctx.close();
            }else{
                //接收到消息以后再把发过来的消息写回去
                System.out.println(s);
                Servers02.channels.writeAndFlush( msg );
            }
            //buf.refCnt()表示buf身上有几个引用,如果释放了就应该没有引用了，结果应该是0
            //System.out.println(buf.refCnt());

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        //删除出现异常的客户端channel，并关闭连接
        Servers02.channels.remove( ctx.channel() );
        ctx.close();
    }
}
