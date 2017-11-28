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
	private JTextArea werewolfsTA;
	private JTextArea villagersTA;
	private JTextArea divinersTA;
	private JTextArea doctorsTA;

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

		// MAIN DISCUSSION
		gameEventsTA = new JTextArea(25, 40);
		gameEventsTA.setText("sfsdfsdfsdf");
		size = gameEventsTA.getPreferredSize();
		gameEventsTA.setBounds(270, 100, size.width, size.height);
		gameEventsTA.setEditable(false);
		this.add(gameEventsTA);

		int w = env.WIDTH_FRAME;
		int h = env.HEIGHT_FRAME;

		// AGENTS INFO LABELS
        int yAgentsInfo = 20;
        int xAgentsInfo = w/9-50;

        JLabel wereWolfsLbl = new JLabel();
        wereWolfsLbl.setText("Werewolfs");
        size = wereWolfsLbl.getPreferredSize();
        wereWolfsLbl.setBounds(xAgentsInfo, yAgentsInfo, size.width, size.height);
        this.add(wereWolfsLbl);

        yAgentsInfo += size.height + 5;
        werewolfsTA = new JTextArea(5, 15);
        size = werewolfsTA.getPreferredSize();
        werewolfsTA.setBounds(xAgentsInfo, yAgentsInfo, size.width, size.height);
        this.add(werewolfsTA);

        yAgentsInfo += size.height + 10;
        JLabel villagersLbl = new JLabel();
        villagersLbl.setText("Villagers");
        size = villagersLbl.getPreferredSize();
        villagersLbl.setBounds(xAgentsInfo, yAgentsInfo, size.width, size.height);
        this.add(villagersLbl);

        yAgentsInfo += size.height + 5;
        villagersTA = new JTextArea(12, 15);
        size = villagersTA.getPreferredSize();
        villagersTA.setBounds(xAgentsInfo, yAgentsInfo, size.width, size.height);
        this.add(villagersTA);

        yAgentsInfo += size.height + 10;
        JLabel divinersLbl = new JLabel();
        divinersLbl.setText("Diviners");
        size = divinersLbl.getPreferredSize();
        divinersLbl.setBounds(xAgentsInfo, yAgentsInfo, size.width, size.height);
        this.add(divinersLbl);

        yAgentsInfo += size.height + 5;
		divinersTA = new JTextArea(5, 15);
        size = divinersTA.getPreferredSize();
        divinersTA.setBounds(xAgentsInfo, yAgentsInfo, size.width, size.height);
        this.add(divinersTA);

		yAgentsInfo += size.height + 10;
        JLabel doctorsLbl = new JLabel();
        doctorsLbl.setText("Doctors");
        size = doctorsLbl.getPreferredSize();
        doctorsLbl.setBounds(xAgentsInfo, yAgentsInfo, size.width, size.height);
        this.add(doctorsLbl);

        yAgentsInfo += size.height + 5;
		doctorsTA = new JTextArea(5, 15);
        size = doctorsTA.getPreferredSize();
        doctorsTA.setBounds(xAgentsInfo, yAgentsInfo, size.width, size.height);
        this.add(doctorsTA);


	}

	public void playerJoined(String name, String role) {
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

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
	}

}
