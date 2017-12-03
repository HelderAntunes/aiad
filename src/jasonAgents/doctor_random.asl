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
+!discuss(day) :
	.all_names(All) &
	.findall(A, .member(A, All) & not A == master & not .my_name(A) & not dead(A), L) &
	not empty(L)
	<-
	.length(L, ListSize);
	.nth(math.floor(math.random(ListSize)), L, Chosen);
	.broadcast(tell, role(Chosen, werewolf)).

//Change for vote selection
+!vote(day) :
	.all_names(All) & .findall(A, .member(A, All) & not A == master & not .my_name(A) & not dead(A), L) &
	not empt(L)
	<-
	.length(L, ListSize);
	.nth(math.floor(math.random(ListSize)), L, Chosen);
	.broadcast(tell, vote(Chosen)).

//Random divination
+!cure:
	.all_names(All) &
	.findall(A, .member(A, All) & not A == master & not .my_name(A) & not dead(A), L) &
	not empty(L)
	<-
	.length(L, ListSize);
	.nth(math.floor(math.random(ListSize)), L, Chosen);
	.send(master, tell, cure(Chosen)).
