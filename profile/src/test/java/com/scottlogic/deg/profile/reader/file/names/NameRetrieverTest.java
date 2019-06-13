package com.scottlogic.deg.profile.reader.file.names;


import com.scottlogic.deg.common.profile.constraints.atomic.NameConstraintTypes;
import com.scottlogic.deg.generator.utils.SetUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NameRetrieverTest {

    @InjectMocks
    private NameRetriever service;

    @Mock
    private Function<String, InputStream> mapper;

    private void mockNames(String fileName, String... names) {
        String combinedName = Stream.of(names)
            .reduce((a,b) -> String.join("\n", a, b))
            .orElseThrow(IllegalStateException::new);
        when(mapper.apply(fileName)).thenReturn(new ByteArrayInputStream(combinedName.getBytes(Charset.defaultCharset())));
    }

    private void mockFirstNames() {
        mockNames("names/firstname_male.csv", "Mark", "Paul");
        mockNames("names/firstname_female.csv", "Jolene", "Tanya");
    }

    @Test
    public void retrieveValuesFirst() {
        mockFirstNames();

        Set<String> names = setFromConstraint(NameConstraintTypes.FIRST);

        assertEquals(SetUtils.setOf("Mark", "Paul", "Jolene", "Tanya"), names);
    }

    private void mockLastNames() {
        mockNames("names/surname.csv", "Gore", "May");
    }

    @Test
    public void retrieveValuesLast() {
        mockLastNames();

        Set<String> names = setFromConstraint(NameConstraintTypes.LAST);

        assertEquals(SetUtils.setOf("Gore", "May"), names);
    }

    private void mockAllNames() {
        mockFirstNames();
        mockLastNames();
    }

    @Test
    public void retrieveValuesFull() {
        mockAllNames();

        Set<String> names = setFromConstraint(NameConstraintTypes.FULL);

        assertEquals(SetUtils.setOf("Mark Gore", "Paul Gore", "Jolene Gore", "Tanya Gore",
            "Mark May", "Paul May", "Jolene May", "Tanya May"), names);
    }

    @ParameterizedTest
    @EnumSource(NameConstraintTypes.class)
    public void testAllValuesGiveValidResult(NameConstraintTypes config) {
        switch (config) {
            case FIRST:
                mockFirstNames();
                break;
            case LAST:
                mockLastNames();
                break;
            case FULL:
                mockAllNames();
                break;
        }

        Set<String> result = setFromConstraint(config);

        assertNotNull(result);
    }

    private Set<String> setFromConstraint(NameConstraintTypes config) {
        return Stream.of(config)
            .flatMap(service)
            .collect(Collectors.toSet());
    }
}