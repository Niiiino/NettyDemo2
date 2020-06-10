package com.nino.nettystudy.s01;

import java.net.InetSocketAddress;
import java.net.ServerSocket;

/**
 * @author Nino
 * @date 2020-06-10 15:34
 */
public class Server {
    public static void main(String[] args) throws Exception {

        ServerSocket ss =new ServerSocket();
        ss.bind( new InetSocketAddress( 8888 ) );

        ss.accept();

        System.out.println("a client accept!");

    }
}
