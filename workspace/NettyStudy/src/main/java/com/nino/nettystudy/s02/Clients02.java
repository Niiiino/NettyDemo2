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

        //事件处理线程池
        EventLoopGroup group = new NioEventLoopGroup( 1 );

        Bootstrap b = new Bootstrap();

        try {
            ChannelFuture f = b.group( group )
                    //指定用哪种IO模型
                    .channel( NioSocketChannel.class )
                    //当这个channel有事件的时候交给下面这个处理器处理
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
            //sync方法用于阻塞 必须等上面出结果（连上或者没连上）为止
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
        System.out.println("客户端退出连接");
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
            //计算有几个字节
            byte[] bytes = new byte[buf.readableBytes()];
            buf.getBytes( buf.readerIndex(), bytes );
            //转为String类型读出来
            String msgAccepted = new String(bytes);
            ClientFrame.INSTANCE.updateText(msgAccepted);
            //System.out.println( new String( bytes ) );
            //System.out.println(buf);
            //buf.refCnt()表示buf身上有几个引用,如果释放了就应该没有引用了，结果应该是0
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
        //channel第一次连上可用，写出一个字符串 这里Netty的ByteBuf封装了NIO的ByteBuffer
        // ByteBuf是直接访问的OS，也就是Direct Memory  所以效率很高
        ByteBuf buf = Unpooled.copiedBuffer("hello".getBytes());
        //因为是直接访问OS，所以产生的垃圾必须要自己释放，也就是Flush自动释放
        //如果是NIO的ByteBuffer访问的就是Java虚拟机，他会有垃圾清理器自己回收
        ctx.writeAndFlush( buf );
    }
}
