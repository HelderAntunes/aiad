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
	+role(Id, doctor);
	.send(master, tell, join(Id, doctor)).

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
	Night Vote
*/

+time(night, vote) : .my_name(Self) & not dead(Self) <-
	!cure.

/*
	Change here
*/

//Change for discussion
+!discuss(day) <- .wait(0).
	
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
+!cure: .all_names(All) & .findall(A, .member(A, All) & not A == master & not .my_name(A) & not dead(A), L )<-
	.length(L, ListSize);
	.nth(math.floor(math.random(ListSize)), L, Chosen);
	.send(master, tell, cure(Chosen)).
