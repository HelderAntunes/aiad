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

class InitGamePanel extends JPanel {

	private JFrame frame;
	private BufferedImage image;
	private WerewolfsGameEnv env;
	private JLabel infoTestLbl;
	
	private JLabel werewolfTitle;
	private JLabel villagerTitle;
	private JLabel divinerTitle;
	private JLabel doctorTitle;

	private JComboBox comboBoxWerewolf;

	public InitGamePanel(WerewolfsGameEnv env) {

		try {                
          image = ImageIO.read(new File("./IMG_0062.jpg"));
       	} catch (IOException ex) {

       	}
       	this.env = env;
		this.frame = env.getFrame();
		this.setLayout(null);
		JButton startBtn = new JButton("START");
        Dimension size = startBtn.getPreferredSize();
        startBtn.setBounds(400, 300,size.width, size.height);
        startBtn.addActionListener(new ActionListener() { 
		  public void actionPerformed(ActionEvent e) { 
		    frame.getContentPane().removeAll();
		    frame.getContentPane().invalidate();
			JPanel newPanel = new MidGamePanel(env);
			env.setCurrPanel(newPanel);
			frame.getContentPane().add(newPanel);
			frame.getContentPane().revalidate();
		  } 
		});
        this.add(startBtn);

        infoTestLbl = new JLabel("test label");
        //infoTestLbl.setText("test label");
        size = infoTestLbl.getPreferredSize();
        infoTestLbl.setBounds(390, 295, size.width, size.height);
        this.add(infoTestLbl);
		
		int w = env.WIDTH_FRAME;
		werewolfTitle = new JLabel("Werewolfs");
		werewolfTitle.setText("Werewolfs");
		size = werewolfTitle.getPreferredSize();
		werewolfTitle.setBounds(w/9, 20, size.width, size.height);
		this.add(werewolfTitle);
		
		villagerTitle = new JLabel("Villagers");
		villagerTitle.setText("Villagers");
		villagerTitle.setBounds(w/9*3, 20, size.width, size.height); 
		this.add(villagerTitle);

		divinerTitle = new JLabel("Diviners");
		divinerTitle.setText("Diviners");
		divinerTitle.setBounds(w/9*5, 20, size.width, size.height); 
		this.add(divinerTitle);
		
		doctorTitle = new JLabel("Doctors");
		doctorTitle.setText("Doctors");
		doctorTitle.setBounds(w/9*7, 20, size.width, size.height);
		this.add(doctorTitle);

		String[] options = { "BDI", "Strategic", "Random" };
        
        JLabel comboBoxWerewolfLbl = new JLabel();
        comboBoxWerewolfLbl.setBounds(w/9, 25, w/9, 25);
        comboBoxWerewolf = new JComboBox(options);
        comboBoxWerewolf.setBounds(w/9, 30, w/9, 30);
        this.add(comboBoxWerewolf);
        


	}

	@Override 
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		//g.drawImage(image, 0, 0, this); 
		}
	
	public void setInfoTestLbl(String text) {
		infoTestLbl.setText(text);	
	}
	
}