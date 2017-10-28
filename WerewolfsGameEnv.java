// Environment code for project WerewolfsGame.mas2j

import jason.asSyntax.*;
import jason.environment.*;
import java.util.logging.*;
import java.io.File;
import java.util.Scanner;
import java.lang.String;


public class WerewolfsGameEnv extends jason.environment.Environment {

    private Logger logger = Logger.getLogger("WerewolfsGame.mas2j." + WerewolfsGameEnv.class.getName());

    /** Called before the MAS execution with the args informed in .mas2j */
    @Override
    public void init(String[] args) {
        super.init(args);
		try { this.readOptionFile(); }
		catch (Exception e) {}
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

