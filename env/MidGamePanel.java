package env;

import jason.asSyntax.*;
import jason.environment.*;
import java.util.logging.*;
import java.io.*;
import java.util.Scanner;
import java.lang.String;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

class MidGamePanel extends JPanel {
	private JFrame frame;
	private WerewolfsGameEnv env;

	public MidGamePanel(WerewolfsGameEnv env) {
		this.env = env;
		this.frame = env.getFrame();
		this.setLayout(null);
		JButton startBtn = new JButton("MID");
        Dimension size = startBtn.getPreferredSize();
        startBtn.setBounds(150, 15,size.width, size.height);
        startBtn.addActionListener(new ActionListener() { 
		  public void actionPerformed(ActionEvent e) { 
		    frame.getContentPane().removeAll();
		    frame.getContentPane().invalidate();
			JPanel newPanel = new InitGamePanel(env);
			env.setCurrPanel(newPanel);
			frame.getContentPane().add(newPanel);
			frame.getContentPane().revalidate();
		  } 
		});
        this.add(startBtn);
	}

	@Override 
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		}

}