package com.yangyuan.wififileshareNio.nioTransfer;

import android.os.Handler;

import com.yangyuan.wififileshareNio.bean.ServiceFileInfo;

import java.util.ArrayList;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * Created by yangy on 2018/3/24.
 */

public class NioGetFileClient {
    public static final int PORT = 60666;

    public void connect(String ip, final ArrayList<ServiceFileInfo> serviceFileInfos, final Handler uiHandler) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<Channel>() {

                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline().addLast(new ObjectEncoder());
                            ch.pipeline()
                                    .addLast(
                                            new ObjectDecoder(
                                                    ClassResolvers
                                                            .weakCachingConcurrentResolver(null)));
                            ch.pipeline()
                                    .addLast(
                                            new NioGetFileListHandler(serviceFileInfos, uiHandler));
                        }
                    });
            ChannelFuture f = b.connect(ip, PORT).sync();
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }


}