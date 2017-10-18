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

/* 
	Phase 3
	Day Discussion
*/

+time(day, discussion) : .my_name(Self) & not dead(Self) <-
	!discuss(day).
	
/* 
	Phase 4
	Day Vote
*/

+time(day, vote) : .my_name(Self) & not dead(Self) <-
	!vote(day).

/*
	Change here
*/
	
//Change for discussion
+!discuss(day) <- .wait(0).
	
//Change for vote selection
+!vote(day) : .all_names(All) & .findall(A, .member(A, All) & not A == master & not .my_name(A) & not dead(A), L )<-
	.length(L, ListSize);
	.nth(math.floor(math.random(ListSize)), L, Chosen);
	.broadcast(tell, vote(Chosen)).


