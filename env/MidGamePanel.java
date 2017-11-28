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

	private boolean guiDone = false;

	public MidGamePanel(WerewolfsGameEnv env) {
		this.env = env;
		this.frame = env.getFrame();
		this.setLayout(null);

		JButton startBtn = new JButton("EXIT");
        Dimension size = startBtn.getPreferredSize();
        startBtn.setBounds(700, 520,size.width, size.height);
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

		// MAIN EVENTS
		gameEventsTA = new JTextArea(25, 40);
		gameEventsTA.setText("MAIN EVENTS");
		size = gameEventsTA.getPreferredSize();
		gameEventsTA.setBounds(270, 100, size.width, size.height);
		gameEventsTA.setEditable(false);
		this.add(gameEventsTA);

		int w = env.WIDTH_FRAME;
		int h = env.HEIGHT_FRAME;

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

	public void playerJoined(String name, String role) {
		waitForGUI();

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
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
	}

}
