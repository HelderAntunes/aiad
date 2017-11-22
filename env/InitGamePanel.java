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
	private JComboBox comboBoxVillager;
	private JComboBox comboBoxDiviner;
	private JComboBox comboBoxDoctor;

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

		// TITLES
		int yTitles = 20;

		werewolfTitle = new JLabel("Werewolfs");
		werewolfTitle.setText("Werewolfs");
		size = werewolfTitle.getPreferredSize();
		werewolfTitle.setBounds(w/9, yTitles, size.width, size.height);
		this.add(werewolfTitle);
		
		villagerTitle = new JLabel("Villagers");
		villagerTitle.setText("Villagers");
		villagerTitle.setBounds(w/9*3, yTitles, size.width, size.height); 
		this.add(villagerTitle);

		divinerTitle = new JLabel("Diviners");
		divinerTitle.setText("Diviners");
		divinerTitle.setBounds(w/9*5, yTitles, size.width, size.height); 
		this.add(divinerTitle);
		
		doctorTitle = new JLabel("Doctors");
		doctorTitle.setText("Doctors");
		doctorTitle.setBounds(w/9*7, yTitles, size.width, size.height);
		this.add(doctorTitle);
        
        // COMBO LABELS
        int yComboLabels = yTitles + size.height + 20;

        JLabel comboBoxWerewolfLbl = new JLabel();
        comboBoxWerewolfLbl.setText("Agent type");
        size = comboBoxWerewolfLbl.getPreferredSize();
        comboBoxWerewolfLbl.setBounds(w/9, yComboLabels, size.width, size.height);
        this.add(comboBoxWerewolfLbl);

        JLabel comboBoxVillagerLbl = new JLabel();
        comboBoxVillagerLbl.setText("Agent type");
        size = comboBoxVillagerLbl.getPreferredSize();
        comboBoxVillagerLbl.setBounds(w/9*3, yComboLabels, size.width, size.height);
        this.add(comboBoxVillagerLbl);

        JLabel comboBoxDivinerfLbl = new JLabel();
        comboBoxDivinerfLbl.setText("Agent type");
        size = comboBoxDivinerfLbl.getPreferredSize();
        comboBoxDivinerfLbl.setBounds(w/9*5, yComboLabels, size.width, size.height);
        this.add(comboBoxDivinerfLbl);

        JLabel comboBoxDoctorLbl = new JLabel();
        comboBoxDoctorLbl.setText("Agent type");
        size = comboBoxDoctorLbl.getPreferredSize();
        comboBoxDoctorLbl.setBounds(w/9*7, yComboLabels, size.width, size.height);
        this.add(comboBoxDoctorLbl);
        
        // COMBOS
        String[] options = { "BDI", "Strategic", "Random" };
        int yCombos = yComboLabels + size.height + 5;

        comboBoxWerewolf = new JComboBox(options);
        size = comboBoxWerewolf.getPreferredSize();
        comboBoxWerewolf.setBounds(w/9, yCombos, size.width, size.height);
        this.add(comboBoxWerewolf);

        comboBoxVillager = new JComboBox(options);
        size = comboBoxVillager.getPreferredSize();
        comboBoxVillager.setBounds(w/9*3, yCombos, size.width, size.height);
        this.add(comboBoxVillager);

        comboBoxDiviner = new JComboBox(options);
        size = comboBoxDiviner.getPreferredSize();
        comboBoxDiviner.setBounds(w/9*5, yCombos, size.width, size.height);
        this.add(comboBoxDiviner);

        comboBoxDoctor = new JComboBox(options);
        size = comboBoxDoctor.getPreferredSize();
        comboBoxDoctor.setBounds(w/9*7, yCombos, size.width, size.height);
        this.add(comboBoxDoctor);

        // NUMBER AGENTS LABELS
        int yNumAgents = yCombos + size.height + 10;
        JLabel numAgentsWerewolfLbl = new JLabel();
        numAgentsWerewolfLbl.setText("Number");
        size = numAgentsWerewolfLbl.getPreferredSize();
        numAgentsWerewolfLbl.setBounds(w/9, yNumAgents, size.width, size.height);
        this.add(numAgentsWerewolfLbl);

        JLabel numAgentsVillagerLbl = new JLabel();
        numAgentsVillagerLbl.setText("Number");
        size = numAgentsVillagerLbl.getPreferredSize();
        numAgentsVillagerLbl.setBounds(w/9*3, yNumAgents, size.width, size.height);
        this.add(numAgentsVillagerLbl);

        JLabel numAgentsDivinerfLbl = new JLabel();
        numAgentsDivinerfLbl.setText("Number");
        size = numAgentsDivinerfLbl.getPreferredSize();
        numAgentsDivinerfLbl.setBounds(w/9*5, yNumAgents, size.width, size.height);
        this.add(numAgentsDivinerfLbl);

        JLabel numAgentsDoctorLbl = new JLabel();
        numAgentsDoctorLbl.setText("Number");
        size = numAgentsDoctorLbl.getPreferredSize();
        numAgentsDoctorLbl.setBounds(w/9*7, yNumAgents, size.width, size.height);
        this.add(numAgentsDoctorLbl);
        
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