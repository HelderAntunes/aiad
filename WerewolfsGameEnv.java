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
		
		try {
			this.readOptionFile();
			addPercept(ASSyntax.parseLiteral("percept(demo)"));
		}
		catch (Exception e) {}
		
    }
	
	public void readOptionFile () throws Exception {
		File file = new File("werewolf_options.txt");
		Scanner scanner = new Scanner(file);
		
		while (scanner.hasNext()) {
			String type = scanner.next();
			int num = scanner.nextInt();
			if (type.equals("villager_random")) {
				addPercept(Literal.parseLiteral("createRandomVillager(" + num + ")"));
			}
			else if (type.equals("werewolf_random")) {
				addPercept(Literal.parseLiteral("createRandomWerewolf(" + num + ")"));
			}
			else if (type.equals("diviner_random")) {
				addPercept(Literal.parseLiteral("createRandomDiviner(" + num + ")"));
			}
		}
	}

    @Override
    public boolean executeAction(String agName, Structure action) {
        logger.info("executing: "+action+", but not implemented!");
        if (true) { // you may improve this condition
             informAgsEnvironmentChanged();
        }
        return true; // the action was executed with success
    }

    /** Called before the end of MAS execution */
    @Override
    public void stop() {
        super.stop();
    }
}

