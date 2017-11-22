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
	for(.member(Name,All)){
		+trust(Name,0,0,1.0);
	};
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
	.all_names(All) & .findall(A, .member(A, All) & not A == master & not .my_name(A) & not dead(A) & not role(A,werewolf), L ) <-
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

/*+role(Name, Werewolf)[source(self | master)] <- .print("Stupid").
& X\=master*/
/*
+role(Y, Werewolf)[source(X)] : .my_name(Self) & X\=Self  & not role(X, werewolf) <-
	+suspect([role(X, Werewolf)], [role(Y, Werewolf)]).
	
+suspect(B1, B2) <- !think(suspect(B1,B2)).

+!think(suspect(B1,B2)) <-
	for ( member(b1, B1) ) {
        eval(X, b1);
		if(X){
			X
		}
    }*/

//X diz que Y é um werewolf.
+role(Y, werewolf)[source(X)] : .my_name(Self) & not X == Self & trust(X, R, W, T) & suspect(role(X, werewolf), S)<-
		-+suspect(role(X, werewolf), S+(1-S)*T).
		
+role(Y, werewolf)[source(X)] : .my_name(Self) & not X == Self & trust(X, R, W, T)<-
		-+suspect(role(X, werewolf), T).		
		
+role(Y, R)[source(X)] : .my_name(Self) & not X == Self & trust(X, R, W, T) & suspect(role(X, R), S)<-
		-+suspect(role(Y, werewolf), S+(1-S)*T).
		
+role(Y, R)[source(X)] : .my_name(Self) & not X == Self & trust(X, R, W, T) & suspect(role(X, R2), S)<-
	if(T > S){
		-suspect(role(Y, R2), _);
		+suspect(role(Y, R), T-S);
	}
	else{
		-+suspect(role(X, R2), S-T);
	}.
		
+role(Y, R)[source(X)] : .my_name(Self) & not X == Self & trust(X, R, W, T) <-
	-+suspect(role(X, R), T).	
//FC1 + (1-FC2)*FC2

+role(Y, Role)[source(master)] <-
	.findall(X, role(Y, _)[source(X)] & .my_name(Self) & not X == Self, BeliefList);
	for(member(role(Y, R)[source(N)], BeliefList)){
		?trust(X, R, W, T)
		if(R == Role){
			-+trust(X, R+1, W, (((R+1)/(R+1+W))-0.5)*2);
		}
		else{
			-+trust(X, R, W+1, ((R/(R+1+W))-0.5)*2);
		}
	}.

	
	
	
