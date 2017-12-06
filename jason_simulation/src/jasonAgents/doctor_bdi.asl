// Agent diviner in project WereTest.mas2j

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
	+role(Id, doctor);
	.send(master, tell, join(Id, doctor)).

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
	!cure.

/*
	Change here
*/
// accuse the most suspicious agent
+!discuss(day) :
	.setof([FC,A], suspect(role(A,werewolf),FC) & not A == master & not .my_name(A) & not dead(A), L ) &
	.length(L, ListSize) &
	not ListSize == 0
	<-
	Rand = math.random(1);
	.nth(ListSize - 1, L, [MaxFC,Chosen]);
	if(Rand < MaxFC){
		.broadcast(tell, role(Chosen, werewolf))
	}
	.

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

// vote the most suspicious agent
+!vote(day):
	.setof([FC,A], suspect(role(A,werewolf),FC) & not A == master & not .my_name(A) & not dead(A), L) &
	.length(L, ListSize) & not ListSize == 0
	<-
	.nth(ListSize - 1, L, [_,Chosen]);
	.broadcast(tell, vote(Chosen)).

// random vote
+!vote(day):
	.all_names(All) &
	.findall(A, .member(A, All) & not A == master & not .my_name(A) & not dead(A), L)
	<-
	.length(L, ListSize);
	.nth(math.floor(math.random(ListSize)), L, Chosen);
	.broadcast(tell, vote(Chosen)).

//////////
// cure //
//////////

// cure what he thinks is more villager
+!cure(day):
	.setof([FC,A], suspect(role(A,Role),FC) & not Role == werewolf & not A == master & not .my_name(A) & not dead(A), L) &
	.length(L, ListSize) & not ListSize == 0
	<-
	.nth(ListSize-1, L, [_,Chosen]);
	.send(master, tell, cure(Chosen)).

// cure the less suspicious agent
+!cure(day):
	.setof([FC,A], suspect(role(A,werewolf),FC) & not A == master & not .my_name(A) & not dead(A), L) &
	.length(L, ListSize) & not ListSize == 0
	<-
	.nth(0, L, [_,Chosen]);
	.send(master, tell, cure(Chosen)).

// Random cure
+!cure:
	.all_names(All) &
	.findall(A, .member(A, All) & not A == master & not .my_name(A) & not dead(A), L) &
	not empty(L)
	<-
	.length(L, ListSize);
	.nth(math.floor(math.random(ListSize)), L, Chosen);
	.send(master, tell, cure(Chosen)).

/*
	Observation Model
*/

/*
	X == master
*/
+role(Y, Role)[source(master)] <-
	.findall([X, R], role(Y, R)[source(X)] & not X == self & not X == master, BeliefList);
	for(.member([N, RR], BeliefList)){
		?trust(N, Corrects, Wrongs, Tn);
		if(RR == Role){
			-trust(N, Corrects, Wrongs, Tn);
			+trust(N, Corrects+1, Wrongs, (((Corrects+1)/(Corrects+1+Wrongs))-0.5)*2);
		}
		else{
			-trust(N, Corrects, Wrongs, Tn);
			+trust(N, Corrects, Wrongs+1, ((Corrects/(Corrects+1+Wrongs))-0.5)*2);
		}
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
	-trust(N, Corrects, Wrongs, Tn);
	+trust(X, Corrects+1, Wrongs, (((Corrects+1)/(Corrects+1+Wrongs))-0.5)*2).

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
	-trust(N, Corrects, Wrongs, Tn);
	+trust(X, Corrects, Wrongs+1, (((Corrects)/(Corrects+1+Wrongs))-0.5)*2).

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
