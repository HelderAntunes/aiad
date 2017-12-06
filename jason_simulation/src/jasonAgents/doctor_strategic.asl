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

//Random Vote
+!vote(day) :
	.all_names(All) & .findall(A, .member(A, All) & not A == master & not .my_name(A) & not dead(A), L) &
	not empt(L)
	<-
	.length(L, ListSize);
	.nth(math.floor(math.random(ListSize)), L, Chosen);
	.broadcast(tell, vote(Chosen)).
	
//Cure Person alive with the most votes so far already voted to be killed by a possible werewolf
+!cure: setof([NumVotes, Player], numVotes(Player, NumVotes) & role(Source, werewolf) & vote(Player)[source(Source)] & not A == master & not .my_name(A) & not dead(A), L) & not empt(L) <-
	.length(L, ListSize);
	.nth(ListSize - 1, L, [_,Chosen]);
	.send(master, tell, cure(Chosen)).

//Cure Person alive with the most votes so far
+!cure: setof([NumVotes, Player], numVotes(Player, NumVotes) & not A == master & not .my_name(A) & not dead(A), L) & not empt(L) <-
	.length(L, ListSize);
	.nth(ListSize - 1, L, [_,Chosen]);
	.send(master, tell, cure(Chosen)).

//If all fails try Random cure
+!cure:
	.all_names(All) &
	.findall(A, .member(A, All) & not A == master & not .my_name(A) & not dead(A), L) &
	not empty(L)
	<-
	.length(L, ListSize);
	.nth(math.floor(math.random(ListSize)), L, Chosen);
	.send(master, tell, cure(Chosen)).

@processOrder[atomic]	
+vote(Player) <-
	-numVotes(Player,X);
	+numVotes(Player, X + 1).
