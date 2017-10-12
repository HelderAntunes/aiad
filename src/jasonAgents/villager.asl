// Agent villager in project WereTest.mas2j
/* Initial beliefs and rules */
first_names([
	"Robert",
	"Bob",
	"Just",
	"Elizabeth",
	"Sophie",
	"Mitty"	
]).

last_names([
	"Nicholas",
	"the Woodcutter",
	"Werewolf",
	"Battory",
	"Mitty"	
]).

/* Initial goals */
!start.
/* Plans */
/* 
	Phase 1 
	Generate random distribution of players
*/

//None

/* 
	Phase 2 
	Invite players
*/

+!start <- 
	.my_name(Id);
	+role(Id, villager);
	?first_names(FirstNames);
		.length(FirstNames, LFN);
		.nth(math.floor(math.random(LFN)), FirstNames, FirstName);
	?last_names(LastNames);
		.length(LastNames, LLN);
		.nth(math.floor(math.random(LLN)), LastNames, LastName);
	.concat(FirstName, " ", LastName, Name);
	.send(master, tell, join(Id, Name, villager)).

