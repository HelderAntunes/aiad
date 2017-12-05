// Agent diviner (BDI) in project WereTest.mas2j

/* Initial beliefs and rules */

playersInfo([]).

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
		+suspect(role(Name,villager),0.0);
		+trust(Name,0,0,0.5);
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
	playersInfo(PlayersInfo) &
	.findall(Player, .member([Player,werewolf], PlayersInfo) & not dead(Player), KnownWerewolfs) &
	not .empty(KnownWerewolfs)
	<-
	.length(KnownWerewolfs, NumKnownWerewolfs);
	.nth(math.floor(math.random(NumKnownWerewolfs)), KnownWerewolfs, Chosen);
	.broadcast(tell, role(Chosen, werewolf)).

// accuse a random agent whit random probability
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

///////////////////////////////
// Change for vote selection //
///////////////////////////////

// vote a random known werewolf
+!vote(day):
	playersInfo(PlayersInfo) &
	.findall(Player, .member([Player,werewolf], PlayersInfo) & not dead(Player), KnownWerewolfs) &
	not .empty(KnownWerewolfs)
	<-
	.length(KnownWerewolfs, NumKnownWerewolfs);
	.nth(math.floor(math.random(NumKnownWerewolfs)), KnownWerewolfs, Chosen);
	.broadcast(tell, vote(Chosen)).

// random vote
+!vote(day):
	.all_names(All) &
	.findall(A, .member(A, All) & not A == master & not .my_name(A) & not dead(A), L)
	<-
	.length(L, ListSize);
	.nth(math.floor(math.random(ListSize)), L, Chosen);
	.broadcast(tell, vote(Chosen)).

////////////////
// divination //
////////////////

// as for an unkwnown agent
+!divine:
	playersInfo(PlayersInfo) &
	.all_names(All) &
	.findall(A, .member(A, All) & not .member([A,_], PlayersInfo) & not A == master & not .my_name(A) & not dead(A), UnknownAgents) &
	not .empty(UnknownAgents)
	<-
	.length(UnknownAgents, NumUnknownAgents);
	.nth(math.floor(math.random(NumUnknownAgents)), UnknownAgents, Chosen);
	.send(master, askOne, join(Chosen, RoleAsked), join(Chosen, RoleReceived));
	.concat([[Chosen, RoleReceived]], PlayersInfo, NewPlayersInfo);
	-+playersInfo(NewPlayersInfo).

+!divine.
