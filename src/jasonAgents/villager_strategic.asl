// Agent villager in project WereTest.mas2j

/* Initial beliefs and rules */


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
	.send(master, tell, join(Id, villager)).
	
+init(List) <- 
	for(.member([Name, _,_],List)){
		+numVotes(Name, 0);
	}
	-init(List).

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
	Phase 5
	Night Discussion
*/

+time(night, vote) : .my_name(Self) & not dead(Self) <-
	.findall(P,vote(P), VotedList);
	for(.member(Player,VotedList)){
		.findall(S,vote(Player)[source(S)],L);
		.length(L, ListSize);
		-numVotes(Player,X);
		+numVotes(Player, ListSize);
	}.

/*
	Change here
*/	
//Change for discussion
+!discuss(day) : .setof([Num,Player], numVotes(Player,Num) & not .my_name(Player) & not dead(Player),L)<-
	.nth(0,L,[Min,_]);
	.findall(P,.member([Min,P], L), Suspects);
	.length(Suspects, ListSize);
	.nth(math.floor(math.random(ListSize)), Suspects, Chosen);
	
	Rand = math.random(1);
	if(Rand < 0.3){
		.broadcast(tell, role(Chosen, werewolf))
	}.

	
//Change for vote selection
+!vote(day) : .setof([Num,Player], numVotes(Player,Num) & not .my_name(Player) & not dead(Player),L)<-
	.nth(0,L,[Min,_]);
	.findall(P,.member([Min,P], L), Suspects);
	.length(Suspects, ListSize);
	.nth(math.floor(math.random(ListSize)), Suspects, Chosen);
	.broadcast(tell, vote(Chosen)).


