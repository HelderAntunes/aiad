// Agent master in project WereTest.mas2j

/* Initial beliefs and rules */
day(0).

/* 
	Phase 1 
	Get options about agents types.
*/

/**
 * Agent initials:
 * RV, SV, BV: Random, Strategic, BDI villager 
 * RW, SW, BW: Random, Strategic, BDI werewolf 
 * RDi, SDi, BDi: Random, Strategic, BDI diviner 
 * RDo, SDo, BDo: Random, Strategic, BDI doctor 
 */
+createAgents(RV, SV, BV, RW, SW, BW, RDi, SDi, BDi, RDo, SDo, BDo) <-
	.print("Creating agents");
	+villagers_number(RV + SV + BV);
	+werewolfs_number(RW + SW + BW);
	+diviners_number(RDi + SDi + BDi);
	+doctors_number(RDo + SDo + BDo);
	+total_players(RV + SV + BV + RW + SW + BW + RDi + SDi + BDi + RDo + SDo + BDo);
	!invite_players.
	
/* 
	Phase 2 
	Invite players
*/
+!invite_players : not players(_)<-
	+players([]);
	!invite_villagers;
	!invite_werewolfs;
	!invite_diviners;
	!invite_doctors;
	.wait(1000);
	!invite_players.
	
+!invite_players : players(_) & not waiting_players(0) <-
	.print("Agents failed to connect. Shutting off...");
	.stopMas.
	
+!invite_players : players(_) & waiting_players(0) <-
	.print("All players joined!");
	.print("Game Start!!!");
	!start_game.
	
+!invite_villagers : villagers_number(N) <-
	?createAgents(RV, SV, BV, _, _, _, _, _, _, _, _, _);
	+temp(1);
	while(temp(I) & I <= RV) {
		.concat("villager", I, Name);
		.create_agent(Name, "villager_random.asl");
		-+temp(I+1);
	}
	-temp(_).
	
+!invite_werewolfs : werewolfs_number(N) <-
	?createAgents(_, _, _, RW, SW, BW, _, _, _, _, _, _);
	+temp(1);
	while(temp(I) & I <= RW) {
		.concat("werewolf", I, Name);
		.create_agent(Name, "werewolf_random.asl");
		-+temp(I+1);
	}
	-temp(_).

+!invite_diviners : diviners_number(N) <-
	?createAgents(_, _, _, _, _, _, RDi, SDi, BDi, _, _, _);
	+temp(1);
	while(temp(I) & I <= RDi) {
		.concat("diviner", I, Name);
		.create_agent(Name, "diviner_random.asl");
		-+temp(I+1);
	}
	-temp(_).
	
+!invite_doctors : doctors_number(N) <-
	?createAgents(_, _, _, _, _, _, _, _, _, RDo, SDo, BDo);
	+temp(1);
	while(temp(I) & I <= RDo) {
		.concat("doctor", I, Name);
		.create_agent(Name, "doctor_random.asl");
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
		if(cure(Chosen)){
			.print(Chosen, " is cured and cannot die");
		}
		else{
			//.print(SortedCountList, " ", N0, " ", N1);
			!kill(Chosen);
		}
	} else {
		.print("First place tie...");
	}
	.abolish(cure(_));
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
	if (Role == doctor) {
		?doctors_number(D0);
		-+doctors_number(D0-1);
		//.print(D0-1);
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
	if (T-W == 0) {
		.print("-----------------------------");
		.print("Werewolfs win!");
		.suspend;
	}
	.


	
