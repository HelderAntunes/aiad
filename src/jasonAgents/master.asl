// Agent master in project WereTest.mas2j

/* Initial beliefs and rules */

day(0).

players_range(15, 20).
werewolfs_range(2, 4).
diviners_range(1, 2).

/* Initial goals */

//!start.
!generate_player_distribution.
	
/*+createRandomVillager(NumAgents) <- 
	.print("Random villager... ", NumAgents).
	
+createRandomWerewolf(NumAgents) <- 
	.print("Random werewolf... ", NumAgents).
	
+createRandomDiviner(NumAgents) <- 
	.print("Random diviner... ", NumAgents).*/


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
	.print("Game Start!!!");
	!start_game.
	
+!invite_werewolfs : werewolfs_number(N) <-
	+temp(1);
	while(temp(I) & I <= N) {
		.concat("werewolf", I, Name);
		.create_agent(Name, "werewolf_random.asl");
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
+join(Id, Role): day(0) & not waiting_players(0) <-
	-players(OldList);
	mylib.randomName(Name);
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
	!tellWhoAreWerewolfs;
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
	
+!endVote(day) <-
	.wait(1000);
	?players_alive(Alive);
	?players(PlayerList);
	.findall([Voter, Voted], vote(Voted)[source(Voter)], VoteList);
	.length(VoteList, TotalVotes);
	.print("Total Votes: ",TotalVotes);
	if(TotalVotes >= Alive/2){
		for(.member([_,Name],VoteList)){
			if(voteCount(Name, W)){
				-+voteCount(Name,W+1);
			}else{
				+voteCount(Name,1);
			}
		}
		.findall([Count, Name],voteCount(Name,Count), CountList);
		.sort(CountList, ReversedSortedCountList);
		.reverse(ReversedSortedCountList, SortedCountList);
		
		for(.member([Count,Name],SortedCountList)){
			.member([Name, TrueName, _], PlayerList);
			.print("->", TrueName," - ", Count, " vote(s)");
		}
		.nth(0, SortedCountList, [N0, Chosen]);
		if((.length(SortedCountList, L) & L == 1) | (.nth(1, SortedCountList, [N1,_]) & N0 > N1)){
			!kill(Chosen);
		} else {
			.print("First place tie...");
		}
		
		//.print(SortedCountList);
	}else{
		.print("Not enough votes... needed ", Alive/2);
	}
	!clean_votes.
	
+!endPhase(day, vote) <-
	.broadcast(untell, time(_,_));
	.broadcast(tell, time(night,vote));
	-+time(night, vote).
	
/* 
	Phase 5
	Night Vote
*/
	
+time(night, vote) <-
	!sayNight;
	!sayPhase;
	!endVote(night);
	!endPhase(night, vote).
	
+!endVote(night) <-
	.wait(1000);
	
	// get votes
	.findall([Voter, Voted], vote(Voted)[source(Voter)], VoteList);
	.length(VoteList, TotalVotes);
	.print("Total Votes: ",TotalVotes);
	
	// count votes
	for(.member([_,Name],VoteList)) {
		if(voteCount(Name, W)){
			-+voteCount(Name,W+1);
		}else{
			+voteCount(Name,1);
		}
	}
	.findall([Count, Name],voteCount(Name,Count), CountList);
	.sort(CountList, ReversedSortedCountList);
	.reverse(ReversedSortedCountList, SortedCountList);

	// get and kill the victim
	?players(PlayerList);
	for(.member([Count,Name],SortedCountList)) {
		.member([Name, TrueName, _], PlayerList);
		.print("->", TrueName," - ", Count, " vote(s)");
	}
	.nth(0, SortedCountList, [N0, Chosen]);
	if((.length(SortedCountList, L) & L == 1) | (.nth(1, SortedCountList, [N1,_]) & N0 > N1)){
		//.print(SortedCountList, " ", N0, " ", N1);
		!kill(Chosen);
	} else {
		.print("First place tie...");
	}
	
	!clean_votes.
	
+!endPhase(night, vote) <-
	.broadcast(untell, time(_,_));
	.broadcast(tell, time(day,discussion));
	-+time(day,discussion).

/* 
	Other
*/

+!sayDay : day(X) <-
	.print("-----------------------------");
	.print("Current day: ", X).

+!sayNight : day(X) <-
	.print("-----------------------------");
	.print("Current night: ", X).
	
+!sayPhase : time(Time, Event) <-
	.print("-----------------------------");
	.print("Starting ", Time, " ", Event).
	
		
+!clean_votes <-
	.abolish(vote(_));
	.abolish(voteCount(_,_)).
	
+!kill(ThatGuy) <- 
	?players(PlayerList);
	.member([ThatGuy, ThatGuyName, Role], PlayerList);
	.print(ThatGuyName, " died");
	
	?players_alive(A);
	-+players_alive(A-1);
	if (Role == werewolf) {
		?werewolfs_number(W);
		-+werewolfs_number(W-1);
	}
	if (Role == villager) {
		?villagers_number(V);
		-+villagers_number(V-1);
		//.print(V-1);
	}
	if (Role == diviner) {
		?diviners_number(D);
		-+diviners_number(D-1);
		//.print(D-1);
	}

	.broadcast(tell, dead(ThatGuy))
	!checkWin.

+!tellWhoAreWerewolfs: true <- 
	.findall(Name, join(Name,_,werewolf) , WerewolfList);
	.send(WerewolfList, tell, werewolf(WerewolfList)).
	
+!checkWin <-
	?total_players(T);
	?werewolfs_number(W);
	?villagers_number(V);
	?diviners_number(D);
	if (W == 0) {
		.print("-----------------------------");
		.print("Villagers win!");
		.suspend;
	}
	if (T == 0) {
		.print("-----------------------------");
		.print("Werewolfs win!");
		.suspend;
	}
	.


	
