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
	updateEventPanelEnv("All players joined!", "ORANGE");
	updateEventPanelEnv("Game Start!!!\n\n", "ORANGE");
	!start_game.

+!invite_villagers : villagers_number(N) <-
	?createAgents(RV, SV, BV, _, _, _, _, _, _, _, _, _);
	+temp(1);
	while(temp(I) & I <= RV) {
		.concat("villager", I, Name);
		.create_agent(Name, "villager_random.asl");
		-+temp(I+1);
	}
	-+temp(1);
	while(temp(I) & I <= SV) {
		.concat("villager", I, Name);
		.create_agent(Name, "villager_strategic.asl");
		-+temp(I+1);
	}
	-+temp(1);
	while(temp(I) & I <= BV) {
		.concat("villager", I, Name);
		.create_agent(Name, "villager_bdi.asl");
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
	while(temp(I) & I <= RW + SW) {
		.concat("werewolf", I, Name);
		.create_agent(Name, "werewolf_strategic.asl");
		-+temp(I+1);
	}
	while(temp(I) & I <= RW + SW + BW) {
		.concat("werewolf", I, Name);
		.create_agent(Name, "werewolf_bdi.asl");
		-+temp(I+1);
	}
	-temp(_);
	.

+!invite_diviners : diviners_number(N) <-
	?createAgents(_, _, _, _, _, _, RDi, SDi, BDi, _, _, _);
	+temp(1);
	while(temp(I) & I <= RDi) {
		.concat("diviner", I, Name);
		.create_agent(Name, "diviner_random.asl");
		-+temp(I+1);
	}
	while(temp(I) & I <= RDi + SDi) {
		.concat("diviner", I, Name);
		.create_agent(Name, "diviner_strategic.asl");
		-+temp(I+1);
	}
	while(temp(I) & I <= RDi + SDi + BDi) {
		.concat("diviner", I, Name);
		.create_agent(Name, "diviner_bdi.asl");
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
	while(temp(I) & I <= RDo + SDo) {
		.concat("doctor", I, Name);
		.create_agent(Name, "doctor_strategic.asl");
		-+temp(I+1);
	}
	while(temp(I) & I <= RDo + SDo + BDo) {
		.concat("doctor", I, Name);
		.create_agent(Name, "doctor_bdi.asl");
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
	.print(Name, " has joined the game.");
	playerJoined(Name, Role).

/*
	Phase 3
	Day Discussion
*/

+!start_game <-
	?total_players(N);
	+players_alive(N);
	?players(List);
	.broadcast(tell, init(List));
	!tellWhoAreWerewolfs;
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

+!endVote(day) <-
	.wait(1000);
	?players_alive(Alive);
	?players(PlayerList);
	.findall([Voter, Voted], vote(Voted)[source(Voter)], VoteList);
	.length(VoteList, TotalVotes);
	.concat("Total Votes: ",TotalVotes, M1);
	.print(M1);
	updateEventPanelEnv(M1);
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
			.concat("->", TrueName," - ", Count, " vote(s)", M2);
			.print(M2);
			updateEventPanelEnv(M2);
		}
		.nth(0, SortedCountList, [N0, Chosen]);
		if((.length(SortedCountList, L) & L == 1) | (.nth(1, SortedCountList, [N1,_]) & N0 > N1)){
			!kill(Chosen);
		} else {
			.print("First place tie...");
			updateEventPanelEnv("First place tie...");
		}

		//.print(SortedCountList);
	}else{
		.concat("Not enough votes... needed ", Alive/2, M3);
		.print(M3);
		updateEventPanelEnv(M3);
	}
	!clean_votes.

+!endPhase(day, vote) <-
	.broadcast(untell, time(_,_));
	.broadcast(tell, time(night,discussion));
	-+time(night, discussion).

/*
	Phase 5
	Night Vote
*/
+time(night, discussion) <-
	!sayNight;
	!sayPhase;
	!endPhase(night, discussion).


+!endPhase(night, discussion) <-
	.wait(5000);
	.broadcast(untell, time(_,_));
	.broadcast(tell, time(night, vote));
	-+time(night, vote).

+time(night, vote) <-
	!sayPhase;
	!endVote(night);
	!endPhase(night, vote).

+!endVote(night) <-
	.wait(1000);

	// get votes
	.findall([Voter, Voted], vote(Voted)[source(Voter)], VoteList);
	.length(VoteList, TotalVotes);
	.concat("Total Votes: ", TotalVotes, M1);
	.print(M1);
	updateEventPanelEnv(M1);

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
		.concat("->", TrueName," - ", Count, " vote(s)", M2);
		.print(M2);
		updateEventPanelEnv(M2);
	}
	.nth(0, SortedCountList, [N0, Chosen]);
	if((.length(SortedCountList, L) & L == 1) | (.nth(1, SortedCountList, [N1,_]) & N0 > N1)){
		if(cure(Chosen)){
			.concat(Chosen, " is cured and cannot die", M3)
			.print(M3);
			updateEventPanelEnv(M3);
		}
		else{
			//.print(SortedCountList, " ", N0, " ", N1);
			!kill(Chosen);
		}
	} else {
		.print("First place tie...");
		updateEventPanelEnv("First place tie...");
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
	.concat("\n-----------------------------\n", "Current day: ", X, "\n", Message);
	.print(Message);
	updateTimeDayEnv("Day", X);
	updateEventPanelEnv(Message).

+!sayNight : day(X) <-
	.concat("\n-----------------------------\n", "Current night: ", X, "\n", Message);
	.print(Message);
	updateTimeDayEnv("Night", X);
	updateEventPanelEnv(Message).

+!sayPhase : time(Time, Event) <-
	.concat("-----------------------------\n", "Starting ", Time, " ", Event, Message);
	.print(Message);
	updateEventDayEnv(Event);
	updateEventPanelEnv(Message).

+!clean_votes <-
	.abolish(vote(_));
	.abolish(voteCount(_,_)).

+!kill(ThatGuy) <-
	?players(PlayerList);
	.member([ThatGuy, ThatGuyName, Role], PlayerList);
	//.print(ThatGuyName, " died");
	.concat(ThatGuy, " died", Message);
	.print(Message);
	updateEventPanelEnv(Message);
	playerDied(ThatGuyName);
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

	.broadcast(tell, dead(ThatGuy));
	.broadcast(tell, role(ThatGuy, Role));
	!checkWin.

+!tellWhoAreWerewolfs: true <-
	.findall(Name, join(Name,werewolf) , WerewolfList);
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

+role(Y, Rxy)[source(X)] <-
	.concat(X, " says that ", Y, " is a ", Rxy, Message);
	.print(Message);
	updateEventPanelEnv(Message).
