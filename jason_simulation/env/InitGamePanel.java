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

	private JTextField werewolfsNumTF_random = new JTextField();
  	private JTextField villagersNumTF_random = new JTextField();
	private JTextField divinersNumTF_random = new JTextField();
	private JTextField doctorsNumTF_random = new JTextField();

	private JTextField werewolfsNumTF_strategic = new JTextField();
  	private JTextField villagersNumTF_strategic = new JTextField();
	private JTextField divinersNumTF_strategic = new JTextField();
	private JTextField doctorsNumTF_strategic = new JTextField();

	private JTextField werewolfsNumTF_bdi = new JTextField();
  	private JTextField villagersNumTF_bdi = new JTextField();
	private JTextField divinersNumTF_bdi = new JTextField();
	private JTextField doctorsNumTF_bdi = new JTextField();

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

				int RV = Integer.parseInt(villagersNumTF_random.getText());
				int SV = Integer.parseInt(villagersNumTF_strategic.getText());
				int BV = Integer.parseInt(villagersNumTF_bdi.getText());
				int RW = Integer.parseInt(werewolfsNumTF_random.getText());
				int SW = Integer.parseInt(werewolfsNumTF_strategic.getText());
				int BW = Integer.parseInt(werewolfsNumTF_bdi.getText());
				int RDi = Integer.parseInt(divinersNumTF_random.getText());
				int SDi = Integer.parseInt(divinersNumTF_strategic.getText());
				int BDi = Integer.parseInt(divinersNumTF_bdi.getText());
				int RDo = Integer.parseInt(doctorsNumTF_random.getText());
				int SDo = Integer.parseInt(doctorsNumTF_strategic.getText());
				int BDo = Integer.parseInt(doctorsNumTF_bdi.getText());

				int[] agents = {RV, SV, BV, RW, SW, BW, RDi, SDi, BDi, RDo, SDo, BDo};

				String literal = "createAgents(" + agents[0];
				for (int i = 1; i < agents.length; i++) literal += "," + agents[i];
				env.addPercept("master",Literal.parseLiteral(literal + ")"));

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

        // NUMBER AGENTS LABELS
        int yNumAgents = yTitles + size.height + 20 + w/9;
		createNumAgentsLbl(w/9*1, yNumAgents, "Number random");
		createNumAgentsLbl(w/9*3, yNumAgents, "Number random");
		createNumAgentsLbl(w/9*5, yNumAgents, "Number random");
		size = createNumAgentsLbl(w/9*7, yNumAgents, "Number random");

        // NUMBER AGENTS INPUTS
        int yNumAgentsTF = yNumAgents + size.height + 5;
		createNumAgentsInput(werewolfsNumTF_random, w/9*1, yNumAgentsTF, "2");
		createNumAgentsInput(villagersNumTF_random, w/9*3, yNumAgentsTF, "7");
		createNumAgentsInput(divinersNumTF_random, w/9*5, yNumAgentsTF, "1");
		size = createNumAgentsInput(doctorsNumTF_random, w/9*7, yNumAgentsTF, "1");

		// NUMBER AGENTS LABELS
		yNumAgents = yNumAgentsTF + size.height + 10;
		createNumAgentsLbl(w/9*1, yNumAgents, "Number strategic");
		createNumAgentsLbl(w/9*3, yNumAgents, "Number strategic");
		createNumAgentsLbl(w/9*5, yNumAgents, "Number strategic");
		size = createNumAgentsLbl(w/9*7, yNumAgents, "Number strategic");

		// NUMBER AGENTS INPUTS
		yNumAgentsTF = yNumAgents + size.height + 5;
		createNumAgentsInput(werewolfsNumTF_strategic, w/9*1, yNumAgentsTF, "0");
		createNumAgentsInput(villagersNumTF_strategic, w/9*3, yNumAgentsTF, "0");
		createNumAgentsInput(divinersNumTF_strategic, w/9*5, yNumAgentsTF, "0");
		size = createNumAgentsInput(doctorsNumTF_strategic, w/9*7, yNumAgentsTF, "0");

		// NUMBER AGENTS LABELS
		yNumAgents = yNumAgentsTF + size.height + 10;
		createNumAgentsLbl(w/9*1, yNumAgents, "Number BDI");
		createNumAgentsLbl(w/9*3, yNumAgents, "Number BDI");
		createNumAgentsLbl(w/9*5, yNumAgents, "Number BDI");
		size = createNumAgentsLbl(w/9*7, yNumAgents, "Number BDI");

		// NUMBER AGENTS INPUTS
		yNumAgentsTF = yNumAgents + size.height + 5;
		createNumAgentsInput(werewolfsNumTF_bdi, w/9*1, yNumAgentsTF, "0");
		createNumAgentsInput(villagersNumTF_bdi, w/9*3, yNumAgentsTF, "0");
		createNumAgentsInput(divinersNumTF_bdi, w/9*5, yNumAgentsTF, "0");
		size = createNumAgentsInput(doctorsNumTF_bdi, w/9*7, yNumAgentsTF, "0");

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
