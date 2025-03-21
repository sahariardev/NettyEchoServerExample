package org.sahariardev;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        int port = 8080;

        try {
            Bootstrap clientBootstrap = new Bootstrap();

            clientBootstrap
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress(port))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ClientHandler());
                        }
                    });


            while (true) {
                Scanner scanner = new Scanner(System.in);
                String msg = scanner.nextLine();

                if (msg.equals("bye")) {
                    break;
                }

                ChannelFuture future = clientBootstrap.connect().sync();
                Channel channel = future.channel();


                ByteBuf byteBuf = Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8);
                channel.writeAndFlush(byteBuf).sync();

                channel.closeFuture().sync();
            }

        } finally {
            group.shutdownGracefully().sync();
        }
    }
}
