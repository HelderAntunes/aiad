// Agent master in project WereTest.mas2j

/* Initial beliefs and rules */

day(0).

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
	+total_players(math.floor(math.random(Vf - Vi + 1) + Vi));
	?total_players(T);
	+villagers_number(math.max(T - W - D, 0)).
	
/* 
	Phase 2 
	Invite players
*/
+!invite_players : not players(_)<-
	+players([]);
	!invite_werewolfs;
	!invite_diviners;
	!invite_villagers;
	.wait(1000);
	!invite_players.
	
+!invite_players : players(_) & not waiting_players(0) <-
	.print("Agents failed to connect. Shutting off...");
	.stopMas.
	
+!invite_players : players(_) & waiting_players(0) <-
	.print("All players joined!");
	.print("Game starting in 5 seconds...");
	.wait(0000);
	.print("Game Start!!!");
	!start_game.
	
	
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
		.create_agent(Name, "villager_random.asl");
		-+temp(I+1);
	}
	-temp(_).
	
+players(List) <-
	.length(List, L);
	?total_players(T);
	-+waiting_players(T-L).	
	
@processOrder[atomic]
+join(Id, Name, Role): day(0) & not waiting_players(0) <-
	-players(OldList);
	.concat(OldList, [[Id, Name, Role]], NewList);
	+players(NewList);
	.print(Name, " has joined the game.").
	

	
	
/* 
	Phase 3
	Day Discussion
*/

+!start_game <-
	?total_players(N);
	+players_alive(N);
	.broadcast(tell, time(day, discussion));
	-+time(day, discussion).
	
	
+time(day, discussion) <-
	?day(D);
	-+day(D+1);
	!sayDay;
	!sayPhase;
	!endPhase(day, discussion).
	
	
+!endPhase(day, discussion) <-
	.wait(5000);
	.broadcast(untell, time(_,_));
	.broadcast(tell, time(day, vote));
	-+time(day, vote).
	
/* 
	Phase 4
	Day Vote
*/
	
+time(day, vote) <-
	!sayPhase;
	!endVote(day);
	!endPhase(day, vote).

+!endPhase(day, vote) <-
	.broadcast(untell, time(_,_));
	.broadcast(tell, time(day,discussion));
	-+time(day,discussion).
	
+!endVote(day) <-
	.wait(5000);
	?players_alive(Alive)
	.findall([Voter, Voted], vote(Voted)[source(Voter)], VoteList);
	.length(VoteList, TotalVotes);
	.print(TotalVotes);
	if(TotalVotes >= Alive/2){
		for(.member([_,Name],VoteList)){
			if(voteCount(Name, W)){
				-+voteCount(Name,W+1);
			}else{
				+voteCount(Name,1);
			}
		}
		.findall([Count, Name],voteCount(Name,Count), CountList);
		.abolish(voteCount(_,_));
		if(.length(SortedCountList, L) & L == 1){
			.nth(0, CountList, [N0, Chosen]);
			!kill(Chosen);
		} else {
			.sort(CountList, ReversedSortedCountList);
			.reverse(ReversedSortedCountList, SortedCountList);
			if(.nth(0, SortedCountList, [N0, Chosen]) & .nth(1, SortedCountList, [N1,_]) & N0 > N1){
				!kill(Chosen);
			} else {
				.print("First place tie...");
			}
		}
		//.print(SortedCountList);
	}else{
		.print("Not enough votes...");
	}
	.print("test");
	!clean_votes.

	
	
/* 
	Other
*/

+!sayDay : day(X) <-
	.print("Current day: ", X).
	
+!sayPhase : time(Time, Event) <-
	.print("Starting ", Time, " ", Event).
	
		
+!clean_votes <-
	.abolish(vote(_)).
	
+!kill(ThatGuy) <- 
	.print(ThatGuy, " died")
	.broadcast(tell, dead(ThatGuy)).
