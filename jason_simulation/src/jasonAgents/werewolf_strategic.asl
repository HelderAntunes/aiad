// Agent werewolf in project WereTest.mas2j



/* Initial beliefs and rules */
votes([]).

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
		+role(Name,werewolf)[source(master)];
	}.

/* 
	Phase 3
	Day Discussion
*/

+time(day, discussion) : .my_name(Self) & not dead(Self) <-
	!discuss(day).
	
+!discuss(day) : 
	votes(Voters) & .findall(A, .member(A, Voters) & not dead(A), L) & .length(L, ListSize) & ListSize > 0
	<-
	.nth(math.floor(math.random(ListSize)), L, Chosen);
	.broadcast(tell, role(Chosen, werewolf)).
+!discuss(day).

	
/* 
	Phase 4
	Day Vote
*/

+time(day, vote) : .my_name(Self) & not dead(Self) <-
	!vote(day).
	
	
+!vote(day) : 
	votes(Voters) & .findall(A, .member(A, Voters) & not dead(A), L) & .length(L, ListSize) & ListSize > 0
	<-
	.nth(math.floor(math.random(ListSize)), L, Chosen);
	.broadcast(tell, vote(Chosen)).
	
+!vote(day) : 
	.all_names(All) & .findall(A, .member(A, All) & not A == master & not .my_name(A) & not dead(A) & not role(A,werewolf)[source(master)], L ) <-
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
	votes(Voters) & .findall(A, .member(A, Voters) & not dead(A), L) & .length(L, ListSize) & ListSize > 0
	<-
	.nth(math.floor(math.random(ListSize)), L, Chosen);
	?werewolf(List);
	.send(List, tell, role(Chosen, diviner));
	.send(master, tell, role(Chosen, diviner)).
+!discuss(night).
/* 
	Phase 6
	Night Vote
*/

+time(night, vote) : .my_name(Self) & not dead(Self) <-
	!vote(night).
	
+!vote(night) : 
	votes(Voters) & .findall(A, .member(A, Voters) & not dead(A), L) & .length(L, ListSize) & ListSize > 0
	<-
	.nth(math.floor(math.random(ListSize)), L, Chosen);
	.send(master, tell, vote(Chosen)).
	
+!vote(night) : 
	.all_names(All) & .findall(A, .member(A, All) & not A == master & not .my_name(A) & not dead(A) & not role(A,werewolf)[source(master)], L ) <-
	.length(L, ListSize);
	.nth(math.floor(math.random(ListSize)), L, Chosen);
	.send(master, tell, vote(Chosen)).
	
+vote(B)[source(A)]: not A == master & not .my_name(A) & not dead(A) 
	& not role(A,werewolf)[source(master)] & role(B,werewolf)[source(master)] 
	& votes(L)
	<-
	-+votes([A|L]);
	-vote(B)[source(A)].

