// Internal action code for project WerewolfsGame.mas2j
package mylib;

import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;

import jason.JasonException;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.InternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.NumberTermImpl;
import jason.asSyntax.Term;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class randomName extends DefaultInternalAction {
    private static InternalAction singleton = null;
    public static InternalAction create() {
        if (singleton == null)
            singleton = new randomName();
        return singleton;
    }
	
	private ArrayList<String> first_names = new ArrayList<String>(Arrays.asList(
		"Bob", "Jonathan", "Robert", "Pierre", 
		"Tim", "Ink", "Arthur", "David", "Henry", 
		"Dallas", "Mike", "Sterling", "Dave",
		"Anne", "Rossette", "Holly", "Mitty",
		"Lisbeth", "Elizabeth", "Lilian", "Cynthia", "Alexa", 
		"Ermelinda", "Karyn", "Jacqualine", "Arnette"
	));
	private ArrayList<String> last_names = new ArrayList<String>(Arrays.asList(
		"the Builder", "Joestar", "Nicholas", "Timberwood",
		"Walker", "Simpsims", "Brown", "Penacova", "Smith", "Jones", "Taylor", "Williams", "Davies", 
		"Evans"
	));
    private Random random = new Random();

    @Override public int getMinArgs() {
        return 1;
    }
    @Override public int getMaxArgs() {
        return 1;
    }

    @Override protected void checkArguments(Term[] args) throws JasonException {
        super.checkArguments(args); // check number of arguments
        if (!args[0].isVar())
            throw JasonException.createWrongArgument(this,"first argument must be a variable.");
    }

    @Override
    public Object execute(final TransitionSystem ts, final Unifier un, final Term[] args) throws Exception {
        checkArguments(args);
        return un.unifies(args[0], new StringTermImpl(getRandomName()));    
    }
	
	private String getRandomName(){
		return first_names.get(random.nextInt(first_names.size())) + " " + last_names.get(random.nextInt(last_names.size()));
	}
}


