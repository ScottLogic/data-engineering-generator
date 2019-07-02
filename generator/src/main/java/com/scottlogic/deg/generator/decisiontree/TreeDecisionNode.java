Copyright 2019 Scott Logic Ltd /
/
Licensed under the Apache License, Version 2.0 (the \"License\");/
you may not use this file except in compliance with the License./
You may obtain a copy of the License at/
/
    http://www.apache.org/licenses/LICENSE-2.0/
/
Unless required by applicable law or agreed to in writing, software/
distributed under the License is distributed on an \"AS IS\" BASIS,/
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied./
See the License for the specific language governing permissions and/
limitations under the License.
package com.scottlogic.deg.generator.decisiontree;

import com.scottlogic.deg.common.util.FlatMappingSpliterator;

import java.util.*;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class TreeDecisionNode implements DecisionNode {
    private final Collection<ConstraintNode> options;
    private final Set<NodeMarking> nodeMarkings;

    public TreeDecisionNode(ConstraintNode... options) {
        this(Collections.unmodifiableCollection(Arrays.asList(options)));
    }

    public TreeDecisionNode(Collection<ConstraintNode> options) {
        this(options, Collections.emptySet());
    }

    public TreeDecisionNode(Collection<ConstraintNode> options, Set<NodeMarking> nodeMarkings) {
        this.options = Collections.unmodifiableCollection(options);
        this.nodeMarkings = Collections.unmodifiableSet(nodeMarkings);
    }

    @Override
    public Collection<ConstraintNode> getOptions() {
        return options;
    }

    @Override
    public DecisionNode setOptions(Collection<ConstraintNode> options){
        return new TreeDecisionNode(options);
    }

    @Override
    public boolean hasMarking(NodeMarking detail) {
        return this.nodeMarkings.contains(detail);
    }

    @Override
    public DecisionNode markNode(NodeMarking marking) {
        Set<NodeMarking> newMarkings = FlatMappingSpliterator.flatMap(
                Stream.of(Collections.singleton(marking), this.nodeMarkings),
                Collection::stream)
            .collect(Collectors.toSet());
        return new TreeDecisionNode(this.options, newMarkings);
    }

    @Override
    public String toString(){
        return this.options.size() >= 5
            ? String.format("Options: %d", this.options.size())
            : String.format("Options [%d]: %s",
                this.options.size(),
                String.join(
                    " OR ",
                    this.options.stream().map(o -> o.toString()).collect(Collectors.toList())));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TreeDecisionNode that = (TreeDecisionNode) o;
        return Objects.equals(options, that.options);
    }

    @Override
    public int hashCode() {
        return Objects.hash(options);
    }

    @Override
    public DecisionNode accept(NodeVisitor visitor){
        Stream<ConstraintNode> options = getOptions().stream().map(c->c.accept(visitor));
        return visitor.visit(
            new TreeDecisionNode(
                options.collect(Collectors.toSet()),
                nodeMarkings
            )
        );
    }
}
