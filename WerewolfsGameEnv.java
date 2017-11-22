// Environment code for project WerewolfsGame.mas2j

import jason.asSyntax.*;
import jason.environment.*;
import java.util.logging.*;
import java.io.File;
import java.util.Scanner;
import java.lang.String;
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.Container;
import java.awt.Insets;
import java.awt.Dimension;
import javax.swing.JButton;
import java.awt.Graphics;
import java.awt.event.*;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.awt.image.BufferedImage;

public class WerewolfsGameEnv extends jason.environment.Environment {

    private Logger logger = Logger.getLogger("WerewolfsGame.mas2j." + WerewolfsGameEnv.class.getName());
    private static int WIDTH_FRAME = 800;
    private static int HEIGHT_FRAME = 600;

    JFrame frame = new JFrame("The Werewolves of Millers Hollow");

    /** Called before the MAS execution with the args informed in .mas2j */
    @Override
    public void init(String[] args) {
        super.init(args);
		try { this.readOptionFile(); }
		catch (Exception e) {}
	
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                initGUI();
            }
        });
    }
    private void initGUI() {
        //Create and set up the window.
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        InitGamePanel initGamePanel = new InitGamePanel(frame);
        frame.getContentPane().add(initGamePanel);

        //Size and display the window.
        frame.setSize(WIDTH_FRAME, HEIGHT_FRAME);
        frame.setVisible(true);
    }

    class InitGamePanel extends JPanel {
    	private JFrame frame;
		private BufferedImage image;

    	public InitGamePanel(JFrame frame) {

    		try {                
	          image = ImageIO.read(new File("./IMG_0062.jpg"));
	       	} catch (IOException ex) {

	       	}
    		this.frame = frame;
    		this.setLayout(null);
    		JButton startBtn = new JButton("START");
	        Dimension size = startBtn.getPreferredSize();
	        startBtn.setBounds(150, 15,size.width, size.height);
	        startBtn.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) { 
			    frame.getContentPane().removeAll();
			    frame.getContentPane().invalidate();
				frame.getContentPane().add(new MidGamePanel(frame));
				frame.getContentPane().revalidate();
			  } 
			});
	        this.add(startBtn);
    	}

    	@Override 
    	public void paintComponent(Graphics g) {
    		super.paintComponent(g);
    		g.drawImage(image, 0, 0, this); 
   		}

    }

    class MidGamePanel extends JPanel {
    	private JFrame frame;

    	public MidGamePanel(JFrame frame) {
    		this.frame = frame;
    		this.setLayout(null);
    		JButton startBtn = new JButton("MID");
	        Dimension size = startBtn.getPreferredSize();
	        startBtn.setBounds(150, 15,size.width, size.height);
	        startBtn.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) { 
			    frame.getContentPane().removeAll();
			    frame.getContentPane().invalidate();
				frame.getContentPane().add(new InitGamePanel(frame));
				frame.getContentPane().revalidate();
			  } 
			});
	        this.add(startBtn);
    	}

    	@Override 
    	public void paintComponent(Graphics g) {
    		super.paintComponent(g);
   		}

    }
	
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
	public void readOptionFile () throws Exception {
		File file = new File("werewolf_options.txt");
		Scanner scanner = new Scanner(file);
		int[] agents = new int[12];
		
		while (scanner.hasNext()) {
			String type = scanner.next();
			int num = scanner.nextInt();
			if (type.equals("villager_random")) agents[0] = num;
			else if (type.equals("werewolf_random")) agents[3] = num;
			else if (type.equals("werewolf_bdi")) agents[5] = num;
			else if (type.equals("diviner_random")) agents[6] = num;
			else if (type.equals("doctor_random")) agents[9] = num;
		}
		
		String literal = "createAgents(" + agents[0];
		for (int i = 1; i < agents.length; i++) literal += "," + agents[i]; 
		addPercept(Literal.parseLiteral(literal + ")"));
	}

    @Override
    public boolean executeAction(String agName, Structure action) {
		if (action.getFunctor().equals("Something")) {
			return true;
		} else {
			logger.info("executing: "+action+", but not implemented!");
			return false;
		}
    }

    /** Called before the end of MAS execution */
    @Override
    public void stop() {
        super.stop();
    }
}

