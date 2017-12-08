// Environment code for project WerewolfsGame.mas2j
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

public class WerewolfsGameEnv extends jason.environment.Environment {

    private Logger logger = Logger.getLogger("WerewolfsGame.mas2j." + WerewolfsGameEnv.class.getName());
    public static int WIDTH_FRAME = 1000;
    public static int HEIGHT_FRAME = 600;

    JFrame frame;
	  JPanel currPanel;

    /** Called before the MAS execution with the args informed in .mas2j */
    @Override
    public void init(String[] args) {
        super.init(args);

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                initGUI();
            }
        });

    }
    private void initGUI() {
		frame = new JFrame("The Werewolves of Millers Hollow");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WIDTH_FRAME, HEIGHT_FRAME);
        frame.setResizable(false);
        frame.setVisible(true);

        currPanel = new InitGamePanel(this);
        frame.getContentPane().add(currPanel);
    }

	public JPanel getCurrPanel() {
		return currPanel;
	}

	public void setCurrPanel(JPanel newPanel) {
		currPanel = newPanel;
	}

    public JFrame getFrame() {
    	return frame;
    }

    public Logger getLogger() {
        return logger;
    }

    @Override
    public boolean executeAction(String agName, Structure action) {

		if (action.getFunctor().equals("playerJoined")) {
            while (!(currPanel instanceof MidGamePanel)) waitForGUI();
            String name = action.getTerm(0).toString();
            String role = action.getTerm(1).toString();
            String type = action.getTerm(2).toString();
            ((MidGamePanel)currPanel).playerJoined(name, role, type);
		} else if (action.getFunctor().equals("updateTimeDayEnv")) {
            String timeDay = action.getTerm(0).toString();
            String currDay = action.getTerm(1).toString();
            ((MidGamePanel)currPanel).updateTimeDayEnv(timeDay, currDay);
        } else if (action.getFunctor().equals("updateEventDayEnv")) {
            String event = action.getTerm(0).toString();
            ((MidGamePanel)currPanel).updateEventDayEnv(event);
        } else if (action.getFunctor().equals("updateEventPanelEnv")) {
            String eventMessage = action.getTerm(0).toString();

            if (action.getArity() == 1)
                ((MidGamePanel)currPanel).updateEventPanelEnv(eventMessage);
            if (action.getArity() == 2) {
                String color = action.getTerm(1).toString();
                ((MidGamePanel)currPanel).updateEventPanelEnv(eventMessage, color);
            }
        } else if (action.getFunctor().equals("playerDied")) {
            String player = action.getTerm(0).toString();
            ((MidGamePanel)currPanel).playerDied(player);
        } else if (action.getFunctor().equals("gameFinished")) {
            String eventMessage = action.getTerm(0).toString();
            ((MidGamePanel)currPanel).updateEventPanelEnv(eventMessage);
        } else if (action.getFunctor().equals("addTrust")) {
            String believer = action.getTerm(0).toString();
            String victimOfTrust = action.getTerm(1).toString();
            String value = action.getTerm(2).toString();
            logger.info(believer + " trust " + value + " to " + victimOfTrust);
            ((MidGamePanel)currPanel).addTrust(believer, victimOfTrust, value);
        } else if (action.getFunctor().equals("addSuspect")) {
            String believer = action.getTerm(0).toString();
            String victimOfTrust = action.getTerm(1).toString();
            String role = action.getTerm(2).toString();
            String value = action.getTerm(3).toString();
            ((MidGamePanel)currPanel).addSuspect(believer, victimOfTrust, role, value);
        } else if (action.getFunctor().equals("remTrust")) {
            String believer = action.getTerm(0).toString();
            String victimOfTrust = action.getTerm(1).toString();
            String value = action.getTerm(2).toString();
            ((MidGamePanel)currPanel).remTrust(believer, victimOfTrust, value);
        } else if (action.getFunctor().equals("remSuspect")) {
            String believer = action.getTerm(0).toString();
            String victimOfTrust = action.getTerm(1).toString();
            String role = action.getTerm(2).toString();
            String value = action.getTerm(3).toString();
            ((MidGamePanel)currPanel).remSuspect(believer, victimOfTrust, role, value);
        } else {
    			logger.info("executing: "+action+", but not implemented!");
    			return false;
    		}

        return true;
    }

    private void waitForGUI() {
		try {
			Thread.sleep(400);
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}

    /** Called before the end of MAS execution */
    @Override
    public void stop() {
        super.stop();
    }
}
