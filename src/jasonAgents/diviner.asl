// Agent diviner in project WereTest.mas2j

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
	+role(Id, diviner);
	.send(master, tell, join(Id, diviner)).

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

+time(night, discussion) : .my_name(Self) & not dead(Self) <-
	!divine.
	
+join(Name, Role) <- +role(Name,Role); -join(Name,Role).

/*
	Change here
*/

//Change for discussion
+!discuss(day).
	
//Change for vote selection
+!vote(day) : .findall(A, role(A,werewolf) & not dead(A), L ) & not .length(L, 0)<-
	.length(L, ListSize);
	.nth(math.floor(math.random(ListSize)), L, Chosen);
	.broadcast(tell, vote(Chosen)).
	
+!vote(day) : .all_names(All) & .findall(A, .member(A, All) & not A == master & not .my_name(A) & not dead(A), L )<-
	.length(L, ListSize);
	.nth(math.floor(math.random(ListSize)), L, Chosen);
	.broadcast(tell, vote(Chosen)).
	
//Random divination
+!divine: .all_names(All) & .findall(A, .member(A, All) & not A == master & not .my_name(A) & not dead(A) & not role(A,_) , L )<-
	.length(L, ListSize);
	.nth(math.floor(math.random(ListSize)), L, Chosen);
	.send(master, askOne, join(Chosen,Role)).
+!divine.
