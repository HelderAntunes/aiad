// Agent diviner (BDI) in project WereTest.mas2j

/* Initial beliefs and rules */

/* Initial goals */

!start.

/* Plans */

/*
	Phase 2
	Invite players
*/

+!start <-
	.my_name(Id);
	+role(Id, diviner);
	.send(master, tell, join(Id, diviner)).

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
	!divine.

/*
	Change here
*/

///////////////////////////
// Change for discussion //
///////////////////////////

// accuse a random known werewolf
+!discuss(day) :
	.findall(Player, join(Player,werewolf) & not dead(Player), KnownWerewolfs) &
	not .empty(KnownWerewolfs)
	<-
	.length(KnownWerewolfs, NumKnownWerewolfs);
	.nth(math.floor(math.random(NumKnownWerewolfs)), KnownWerewolfs, Chosen);
	.broadcast(tell, role(Chosen, werewolf)).

//Accuse person with the less votes so far whose role is still unknown
+!discuss(day) : .setof([Num,Player], numVotes(Player,Num) & not join(Player,_) & not .my_name(Player) & not dead(Player),L)<-
	.nth(0,L,[Min,_]);
	.findall(P,.member([Min,P], L), Suspects);
	.length(Suspects, ListSize);
	.nth(math.floor(math.random(ListSize)), Suspects, Chosen);
	
	Rand = math.random(1);
	if(Rand < 0.3){
		.broadcast(tell, role(Chosen, werewolf))
	}.

// accuse a random agent with random probability
+!discuss(day) <-
	Rand = math.random(1);
	if(Rand < 0.2) {
		.all_names(All);
		.findall(A, .member(A, All) & not A == master & not .my_name(A) & not dead(A), L );
		.length(L, ListSize);
		.nth(math.floor(math.random(ListSize)), L, Chosen);
		.broadcast(tell, role(Chosen, werewolf));
	}
	.

////////////////////
// Vote selection //
////////////////////

// vote a random known werewolf
+!vote(day):
	.findall(Player, join(Player, werewolf) & not dead(Player), KnownWerewolfs) &
	not .empty(KnownWerewolfs)
	<-
	.length(KnownWerewolfs, NumKnownWerewolfs);
	.nth(math.floor(math.random(NumKnownWerewolfs)), KnownWerewolfs, Chosen);
	.broadcast(tell, vote(Chosen)).
	
// vote in person with the minimum amount of votes so far whose role is unknown
+!vote(day) : .setof([Num,Player], numVotes(Player,Num) & not join(Player,_) & not .my_name(Player) & not dead(Player),L)<-
	.nth(0,L,[Min,_]);
	.findall(P,.member([Min,P], L), Suspects);
	.length(Suspects, ListSize);
	.nth(math.floor(math.random(ListSize)), Suspects, Chosen);
	.broadcast(tell, vote(Chosen)).

// random vote in case of lack of information
+!vote(day):
	.all_names(All) &
	.findall(A, .member(A, All) & not A == master & not .my_name(A) & not dead(A), L)
	<-
	.length(L, ListSize);
	.nth(math.floor(math.random(ListSize)), L, Chosen);
	.broadcast(tell, vote(Chosen)).
	
@processOrder[atomic]	
+vote(Player) <-
	-numVotes(Player,X);
	+numVotes(Player, X + 1).

////////////////
// divination //
////////////////

// as for an unkwnown agent
+!divine:
	.all_names(All) &
	.findall(A, .member(A, All) & not join(A,_) & not A == master & not .my_name(A) & not dead(A), UnknownAgents) &
	not .empty(UnknownAgents)
	<-
	.length(UnknownAgents, NumUnknownAgents);
	.nth(math.floor(math.random(NumUnknownAgents)), UnknownAgents, Chosen);
	.send(master, askOne, join(Chosen, Role)).

+!divine.
