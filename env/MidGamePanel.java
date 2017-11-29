package env;

import jason.asSyntax.*;
import jason.environment.*;
import java.util.logging.*;
import java.io.*;
import java.util.*;
import java.lang.String;
import javax.swing.*;
import javax.swing.text.*;

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
	private ArrayList<Boolean> werewolfsLive = new ArrayList<Boolean>();
	private ArrayList<Boolean> villagersLive = new ArrayList<Boolean>();
	private ArrayList<Boolean> divinersLive = new ArrayList<Boolean>();
	private ArrayList<Boolean> doctorsLive = new ArrayList<Boolean>();

	private JScrollPane gameEventsScrollPanel;

	private JTextPane gameEventsTA;
	private JTextPane werewolfsTA = new JTextPane();
	private JTextPane villagersTA = new JTextPane();
	private JTextPane divinersTA = new JTextPane();
	private JTextPane doctorsTA = new JTextPane();

	private JLabel phaseGameLbl = new JLabel();

	private BufferedImage sunImage;
	private BufferedImage moonImage;
	private BufferedImage wallpaper;

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
		  wallpaper = ImageIO.read(new File("./assets/wallpaper.jpg"));
       	} catch (IOException ex) {}

		// PHASE GAME LABEL
		phaseGameLbl.setText("PHASE GAME ....");
		Dimension size = phaseGameLbl.getPreferredSize();
		phaseGameLbl.setBounds(w/2 + w/6, 65, size.width, size.height);
		this.add(phaseGameLbl);

		// MAIN EVENTS
		gameEventsTA = new JTextPane();
		appendTextToEventPane(gameEventsTA, "MAIN EVENTS\n\n", Color.RED);
		gameEventsTA.setEditable(false);
		gameEventsTA.setPreferredSize(new Dimension(400, 400));

		gameEventsScrollPanel = new JScrollPane(gameEventsTA);
		gameEventsScrollPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		size = gameEventsScrollPanel.getPreferredSize();
		gameEventsScrollPanel.setBounds(290, 150, size.width, size.height);
		this.add(gameEventsScrollPanel);

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

	private Dimension createAgentTA(JTextPane agentTA, int x, int y, int numRows, int numColumns) {
		agentTA.setPreferredSize(new Dimension(10*numColumns, 15*numRows));
		Dimension size = agentTA.getPreferredSize();
        agentTA.setBounds(x, y, size.width, size.height);

		this.add(agentTA);
		agentTA.setEditable(false);
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
	}

	public synchronized void playerJoined(String name, String role) {
		waitForGUI();

		name = formatStrings(name);
		if (role.equals("werewolf")) {
			werewolfs.add(name);
			werewolfsLive.add(Boolean.TRUE);
		}
		if (role.equals("villager")) {
			villagers.add(name);
			villagersLive.add(Boolean.TRUE);
		}
		if (role.equals("diviner")) {
			diviners.add(name);
			divinersLive.add(Boolean.TRUE);
		}
		if (role.equals("doctor")) {
			doctors.add(name);
			doctorsLive.add(Boolean.TRUE);
		}

		updatePlayers(werewolfs, werewolfsLive, werewolfsTA);
		updatePlayers(villagers, villagersLive, villagersTA);
		updatePlayers(diviners, divinersLive, divinersTA);
		updatePlayers(doctors, doctorsLive, doctorsTA);
	}

	private void updatePlayers(ArrayList<String> playerGroup, ArrayList<Boolean> playersLiveness, JTextPane playerTAInfo) {
		playerTAInfo.setText("");
		for (int i = 0; i < playerGroup.size(); i++) {
			if (playersLiveness.get(i))
				appendTextToEventPane(playerTAInfo, playerGroup.get(i), Color.BLACK);
			else
				appendTextToEventPane(playerTAInfo, playerGroup.get(i), Color.RED);
			appendTextToEventPane(playerTAInfo, "\n", Color.BLACK);
		}
	}

	public synchronized void updateEventPanelEnv(String message) {
		appendTextToEventPane(gameEventsTA, formatStrings(message), Color.BLACK);
		appendTextToEventPane(gameEventsTA, "\n", Color.BLACK);
	}

	public synchronized void updateEventPanelEnv(String message, String color) {
		appendTextToEventPane(gameEventsTA, formatStrings(message), Color.ORANGE); // TODO: map: color String -> color Color
		appendTextToEventPane(gameEventsTA, "\n", Color.BLACK);
	}

	public synchronized void playerDied(String playerName) {
		playerName = formatStrings(playerName);
		
		for (int i = 0; i < werewolfs.size(); i++)
			if (werewolfs.get(i).equals(playerName))
				werewolfsLive.set(i, Boolean.FALSE);
		for (int i = 0; i < villagers.size(); i++)
			if (villagers.get(i).equals(playerName))
				villagersLive.set(i, Boolean.FALSE);
		for (int i = 0; i < diviners.size(); i++)
			if (diviners.get(i).equals(playerName))
				divinersLive.set(i, Boolean.FALSE);
		for (int i = 0; i < doctors.size(); i++)
			if (doctors.get(i).equals(playerName))
				doctorsLive.set(i, Boolean.FALSE);

		updatePlayers(werewolfs, werewolfsLive, werewolfsTA);
		updatePlayers(villagers, villagersLive, villagersTA);
		updatePlayers(diviners, divinersLive, divinersTA);
		updatePlayers(doctors, doctorsLive, doctorsTA);
	}

	/**
	 *	Based on:
	 *  - https://stackoverflow.com/questions/1985021/deleting-and-replacing-selected-text-in-jeditorpane
 	 *  - https://stackoverflow.com/questions/9650992/how-to-change-text-color-in-the-jtextarea
	 */
	private void appendTextToEventPane(JTextPane tp, String msg, Color c) {
		tp.setEditable(true);

        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

        aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
        aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

        int len = tp.getDocument().getLength();
        tp.setCaretPosition(len);
        tp.setCharacterAttributes(aset, false);
        tp.replaceSelection(msg);

		tp.setEditable(false);
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
		int h = this.env.HEIGHT_FRAME;

		// BACKGROUND
		g.drawImage(wallpaper, 0, 0, w, h, this);

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
