// Agent villager in project WereTest.mas2j

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
	+role(Id, villager);
	.send(master, tell, join(Id, villager)).

+init(List) <-
	.my_name(Self);
	for(.member([Id, Name,_],List)){
		+idToName(Id, Name);
	}
	for(.member([Id, Name,_],List)){
		if (not Self == Id) {
			+suspect(role(Id,villager),0.0);
			+trust(Id,0,0,0.5);
		}
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
	Change here
*/

//Accuse most suspicious of if not enough knowledge random
+!discuss(day) : .setof([FC,A], suspect(role(A,werewolf),FC) & not A == master & not .my_name(A) & not dead(A), L )
		& .length(L, ListSize) & not ListSize == 0 <-
	Rand = math.random(1);
	.nth(ListSize - 1, L, [MaxFC,Chosen]);
	if(Rand < MaxFC){
		.broadcast(tell, role(Chosen, werewolf))
	}.

+!discuss(day) <-
	Rand = math.random(1);
	if(Rand < 0.2){
		.all_names(All);
		.findall(A, .member(A, All) & not A == master & not .my_name(A) & not dead(A), L );
		.length(L, ListSize);
		.nth(math.floor(math.random(ListSize)), L, Chosen);
		.broadcast(tell, role(Chosen, werewolf));
	}.

//Vote most suspicious of if not enough knowledge random
+!vote(day) : .setof([FC,A], suspect(role(A,werewolf),FC) & not A == master & not .my_name(A) & not dead(A), L )
		& .length(L, ListSize) & not ListSize == 0 <-
	.nth(ListSize - 1, L, [_,Chosen]);
	.broadcast(tell, vote(Chosen)).

+!vote(day) : .all_names(All) & .findall(A, .member(A, All) & not A == master & not .my_name(A) & not dead(A), L )<-
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
	.findall([X, R], role(Y, R)[source(X)] & not X == self & not X == master, BeliefList);
	for(.member([N, RR], BeliefList)){
		?trust(N, Corrects, Wrongs, Tn);
		if(RR == Role){
			-trust(N,_,_,_);
			+trust(N, Corrects+1, Wrongs, (((Corrects+1)/(Corrects+1+Wrongs))-0.5)*2);
		}
		else{
			-trust(N,_,_,_);
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
	-trust(X,_,_,_);
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
	-trust(N,_,_,_);
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

/*
	Value between [-1, 1]
*/
+trust(Id, Corrects, Wrongs, Value) <-
	.my_name(IdSelf);
	?idToName(IdSelf, MyName);
	?idToName(Id, Name);
	addTrust(MyName, Name, Value).

/*
	Value between [-1, 1]
*/
-trust(Id, Corrects, Wrongs, Value) <-
	.my_name(IdSelf);
	?idToName(IdSelf, MyName);
	?idToName(Id, Name);
	remTrust(MyName, Name, Value).

/*
	Value between [0, 1]
*/
+suspect(role(Id,Role), Value) <-
	.my_name(IdSelf);
	?idToName(IdSelf, MyName);
	?idToName(Id, Name);
	addSuspect(MyName, Name, Role, Value).

/*
	Value between [0, 1]
*/
-suspect(role(Id,Role), Value) <-
	.my_name(IdSelf);
	?idToName(IdSelf, MyName);
	?idToName(Id, Name);
	remSuspect(MyName, Name, Role, Value).
