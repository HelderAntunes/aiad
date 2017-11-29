// Agent werewolf in project WereTest.mas2j



/* Initial beliefs and rules */
bestVote([], Best, Best).
bestVote([X|Xs], CurrBest, Best):-
	betterVote(X,CurrBest) & bestVote(Xs, X, Best).
bestVote([X|Xs], CurrBest, Best):-
	not betterVote(X,CurrBest) & bestVote(Xs, CurrBest, Best).
bestVote([X|Xs], Best):- bestVote(Xs, X, Best).

betterVote(X,CurrBest):-
	suspect(role(X, Rpx), SxRpx) &
	suspect(role(CurrBest, Rpb), SbRpb) &
	betterVote(Rpx, SxRpx, Rpb, SbRpb).

betterVote(diviner, _, doctor, _).
betterVote(diviner, _, villager, _).
betterVote(doctor, _, villager, _).
betterVote(R, SxRpx, R, SbRpb) :-
	SxRpx > SbRpb.

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

+init(List) <- 
	for(.member([Name, _,_],List)){
		+suspect(role(Name,villager),1.0);
		+trust(Name,0,0,0.5);
	}
	-init(List).

+werewolf(List) <- 
	for(.member(Name,List)){
		+role(Name,werewolf)[source(master)];
		-+trust(Name,0,0,1.0);
	}.

/* 
	Phase 3
	Day Discussion
*/
+time(day, discussion) : not initialized <-
	.all_names(All);
	!discuss(day).
	
+time(day, discussion) : .my_name(Self) & not dead(Self) <-
	!discuss(day).
	
+!discuss(day) : .all_names(All) & .findall(A, .member(A, All) & not A == master & not .my_name(A) & not dead(A) & not role(A,werewolf)[source(master)], L) & bestVote(L, Best) & .print(L,Best)<-
	.broadcast(tell, role(Best, werewolf)).
	
	
/* 
	Phase 4
	Day Vote
*/

+time(day, vote) : .my_name(Self) & not dead(Self) <-
	!vote(day).
	
+!vote(day) : 
	.all_names(All) & .findall(A, .member(A, All) & not A == master & not .my_name(A) & not dead(A) & not role(A,werewolf)[source(master)], L) & bestVote(L, Best)<-
	.broadcast(tell, vote(Best)).

/* 
	Phase 5
	Day Discussion
*/
+time(night, discussion) : .my_name(Self) & not dead(Self) <-
	!discuss(night).

+!discuss(night) : .all_names(All) & .findall(A, .member(A, All) & not A == master & not .my_name(A) & not dead(A) & not role(A,werewolf)[source(master)], L) & bestVote(L, Best) & .print(L,Best) & suspect(role(Best, Role), _)<-
	?werewolf(List);
	.send(List, tell, role(Best, Role));
	.send(master, tell, role(Best, Role)).
		

/* 
	Phase 6
	Night Vote
*/

+time(night, vote) : .my_name(Self) & not dead(Self) <-
	!vote(night).
	
+!vote(night) : 
	.all_names(All) & .findall(A, .member(A, All) & not A == master & not .my_name(A) & not dead(A) & not role(A,werewolf)[source(master)], L) & bestVote(L, Best)<-
	.broadcast(tell, vote(Best)).	

	
/*
	Observation Model
*/

/*
	X == master
*/
+role(Y, Role)[source(master)] <-
	.findall([X, R], role(Y, R)[source(X)] & .my_name(Self) & not X == Self & not X == master & not X == self, BeliefList);
	for(.member([N, RR], BeliefList)){
		?trust(N, Corrects, Wrongs, Tn);
		if(RR == Role){
			-+trust(N, Corrects+1, Wrongs, (((Corrects+1)/(Corrects+1+Wrongs))-0.5)*2);
		}
		else{
			-+trust(N, Corrects, Wrongs+1, ((Corrects/(Corrects+1+Wrongs))-0.5)*2);
		}
	}.	

/*
	Day and role(Y, Rxy)[source(werewolf)]
*/
+role(Y, _)[source(X)] : 
	.my_name(Self) & not X == Self &
	not X == master & not X == self &
	role(X, werewolf)[source(master)]
	<-
	-role(Y, Rpy)[source(X)].	
	
/*
	Confirmation role(Y, Rxy)[source(master)]
	Rxy = Rpy
*/
+role(Y, Rpy)[source(X)] : 
	.my_name(Self) & not X == Self &
	not X == master & not X == self &
	role(Y, Rpy)[source(master)] &
	trust(X, Corrects, Wrongs, Tx)
	<-
	-+trust(X, Corrects+1, Wrongs, (((Corrects+1)/(Corrects+1+Wrongs))-0.5)*2).

/*
	Confirmation role(Y, Rpy)[source(master)]
	Rxy != Rpy
*/
+role(Y, Rxy)[source(X)] : 
	.my_name(Self) & not X == Self &
	not X == master & not X == self &
	role(Y, Rpy)[source(master)] &
	trust(X, Corrects, Wrongs, Tx)
	<-
	-+trust(X, Corrects, Wrongs+1, (((Corrects)/(Corrects+1+Wrongs))-0.5)*2).		

/*
	No confirmation role(Y, _)[source(master)]
	Rxy == Rpy
*/
+role(Y, Rpy)[source(X)] : 
	.my_name(Self) & not X == Self &
	not X == master & not X == self &
	trust(X, _, _, Tx) &
	suspect(role(Y, Rpy), SyRpy)
	<-
	-+suspect(role(Y, Rpy), SyRpy + (1.0 - SyRpy) * Tx).	
	
/*
	No confirmation role(Y, _)[source(master)]
	Rxy != Rpy
	Tx = Sy(Rxy)
*/
+role(Y, Rxy)[source(X)] : 
	.my_name(Self) & not X == Self &
	not X == master & not X == self &
	trust(X, _, _, Tx) &
	suspect(role(Y, Rpy), SyRpy)
	<-
	if (Tx > SyRpy) {
		-suspect(role(Y, Rpy), SyRpy);
		+suspect(role(Y, Rxy), Tx - SyRpy);
	}
	else {
		if(Tx < 0){
			-+suspect(role(Y, Rpy), SyRpy - (1.0 - SyRpy) * Tx)
		}
		else{
			-+suspect(role(Y, Rpy), SyRpy - Tx)
		}
	}.	


+!discuss(day) : .all_names(All) & .findall(A, .member(A, All) & not A == master & not .my_name(A) & not dead(A) & not role(A,werewolf)[source(master)], L) & bestVote(L, Best) & .print(L,Best)<-
	.broadcast(tell, role(Best, werewolf)).
	
+!discuss(night) : .all_names(All) & .findall(A, .member(A, All) & not A == master & not .my_name(A) & not dead(A) & not role(A,werewolf)[source(master)], L) & bestVote(L, Best) & .print(L,Best) & suspect(role(Best, Role), _)<-
	?werewolf(List);
	.send(List, tell, role(Best, Role));
	.send(master, tell, role(Best, Role)).
	
	
	
	
	
	
	
	
	
	

	
