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
	private WerewolfsGameEnv env;

	private JLabel werewolfTitle;
	private JLabel villagerTitle;
	private JLabel divinerTitle;
	private JLabel doctorTitle;

	private String[] options = { "Random", "Strategic", "BDI" };
	private JComboBox comboBoxWerewolf = new JComboBox(options);
	private JComboBox comboBoxVillager = new JComboBox(options);
	private JComboBox comboBoxDiviner = new JComboBox(options);
	private JComboBox comboBoxDoctor = new JComboBox(options);

	private JTextField wereWolfsNumTF = new JTextField();
  	private JTextField villagersNumTF = new JTextField();
	private JTextField divinersNumTF = new JTextField();
	private JTextField doctorsNumTF = new JTextField();

	private BufferedImage werewolfImage;
	private BufferedImage villagerImage;
	private BufferedImage divinerImage;
	private BufferedImage doctorImage;
	private BufferedImage wallpaper;

	public InitGamePanel(WerewolfsGameEnv env) {

		this.env = env;
		this.frame = env.getFrame();
		this.setLayout(null);

		try {
		  werewolfImage = ImageIO.read(new File("./assets/werewolf.png"));
		  villagerImage = ImageIO.read(new File("./assets/villager.png"));
		  divinerImage = ImageIO.read(new File("./assets/diviner.png"));
		  doctorImage = ImageIO.read(new File("./assets/doctor.png"));
		  wallpaper = ImageIO.read(new File("./assets/wallpaper.jpg"));
       	} catch (IOException ex) {}

		int w = env.WIDTH_FRAME;
		int h = env.HEIGHT_FRAME;

		JButton startBtn = new JButton("START");
        Dimension size = startBtn.getPreferredSize();
        startBtn.setBounds(w/2 - size.width/2, h - h/6, size.width, size.height);
        startBtn.addActionListener(new ActionListener() {

			/**
			* Agent initials:
			* RV, SV, BV: Random, Strategic, BDI villager
			* RW, SW, BW: Random, Strategic, BDI werewolf
			* RDi, SDi, BDi: Random, Strategic, BDI diviner
			* RDo, SDo, BDo: Random, Strategic, BDI doctor
			*
			* agents array = [RV, SV, BV, RW, SW, BW, RDi, SDi, BDi, RDo, SDo, BDo]
			*
			*/
			public void actionPerformed(ActionEvent e) {

				String typeWerewolfs = comboBoxWerewolf.getSelectedItem().toString();
				String typeVillagers = comboBoxVillager.getSelectedItem().toString();
				String typeDiviners = comboBoxDiviner.getSelectedItem().toString();
				String typeDoctors = comboBoxDoctor.getSelectedItem().toString();

				int[] agents = new int[12];

				int werewolfNum = Integer.parseInt(wereWolfsNumTF.getText());
				if (typeWerewolfs.equals("Random")) agents[3] = werewolfNum;
				if (typeWerewolfs.equals("Strategic")) agents[4] = werewolfNum;
				if (typeWerewolfs.equals("BDI")) agents[5] = werewolfNum;

				int villagerNum = Integer.parseInt(villagersNumTF.getText());
				if (typeVillagers.equals("Random")) agents[0] = villagerNum;
				if (typeVillagers.equals("Strategic")) agents[1] = villagerNum;
				if (typeVillagers.equals("BDI")) agents[2] = villagerNum;

				int divinerNum = Integer.parseInt(divinersNumTF.getText());
				if (typeDiviners.equals("Random")) agents[6] = divinerNum;
				if (typeDiviners.equals("Strategic")) agents[7] = divinerNum;
				if (typeDiviners.equals("BDI")) agents[8] = divinerNum;

				int doctorNum = Integer.parseInt(doctorsNumTF.getText());
				if (typeDoctors.equals("Random")) agents[9] = doctorNum;
				if (typeDoctors.equals("Strategic")) agents[10] = doctorNum;
				if (typeDoctors.equals("BDI")) agents[11] = doctorNum;

				String literal = "createAgents(" + agents[0];
				for (int i = 1; i < agents.length; i++) literal += "," + agents[i];
				env.addPercept(Literal.parseLiteral(literal + ")"));

				frame.getContentPane().removeAll();
				frame.getContentPane().invalidate();
				JPanel newPanel = new MidGamePanel(env);
				env.setCurrPanel(newPanel);
				frame.getContentPane().add(newPanel);
				frame.getContentPane().revalidate();
			}
		});
        this.add(startBtn);

		// MAIN TITLE
		int yMainTitle = 20;
		JLabel mainTitle = new JLabel("MainTile");
		mainTitle.setFont(new Font("Serif", Font.PLAIN, 30));
		mainTitle.setText("Initial configuration");
		size = mainTitle.getPreferredSize();
		mainTitle.setBounds(w/2 - size.width/2, yMainTitle, size.width, size.height);
		this.add(mainTitle);

		// TITLES
		int yTitles = 100 + yMainTitle + size.height;
		createAgentTitle("Werewolfs", w/9*1, yTitles);
		createAgentTitle("Villagers", w/9*3, yTitles);
		createAgentTitle("Diviners", w/9*5, yTitles);
		createAgentTitle("Doctors", w/9*7, yTitles);

        // COMBO LABELS
        int yComboLabels = yTitles + size.height + 20 + w/9;
		createComboTypeAgentLbl(w/9*1, yComboLabels);
		createComboTypeAgentLbl(w/9*3, yComboLabels);
		createComboTypeAgentLbl(w/9*5, yComboLabels);
		size = createComboTypeAgentLbl(w/9*7, yComboLabels);

        // COMBOS
        int yCombos = yComboLabels + size.height + 5;
		createTypeAgentComboBox(comboBoxWerewolf, w/9*1, yCombos);
		createTypeAgentComboBox(comboBoxVillager, w/9*3, yCombos);
		createTypeAgentComboBox(comboBoxDiviner, w/9*5, yCombos);
		size = createTypeAgentComboBox(comboBoxDoctor, w/9*7, yCombos);

        // NUMBER AGENTS LABELS
        int yNumAgents = yCombos + size.height + 10;
		createNumAgentsLbl(w/9*1, yNumAgents, "Number");
		createNumAgentsLbl(w/9*3, yNumAgents, "Number");
		createNumAgentsLbl(w/9*5, yNumAgents, "Number");
		size = createNumAgentsLbl(w/9*7, yNumAgents, "Number");

        // NUMBER AGENTS INPUTS
        int yNumAgentsTF = yNumAgents + size.height + 5;
		createNumAgentsInput(wereWolfsNumTF, w/9*1, yNumAgentsTF, "2");
		createNumAgentsInput(villagersNumTF, w/9*3, yNumAgentsTF, "7");
		createNumAgentsInput(divinersNumTF, w/9*5, yNumAgentsTF, "1");
		size = createNumAgentsInput(doctorsNumTF, w/9*7, yNumAgentsTF, "1");

		setBackground(new java.awt.Color(204, 166, 166));
	}

	private Dimension createAgentTitle(String text, int x, int y) {
		JLabel agentTitle = new JLabel();
		agentTitle.setText(text);
		Dimension size = agentTitle.getPreferredSize();
		agentTitle.setBounds(x, y, size.width, size.height);
		this.add(agentTitle);
		return size;
	}

	private Dimension createComboTypeAgentLbl(int x, int y) {
		JLabel comboBoxLbl = new JLabel();
        comboBoxLbl.setText("Agent type");
        Dimension size = comboBoxLbl.getPreferredSize();
        comboBoxLbl.setBounds(x, y, size.width, size.height);
        this.add(comboBoxLbl);
		return size;
	}

	private Dimension createTypeAgentComboBox(JComboBox comboBox, int x, int y) {
        Dimension size = comboBox.getPreferredSize();
        comboBox.setBounds(x, y, size.width, size.height);
        this.add(comboBox);
		return size;
	}

	private Dimension createNumAgentsLbl(int x, int y, String text) {
		JLabel numAgentsLbl = new JLabel();
        numAgentsLbl.setText(text);
        Dimension size = numAgentsLbl.getPreferredSize();
        numAgentsLbl.setBounds(x, y, size.width, size.height);
        this.add(numAgentsLbl);
		return size;
	}

	private Dimension createNumAgentsInput(JTextField numAgentsTF, int x, int y, String text) {
		numAgentsTF.setColumns(6);
        numAgentsTF.setText(text);
        Dimension size = numAgentsTF.getPreferredSize();
        numAgentsTF.setBounds(x, y, size.width, size.height);
        this.add(numAgentsTF);
		return size;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		int w = this.env.WIDTH_FRAME;
		int h = this.env.HEIGHT_FRAME;

		// BACKGROUND
		// g.drawImage(wallpaper, 0, 0, w, h, this);

		int sizeSquare = w/9;
		int yImages = 180;
		g.drawImage(werewolfImage, w/9*1, yImages, sizeSquare, sizeSquare, this);
		g.drawImage(villagerImage, w/9*3, yImages, sizeSquare, sizeSquare, this);
		g.drawImage(divinerImage, w/9*5, yImages, sizeSquare, sizeSquare, this);
		g.drawImage(doctorImage, w/9*7, yImages, sizeSquare, sizeSquare, this);
	}

}
