package com.nino.nettystudy.s02;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author Nino
 * @date 2020-06-12 15:39
 */
public class ClientFrame extends Frame {

    public static final ClientFrame INSTANCE = new ClientFrame();

    TextArea ta = new TextArea();
    TextField tf = new TextField();

    Clients02 c = null;

    public ClientFrame(){
        this.setSize( 600,400 );
        this.setLocation( 100,20 );
        this.add( ta,BorderLayout.CENTER );
        this.add( tf,BorderLayout.SOUTH );

        tf.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                c.send( tf.getText() );
                //ta.setText( ta.getText()+tf.getText() );
                tf.setText( "" );
            }
        } );
        this.addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                c.closeConnect();
                System.exit( 0 );
            }
        } );

    }

    private void connectToServer() {
        c = new Clients02();
        c.connect();
    }

    public static void main(String[] args) {
        ClientFrame frame = ClientFrame.INSTANCE;
        frame.setVisible( true );
        frame.connectToServer();
    }

    public void updateText(String msgAccepted) {
        /* 在java中存在一些转义字符,比如"\n"为换行符,但是也有一些JDK自带的一些操作符
        ??? 比如 : System.getProperty("line.separator")
        ??? 这也是换行符,功能和"\n"是一致的,但是此种写法屏蔽了 Windows和Linux的区别 ，更保险一些.*/
        ta.setText( ta.getText() + System.getProperty( "line.separator" ) + msgAccepted );
    }
}
