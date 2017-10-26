# Werewolves of Miller's Hollow - Agents and Distributed AI

Repository to host AIAD project.

## Overview

Werewolves of Miller's Hollow is a board-free game. Its action revolves around negotiation and trust. Players secretly take on the roles of villagers or werewolves, and have to try to survive until the opposing team dies. The werewolves know the identity of the other werewolves, and have to play as a team to kill the villagers.

The game is divided into two phases, a daytime phase and a night phase. During the day players argue about who they believe are werewolves and why. In this sense, the negotiation consists of exchanging information, which may be true or false. At the end of this phase, players can vote for someone they believe to be a werewolf to be killed, revealing their true role. After the daytime phase comes the night phase, where werewolves can secretly choose one of the villagers to be killed. During this phase, villagers with special roles can activate their special abilities in order to try to collect any information about the werewolf identities, or kill or save other players. These special roles depend on which version of the game is being played; the most common role is that of Seer who can see the true role of another player each night.

## Objectives

In addition to the implementation of the game itself, the job is to develop players of various types (random, strategic, BDI, ...) in order to compare their performance.

## Tools

* [Jason](http://jason.sourceforge.net/wp/)
