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

+init(List) <- 
	for(.member([Name, _,_],List)){
		+suspect(role(Name,villager),0.0);
		+trust(Name,0,0,1.0);
	}
	-init(List).

+werewolf(List) <- 
	for(.member(Name,List)){
		+role(Name,werewolf);
	}
	-werewolf(List).

/* 
	Phase 3
	Day Discussion
*/
+time(day, discussion) : not initialized <-
	.all_names(All);
	!discuss(day).
	
+time(day, discussion) : .my_name(Self) & not dead(Self) <-
	!discuss(day).
	
+!discuss(day) <- .wait(0).
	
	
/* 
	Phase 4
	Day Vote
*/

+time(day, vote) : .my_name(Self) & not dead(Self) <-
	!vote(day).
	
+!vote(day) : 
	.all_names(All) & .findall(A, .member(A, All) & not A == master & not .my_name(A) & not dead(A) & not role(A,werewolf)[source(master)], L ) <-
	.length(L, ListSize);
	.nth(math.floor(math.random(ListSize)), L, Chosen);
	.broadcast(tell, vote(Chosen)).
	
/* 
	Phase 5
	Night Vote
*/

+time(night, vote) : .my_name(Self) & not dead(Self) <-
	!vote(night).
	
+!vote(night) : 
	.all_names(All) & .findall(A, .member(A, All) & not A == master & not .my_name(A) & not dead(A) & not role(A,werewolf), L ) <-
	.length(L, ListSize);
	.nth(math.floor(math.random(ListSize)), L, Chosen);
	.broadcast(tell, vote(Chosen)).
	
/*
	Observation Model
*/

/*
	X == master
*/
+role(Y, Role)[source(master)] <-
	.findall([X, R], role(Y, R)[source(X)] & .my_name(Self) & not X == Self & not X == master, BeliefList);
	.print(BeliefList);
	for(.member([N, RR], BeliefList)){
		?trust(N, Corrects, Wrongs, Tn);
		.print(N, " ", Tn);
		if(RR == Role){
			-+trust(N, Corrects+1, Wrongs, (((Corrects+1)/(Corrects+1+Wrongs))-0.5)*2);
		}
		else{
			-+trust(N, Corrects, Wrongs+1, ((Corrects/(Corrects+1+Wrongs))-0.5)*2);
		}
		.print(N, " ", Tn);
	}.	

/*
	Confirmation role(Y, Rxy)[source(master)]
	Rxy = Rpy
*/
+role(Y, Rpy)[source(X)] : 
	.my_name(Self) & not X == Self &
	not X == master &
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
	not X == master &
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
	not X == master &
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
	not X == master &
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



	
	
	
