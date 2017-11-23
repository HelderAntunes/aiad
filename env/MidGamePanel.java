package env;

import jason.asSyntax.*;
import jason.environment.*;
import java.util.logging.*;
import java.io.*;
import java.util.*;
import java.lang.String;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

class MidGamePanel extends JPanel {
	private JFrame frame;
	private WerewolfsGameEnv env;
	private ArrayList<String> werewolfs = new ArrayList<String>();
	private ArrayList<String> villagers = new ArrayList<String>();
	private ArrayList<String> diviners = new ArrayList<String>();
	private ArrayList<String> doctors = new ArrayList<String>();
	private JTextArea infoTest;
	
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
		
		infoTest = new JTextArea(100, 100);
		infoTest.setText("sfsdfsdfsdf");
		size = infoTest.getPreferredSize();
		infoTest.setBounds(350, 200, size.width, size.height);
		infoTest.setEditable(false); 
		this.add(infoTest);
	}
	
	public void setInfoTestLbl(String text) {
		infoTest.append("\n" + text);	
	}

	@Override 
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
	}

}