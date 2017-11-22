// Agent villager in project WereTest.mas2j

/* Initial beliefs and rules */
suspect(role(werewolf1, werewolf), 0.5).


/* Initial goals */

!start.

/* Plans */

/* 
	Phase 2 
	Invite players
*/

+!start <- 
	.my_name(Id);
	+role(Id, villager);
	.send(master, tell, join(Id, villager)).

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
	

+role(Agent, werewolf) <- .findall(Voter, vote(Agent)[source(Voter)], VoteList);
		for(.member(Voter,VoteList)){
			if(suspect(role(Voter,werewolf),S)){
				-suspect(role(Voter,werewolf), S);
				-+suspect(role(Voter,werewolf), S - 1);
			}
			else{
				+suspect(role(Voter,werewolf), -1);
			}
		}.
		
+role(Agent,Role) <- .findall(Voter, vote(Agent)[source(Voter)], VoteList);
		for(.member(Voter,VoteList)){
			if(suspect(role(Voter,werewolf),S)){
				-suspect(role(Voter,werewolf), S);
				-+suspect(role(Voter,werewolf), S + 1);
			}
			else{
				+suspect(role(Voter,werewolf), 1);
			}
		}.

/*
	Change here
*/
	
//Change for discussion
+!discuss(day).
	
//TODO: Change for vote selection
+!vote(day) : .all_names(All) & .findall(A, .member(A, All) & not A == master & not .my_name(A) & not dead(A), L )<-
	.length(L, ListSize);
	.nth(math.floor(math.random(ListSize)), L, Chosen);
	.broadcast(tell, vote(Chosen)).

