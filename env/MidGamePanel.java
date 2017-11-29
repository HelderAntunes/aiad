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

	private JTextArea gameEventsTA;
	private JTextArea werewolfsTA = new JTextArea();
	private JTextArea villagersTA = new JTextArea();
	private JTextArea divinersTA = new JTextArea();
	private JTextArea doctorsTA = new JTextArea();

	private JLabel phaseGameLbl = new JLabel();

	private BufferedImage sunImage;
	private BufferedImage moonImage;

	private boolean guiDone = false;

	private String timeDay = "Day"; // Day, Night
	private String currDay = "0"; // 1, 2, 3, ...
	private String eventDay = "Discussion"; // Discussion, Vote

	public MidGamePanel(WerewolfsGameEnv env) {
		this.env = env;
		this.frame = env.getFrame();
		this.setLayout(null);

		int w = env.WIDTH_FRAME;
		int h = env.HEIGHT_FRAME;

		try {
		  sunImage = ImageIO.read(new File("./assets/sun.png"));
		  moonImage = ImageIO.read(new File("./assets/moon.png"));
       	} catch (IOException ex) {}

		// PHASE GAME LABEL
		phaseGameLbl.setText("PHASE GAME ....");
		Dimension size = phaseGameLbl.getPreferredSize();
		phaseGameLbl.setBounds(w/2 + w/6, 65, size.width, size.height);
		this.add(phaseGameLbl);

		// MAIN EVENTS
		gameEventsTA = new JTextArea(25, 40);
		gameEventsTA.setText("MAIN EVENTS");
		size = gameEventsTA.getPreferredSize();
		gameEventsTA.setBounds(290, 150, size.width, size.height);
		gameEventsTA.setEditable(false);
		this.add(gameEventsTA);

		// AGENTS INFO LABELS
        int yAgentsInfo = 20;
        int xAgentsInfo = w/9-50;

		size = createAgentLbl(xAgentsInfo, yAgentsInfo, "Werewolfs");
        yAgentsInfo += size.height + 5;
		size = createAgentTA(werewolfsTA, xAgentsInfo, yAgentsInfo, 5, 15);

        yAgentsInfo += size.height + 10;
		size = createAgentLbl(xAgentsInfo, yAgentsInfo, "Villagers");
        yAgentsInfo += size.height + 5;
		size = createAgentTA(villagersTA, xAgentsInfo, yAgentsInfo, 12, 15);

        yAgentsInfo += size.height + 10;
		size = createAgentLbl(xAgentsInfo, yAgentsInfo, "Diviners");
        yAgentsInfo += size.height + 5;
		size = createAgentTA(divinersTA, xAgentsInfo, yAgentsInfo, 5, 15);

		yAgentsInfo += size.height + 10;
		size = createAgentLbl(xAgentsInfo, yAgentsInfo, "Doctors");
        yAgentsInfo += size.height + 5;
		size = createAgentTA(doctorsTA, xAgentsInfo, yAgentsInfo, 5, 15);

		guiDone = true;
	}

	private Dimension createAgentLbl(int x, int y, String text) {
		JLabel agentLbl = new JLabel();
        agentLbl.setText(text);

		Dimension size = agentLbl.getPreferredSize();
        agentLbl.setBounds(x, y, size.width, size.height);

		this.add(agentLbl);
		return size;
	}

	private Dimension createAgentTA(JTextArea agentTA, int x, int y, int numRows, int numColumns) {
		agentTA.setRows(numRows);
		agentTA.setColumns(numColumns);

		Dimension size = agentTA.getPreferredSize();
        agentTA.setBounds(x, y, size.width, size.height);

		this.add(agentTA);
		return size;
	}

	public void updateTimeDayEnv(String timeDay, String currDay) {
		this.timeDay = formatStrings(timeDay);
		this.currDay = currDay;
		updatePhaseGameLbl();
		repaint();
	}

	public void updateEventDayEnv(String event) {
		this.eventDay = event;
		updatePhaseGameLbl();
	}

	private void updatePhaseGameLbl() {
		int w = this.env.WIDTH_FRAME;
		phaseGameLbl.setText(this.timeDay + " " + this.currDay + ": " + this.eventDay);
		Dimension size = phaseGameLbl.getPreferredSize();
		phaseGameLbl.setBounds(w/2 + w/6, 65, size.width, size.height);
		//this.add(phaseGameLbl);
	}

	public void playerJoined(String name, String role) {
		waitForGUI();

		name = formatStrings(name);
		if (role.equals("werewolf")) werewolfs.add(name);
		if (role.equals("villager")) villagers.add(name);
		if (role.equals("diviner")) diviners.add(name);
		if (role.equals("doctor")) doctors.add(name);

		gameEventsTA.append("\n" + name + " " + role);

		updatePlayers(werewolfs, werewolfsTA);
		updatePlayers(villagers, villagersTA);
		updatePlayers(diviners, divinersTA);
		updatePlayers(doctors, doctorsTA);
	}

	private void updatePlayers(ArrayList<String> playerGroup, JTextArea playerTAInfo) {
		playerTAInfo.setText("");
		for (int i = 0; i < playerGroup.size(); i++) {
			if (i > 0) playerTAInfo.append("\n");
			playerTAInfo.append(playerGroup.get(i));
		}
	}

	private void waitForGUI() {
		try {
			while (!guiDone) Thread.sleep(400);
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}

	private String formatStrings(String text) {
		if (text.length() <= 2)
			return text;
		return text.substring(1, text.length()-1);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		int w = this.env.WIDTH_FRAME;
		int sizeSquare = w/9;
		int yImages = 35;
		if (this.timeDay.equals("Day")) {
			g.drawImage(sunImage, w/2 + w/13 - sizeSquare/2, yImages, sizeSquare, sizeSquare, this);
		}
		else {
			g.drawImage(moonImage, w/2 + w/13 - sizeSquare/2, yImages, sizeSquare, sizeSquare, this);
		}
	}

}
