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
    public static int WIDTH_FRAME = 800;
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
            ((MidGamePanel)currPanel).playerJoined(action.getTerm(0).toString(), action.getTerm(1).toString());

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
