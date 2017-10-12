// Agent master in project WereTest.mas2j

/* Initial beliefs and rules */

time(0,day).
players([]).

players_range(15, 20).
werewolfs_range(2, 5).
diviners_range(1, 2).

/* Initial goals */

//!start.
!generate_player_distribution.

/* Plans */

/* 
	Phase 1 
	Generate random distribution of players
*/

+!generate_player_distribution <- 
	!generate_werewolfs;
	!generate_diviners;
	//Always Last
	!generate_villagers;
	!invite_players.

+!generate_werewolfs <- 
	?werewolfs_range(Wi, Wf);
	+werewolfs_number(math.floor(math.random(Wf - Wi + 1) + Wi)).
	
+!generate_diviners <- 
	?diviners_range(Di, Df);
	+diviners_number(math.floor(math.random(Df - Di + 1) + Di)).
	
+!generate_villagers <- 
	?players_range(Vi, Vf);
	?werewolfs_number(W);
	?diviners_number(D);
	+villagers_number(math.max(math.floor(math.random(Vf - Vi + 1) + Vi) - W - D, 0)).
	
/* 
	Phase 2 
	Invite players
*/
+!invite_players <-	
	!invite_werewolfs;
	!invite_diviners;
	!invite_villagers.
	
	
+!invite_werewolfs : werewolfs_number(N) <-
	+temp(1);
	while(temp(I) & I <= N) {
		.concat("werewolf", I, Name);
		.create_agent(Name, "werewolf.asl");
		-+temp(I+1);
	}
	-temp(_).

+!invite_diviners : diviners_number(N) <-
	+temp(1);
	while(temp(I) & I <= N) {
		.concat("diviner", I, Name);
		.create_agent(Name, "diviner.asl");
		-+temp(I+1);
	}
	-temp(_).
	
+!invite_villagers : villagers_number(N) <-
	+temp(1);
	while(temp(I) & I <= N) {
		.concat("villager", I, Name);
		.create_agent(Name, "villager.asl");
		-+temp(I+1);
	}
	-temp(_).
	
+join(Id, Name, Role): time(0,_) <-
	-players(List);
	.concat(List, [[Id, Name, Role]], NewList);
	+players(NewList);
	.print(Name, " has joined the game.").
	
/* 
	Phase 3
	TODO
*/
