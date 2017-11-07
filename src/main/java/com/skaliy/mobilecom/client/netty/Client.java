package com.skaliy.mobilecom.client.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Client implements Runnable {
    private final String host;
    private final int port;
    private BufferedReader reader;
    private Channel channel;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void run() {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ClientInitializer());
            channel = bootstrap.connect(host, port).sync().channel();
            reader = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                channel.write(reader.readLine() + "\r\n");
            }
        } catch (InterruptedException | IOException ignored) {
        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }

    public void setQuery(String sql) {
        channel.write(sql);
    }
}