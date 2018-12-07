# Reductive tree processing

Reductive tree processing is the term given to processing a tree of constraints and decisions (formed from a profile of rules and constraints). The process is a backtracking solution which performs the following steps until no further processing can be performed.

* Determine some constant value for a field and store it for future use
* Reduce the size of the tree, by removing anything that contradicts this value

If the tree has been invalidated (see below), then the process will revert back to the previous state and pick a different constant value and continue. If the tree has been reduced to a single node, then data can be emitted.

## Emitting data
If there is only one node in the tree, then either every other field has been 'fixed' to a constant value **or all other fields are unconstraint??**. Eitherway the process can use the produce rows of data repeating the fixed values for the fixed fields for every field that has not been fixed yet.

For example the state:

| | Field 1 | Field 2 | Field 3 |
| ---- | ---- | ---- | ---- |
| Constraint | > 10 & < 20 | in [A, B, C] | [ab]{2} |
| State | Fixed | Fixed | Unfixed |
| Values | 10 | A | aa, ab, ba, bb |

Once all appropriate values for the last remaining un-fixed fields have been emitted, the process will proceed to switching to the next possible value for the previously fixed-field, until all possible options have been exhausted.

### Combination strategies
The process effecively circumvents the process of combining value sources to produce rows, as a row has effectively been produced in memory up-to the un-fixed fields.

#### Modifications
Currently the process will emit a row for every appropriate value in the set of un-fixed fields. In the above example, 4 rows would be emitted. An alternative approach to combination strategies could be used here to ensure that all values for the set of un-fixed fields are emitted, but not on every iteration. 

In the above example this could mean that aa, ab, ba, bb are emitted given the state above, but only the first appropriate value for Field 3 (aa) would be emitted when Field 1 and Field 2 change in future. The reverse could also be true, to emit all remaining non-emitted values for Field 3 when Field 1 & Field 2 have been exhausted, but this is a more difficult to determine the situation.

## The problem with interesting values
The facility of producing interesting values at present emits a values based on the boundary conditions for the constraints. There may be decisions which vary the output based on different values. As such the mode needs to emit these values also, otherwise the process will not miss possible rows of data from the output.

## Picking a constant value
The first challenge for the process is to calculate the field to fix a value for, there are a number of different approaches, each will emit all the data for the given profiles, however can emit data at different rates in their entirety or for particular fields.

The current strategies are:
* Pick the field with the least variance (`InitialFixFieldStrategy`)
* Pick the field least impacted by other fields (`HierarchicalDependencyFixFieldStrategy`)

The process will use the generally understood most efficient strategy, however it may be important to vary this given the use case of the tool, e.g.:
* "Give me all possible values in field X" (i.e. vary this field the most, therefore defer fixing it to the latest possible point)
* "Treat field Y as irrelevant" (i.e. dont spend any time varying this, therefore eagerly fix a value and concentrate on the remaining set of fields)

The two above demonstrate an important relationship with the data that is emitted and WHEN values are emitted. Neither of the above attempt to reduce backtracking, which is better improved when the fields and their interrelationships are analysed, see `HierarchicalDependencyFixFieldStrategy`.

#### Strategy A: Prefer variance in field 2
| Field 1 (1000 possible values) | Field 2 (2000 possible values) |
| ---- | ---- |
| value 1 | value 1 |
| value 1 | value 2 |
| ... | ... |
| value 1 | value 2000 |
| value 2 | value 1 |

#### Strategy B: Prefer variance in field 1
| Field 1 (1000 possible values) | Field 2 (2000 possible values) |
| ---- | ---- |
| value 1 | value 1 |
| value 2 | value 1 |
| ... | ... |
| value 1000 | value 1 |
| value 1 | value 2 |

The following can be observed:

| Strategy A | Strategy B |
| ---- | ---- |
| Emits all values of Field 2 soner | Emits all values of Field 1 sooner |
| Emits **2000** rows before Field 1 varies | Emits **1000** rows before Field 2 varies |

Arguably Strategy B is better here, as it emits more values for each field quicker than Strategy B, however it depends on whether the variance on a particular field is of greater importance.

## Tree reduction & Invalidation
Each time we fix a value for a field, we can adapt the tree in to a simpler, smaller, tree. The process of reduction removes any constraints that conflict with the value fixed for the last fixed field. As the constraints are removed they leave the valid constraints (which may be equivalent or unrelated to the field that has been fixed).

Crucially as the tree is reduced, the validty of the tree must be enforced. A tree is deemed invalid if:
* A decision has all options removed from it

In this case the process will abort this iteration and back-track.

If a decision is left with only one option after reduction, the tree is still valid. The tree can also be simplified by way of hoisting the atomic constraints to the parent constraint, and removing the then empty decision. This process of simplification further reduces the size and complexity of the tree.

## Visualisation
The process supports visualisation of each tree as its reduced through this process, a tree can be emitted to disk as the `visualise` method would. An edition of the tree is serialised whenever it is confirmed to still be valid and has been reduced further as such you do NOT see a tree for the last field, as its never fixed.

The trees are emitted to a directory, which must be created manually, under the working directory called `reductive-walker`. Within this directory files will be emitted with the the name `Reduced-tree-nnn.gv` where _nnn_ is the iteration number.

To turn this mode on, use the `ReductiveIterationVisualiser` in the `ReductiveDecisionTreeWalker`, it can be changed in the `RuntimeDecisionTreeWalkerFactory`.