package com.nino.nettystudy.s02;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Nino
 * @date 2020-06-12 15:39
 */
public class ClientFrame extends Frame {

    TextArea ta = new TextArea();
    TextField tf = new TextField();

    public ClientFrame(){
        this.setSize( 600,400 );
        this.setLocation( 100,20 );
        this.add( ta,BorderLayout.CENTER );
        this.add( tf,BorderLayout.SOUTH );
        this.setVisible( true );
        tf.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ta.setText( ta.getText()+tf.getText() );
                tf.setText( "" );
            }
        } );
    }

    public static void main(String[] args) {
        new ClientFrame();
    }

}
