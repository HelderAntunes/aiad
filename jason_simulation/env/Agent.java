package env;

import java.io.*;
import java.util.*;
import java.lang.String;

class Agent {

	String name, role, type;
  ArrayList<String> trusts = new ArrayList<String>();
  ArrayList<String> suspects = new ArrayList<String>();
  Boolean live = true;

	public Agent(String name, String role, String type) {
		this.name = name;
		this.role = role;
		this.type = type;
	}

	public String toString() {
		return name + " (" + role + ", " + type + ")";
	}

	public String getTrustsInString() {
		String trustsS = "";
		for (int i = 0; i < trusts.size(); i++)
			trustsS += trusts.get(i) + "\n";
		return trustsS;
	}

	public String getSuspectsInString() {
		String suspectsS = "";
		for (int i = 0; i < suspects.size(); i++)
			suspectsS += suspects.get(i) + "\n";
		return suspectsS;
	}

	public String getAgentInfo() {
		String info = "";

		if (live) info += "State: live\n";
		else info += "State: dead\n";

		info += "\n";
		info += "Trusts\n";
		info += getTrustsInString();

		info += "\n";
		info += "Suspects\n";
		info += getSuspectsInString();

		return info;
	}
}
