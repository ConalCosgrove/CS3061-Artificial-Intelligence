%defining arc predicates

%Here we set the cost of any child node equal to the parent's cost plus the heuristic value

arc([Parent, NCost, _], Seed, Target, [Child, ChildCost, H]) :- Child is Parent * Seed, ChildCost is 1 + NCost, h(Child, H, Target).
arc([Parent, NCost, _], Seed, Target, [Child, ChildCost, H]) :- Child is Parent * Seed + 1, ChildCost is 2 + NCost, h(Child, H, Target).



goal(Node, Target) :- 0 is Node mod Target.

%search function adapted to recursively perform best first (A*) search
search(Nodes, _, Target, [Node, Cost]) :- minSort(Nodes, [[Node, Cost, _]|_]), goal(Node, Target).
search(Nodes, Seed, Target, F) :- minSort(Nodes, [Node|FRest]),
                                  setof(NewNode, arc(Node, Seed, Target, NewNode), FNode),
                                  add-to-frontier(FNode, FRest, FNew),
                                  search(FNew, Seed, Target, F).



%Setting up add to frontier 

add-to-frontier(OldFrontier,NewNodes,NewFrontier) :- append(OldFrontier,NewNodes,NewFrontier).

%Sorting frontier to put least expensive node at head 
minSort([Head|Tail], Result) :- sort(Head, [], Tail, Result).
sort(Head, S, [], [Head|S]).
sort(C, S, [Head|Tail], Result) :- lessthan(C, Head), !, sort(C, [Head|S], Tail, Result);
                              sort(Head, [C|S], Tail, Result).

%Less than predicate, dictates how we check if one node's cost is lower than the other
%According to F(Node) = Cost(Node) + H(Node)
lessthan([_, Cost1, H1], [_, Cost2, H2]) :- F1 is Cost1 + H1, F2 is Cost2 + H2,
                                            F1 =< F2.

%Setting the h value of a node. If it is a goal node, it's heuristic value is 0, 
%otherwise it is the inverse of itself

h(Node, HeuristicVal, Target) :- goal(Node, Target), !, HeuristicVal is 0;
                        HeuristicVal is 1 / Node.

astar(Start, Seed, Target, Found) :- search([[Start, 0, 0]], Seed, Target, Found).

