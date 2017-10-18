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
	Phase 6
	Night Vote
*/

+time(night, vote) : .my_name(Self) & not dead(Self) <-
	!divine.

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
	
+!divine <-
	.wait(0).
