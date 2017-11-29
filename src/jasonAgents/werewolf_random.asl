// Agent werewolf in project WereTest.mas2j



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
	+role(Id, werewolf);
	.send(master, tell, join(Id, werewolf)).


+werewolf(List) <- 
	for(.member(Name,List)){
		+role(Name,werewolf);
	}
	-werewolf(List).

/* 
	Phase 3
	Day Discussion
*/

+time(day, discussion) : .my_name(Self) & not dead(Self) <-
	!discuss(day).
	
+!discuss(day) : .all_names(All) & .findall(A, .member(A, All) & not A == master & not .my_name(A) & not dead(A) & not role(A,werewolf)[source(master)], L ) <-
			.length(L, ListSize);
			.nth(math.floor(math.random(ListSize)), L, Chosen);
			.broadcast(tell, role(Chosen, werewolf)).
	
/* 
	Phase 4
	Day Vote
*/

+time(day, vote) : .my_name(Self) & not dead(Self) <-
	!vote(day).
	
+!vote(day) : 
	.all_names(All) & .findall(A, .member(A, All) & not A == master & not .my_name(A) & not dead(A) & not role(A,werewolf), L ) <-
	.length(L, ListSize);
	.nth(math.floor(math.random(ListSize)), L, Chosen);
	.broadcast(tell, vote(Chosen)).

/* 
	Phase 5
	Night Vote
*/

+time(night, discussion) : .my_name(Self) & not dead(Self) <-
	!discuss(night).
	
+!discuss(night) : 
	.all_names(All) & .findall(A, .member(A, All) & not A == master & not .my_name(A) & not dead(A) & not role(A,werewolf), L ) <-
	.length(L, ListSize);
	.nth(math.floor(math.random(ListSize)), L, Chosen);
	.nth(math.floor(math.random(3)), [villager, doctor, diviner], Role);
	?werewolf(List);
	.send(List, tell, role(Chosen, Role));
	.send(master, tell, role(Chosen, Role)).
	
/* 
	Phase 6
	Night Vote
*/

+time(night, vote) : .my_name(Self) & not dead(Self) <-
	!vote(night).
	
+!vote(night) : 
	.all_names(All) & .findall(A, .member(A, All) & not A == master & not .my_name(A) & not dead(A) & not role(A,werewolf), L ) <-
	.length(L, ListSize);
	.nth(math.floor(math.random(ListSize)), L, Chosen);
	.send(master, tell, vote(Chosen)).
	


