<div>
    <a href="https://finos.github.io/datahelix/">
        <img src="https://finos.github.io/datahelix/docs-header.png" />
    </a>
</div>

# Table of Contents
1. [Introduction](#Introduction)

2. [Profiles](#Profiles)
    1. [Creating a Profile](#Creating-a-Profile)
    2. [Example Profile](#Example-Profile)

3. [Fields](#Fields)
    1. [Name](#fields-name)
    2. [Type](#fields-type)
    3. [Nullable](#fields-nullable)
    4. [Formatting](#fields-formatting)
    5. [Unique](#fields-unique)

4. [Data types](#Data-Types)
    1. [Integer/Decimal](#integerdecimal)
    2. [Strings](#Strings)
    3. [DateTime](#DateTime)
    4. [Time](#time)
    5. [Boolean](#boolean)
    6. [Faker](#faker)

5. [Predicate constraints](#Predicate-constraints)
    1. [Theory](#Theory)
    2. [General constraints](#General-constraints)
        1. [equalTo](#predicate-equalto)
        2. [inSet](#predicate-inset)
        3. [inMap](#predicate-inmap)
        4. [null](#predicate-null)
    3. [String constraints](#String-constraints)
        1. [matchingRegex](#predicate-matchingregex)
        2. [containingRegex](#predicate-containingregex)
        3. [ofLength](#predicate-oflength)
        4. [longerThan](#predicate-longerthan)
        5. [shorterThan](#predicate-shorterthan)
    4. [Integer/Decimal constraints](#Integer/Decimal-constraints)
        1. [greaterThan](#predicate-greaterthan)
        2. [greaterThanOrEqualTo](#predicate-greaterthanorequalto)
        3. [lessThan](#predicate-lessthan)
        4. [lessThanOrEqualTo](#predicate-lessthanorequalto)
        5. [granularTo](#predicate-granularto)
    5. [Time/DateTime constraints](#TimeDateTime-constraints)
        1. [after](#predicate-after)
        2. [afterOrAt](#predicate-afterorat)
        3. [before](#predicate-before)
        4. [beforeOrAt](#predicate-beforeorat)
        5. [granularTo](#predicate-granularto-datetime)
    6. [Dependent field constraints](#otherfield-constraints)
        1. [otherField](#predicate-otherfield)
        2. [offset](#predicate-offset)

6. [Grammatical constraints](#Grammatical-Constraints)
    1. [not](#not)
    2. [anyOf](#anyOf)
    3. [allOf](#allOf)
    4. [if](#if)

7. [Custom Generators](#custom-generator)
    1. [adding](#adding-custom-generator)
    2. [using](#using-custom-generator)
    3. [using as constraint](#using-custom-generator-as-constraint)

8. [Running a Profile](#Running-a-Profile)
    1. [Command Line Arguments](#Command-Line-Arguments)
    2. [Generation Strategies](#Generation-strategies)
        1. [Random Mode](#Random-Mode)
        2. [Full Sequential Mode](#Full-Sequential-Mode)
            1. [Combination Strategies](#Combination-Strategies)
                1. [Minimal](#Minimal)
                2. [Exhaustive](#Exhaustive)
                3. [Pinning](#Pinning)

9. [Visualising Decision Trees](#Visualising-Decision-Trees)

# Introduction

This guide outlines how to create a profile and contains information on the syntax of the DataHelix schema.

* If you are new to DataHelix, please read the [Getting Started page](GettingStarted.md)

* If you would like information on how to contribute to the project as well as a technical overview of the key concepts and structure of   the DataHelix then see the [Developer Guide](DeveloperGuide.md).

# Profiles

## Creating a Profile

This section will walk you through creating basic profiles with which you can generate data.

Profiles are JSON documents consisting of two sections: the list of fields and the constraints.

- **List of Fields** - An array of column headings is defined with unique "name" keys.
```
    "fields": [
        {
            "name": "Column 1"
        },
        {
            "name": "Column 2"
        }
    ]
```
- **List of Constraints** - an array of constraints that reduce the data in each column from the [universal set](user/SetRestrictionAndGeneration.md)
to the desired range of values. They are formatted as JSON objects. There are two types of constraints:

    - [Predicate Constraints](#Predicate-constraints) - predicates that define any given value as being
    _valid_ or _invalid_
    - [Grammatical Constraints](#Grammatical-constraints) - used to combine or modify other constraints

```
    "constraints": [
            {
              "field": "Column 1",
              "equalTo": "foo"
            },
            {
              "field": "Column 2",
              "equalTo": "bar"
            }
      ]

```

These sections are combined to form the [complete profile](#Example-Profile).

## Example Profile
```
    {
    "fields": [
        {
            "name": "Column 1",
            "type": "string"
        },
        {
            "name": "Column 2",
            "type": "integer"
        }
    ],
    "constraints": [
            {
            "field": "Column 1",
            "equalTo": "foo"
            }
        ]
    }
```
* See the [datahelix playground](https://finos.github.io/datahelix/playground/#ICAgIHsKICAgICJmaWVsZHMiOiBbCiAgICAgICAgewogICAgICAgICAgICAibmFtZSI6ICJDb2x1bW4gMSIsCiAgICAgICAgICAgICJ0eXBlIjogInN0cmluZyIKICAgICAgICB9LAogICAgICAgIHsKICAgICAgICAgICAgIm5hbWUiOiAiQ29sdW1uIDIiLAogICAgICAgICAgICAidHlwZSI6ICJpbnRlZ2VyIgogICAgICAgIH0KICAgIF0sCiAgICAiY29uc3RyYWludHMiOiBbCiAgICAgICAgICAgIHsKICAgICAgICAgICAgImZpZWxkIjogIkNvbHVtbiAxIiwKICAgICAgICAgICAgImVxdWFsVG8iOiAiZm9vIgogICAgICAgICAgICB9CiAgICAgICAgXQogICAgfQ%3D%3D) to run and edit this profile online.
* For a larger profile example see [The schema documentation](user/Schema.md)
* Further examples can be found in [the Examples folder](https://github.com/finos/datahelix/tree/master/examples)

A Beta feature has been developed for generating nested data (as a step towards support for relational data), for further information see [the Relational data documentation](RelationalData.md).

# Fields

Fields are the "slots" of data that can take values. Typical fields might be _email_address_ or _user_id_. By default, any piece of data is valid for a field. This is an example field object for the profile:

```javascript
{
    "name": "field1",
    "type": "decimal",
    "nullable": false,
    "formatting": "%.5s",
    "unique": true
}
```

Each of the field properties are outlined below:

<div id="fields-name"></div>

## `name`

Name of the field in the output which has to be unique within the `Fields` array. If generating into a CSV or database, the name is used as a column name. If generating into JSON, the name is used as the key for object properties.

This is a required property.


<div id="fields-type"></div>

## `type`

The data type of the field. See [Data types](#Data-Types) for more on how types work within DataHelix. Valid options are
* `decimal`
*  `integer`
*  `string`
*  `date`
*  `datetime`
*  `time`
*  `ISIN`
*  `SEDOL`
*  `CUSIP`
*  `RIC`
*  `firstname`
*  `lastname`
*  `fullname`
*  `boolean`
*  `faker.<method>.<type>`


 This is a required property.


<div id="fields-nullable"></div>

## `nullable`

Sets the field as nullable. When set to false it is the same as a [not](#not) [null](#predicate-null) constraint for the field.

This is an optional property of the field and defaults to false.

<div id="fields-formatting"></div>

## `formatting`

Used by output serialisers where string output is required.

For the formatting to be applied, the generated data must be applicable, and the `value` must be:

* a string recognised by Java's [`String.format` method](https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html)
* appropriate for the data type of `field`
* not `null` (formatting will not be applied for null values)

Formatting will not be applied if not applicable to the field's value.

Note that currently integer datatypes must be formatted as if they were decimals. For example to format a integer field to be used as part of a string a `value` like `"number: %.0f"` should be used. See [this](https://finos.github.io/datahelix/playground/#ewogICAgImZpZWxkcyI6IFsKICAgIHsKICAgICAgIm5hbWUiOiAiZGVjaW1hbCIsCiAgICAgICJ0eXBlIjogImRlY2ltYWwiLAogICAgICAibnVsbGFibGUiOiBmYWxzZSwKICAgICAgImZvcm1hdHRpbmciOiAioyUuMmYiLAogICAgICAidW5pcXVlIjogdHJ1ZQogICAgfSwKICAgIHsKICAgICAgIm5hbWUiOiAiYW5faW50ZWdlciIsCiAgICAgICJ0eXBlIjogImludGVnZXIiLAogICAgICAibnVsbGFibGUiOiBmYWxzZSwKICAgICAgImZvcm1hdHRpbmciOiAiJSwuMGYiCiAgICB9LAogICAgewogICAgICAibmFtZSI6ICJkYXRlMSIsCiAgICAgICJ0eXBlIjogImRhdGV0aW1lIiwKICAgICAgIm51bGxhYmxlIjogZmFsc2UsCiAgICAgICJmb3JtYXR0aW5nIjogIiV0RiIsCiAgICAgICJ1bmlxdWUiOiB0cnVlCiAgICB9LAogICAgewogICAgICAibmFtZSI6ICJkYXRlMiIsCiAgICAgICJ0eXBlIjogImRhdGV0aW1lIiwKICAgICAgIm51bGxhYmxlIjogZmFsc2UsCiAgICAgICJmb3JtYXR0aW5nIjogIklzc3VlZDogJTEkdGIgJTEkdFkiLAogICAgICAidW5pcXVlIjogdHJ1ZQogICAgfQogIF0sCiAgImNvbnN0cmFpbnRzIjogWwogICAgewogICAgICAiZmllbGQiOiAiZGVjaW1hbCIsCiAgICAgICJlcXVhbFRvIjogMTQuMQogICAgfSwKICAgIHsKICAgICAgImZpZWxkIjogImRhdGUxIiwKICAgICAgImVxdWFsVG8iOiAiMjAwMS0wMi0wM1QwNDowNTowNi4wMDciCiAgICB9LAogICAgewogICAgICAiZmllbGQiOiAiZGF0ZTIiLAogICAgICAiZXF1YWxUbyI6ICIyMDAxLTAyLTAzVDA0OjA1OjA2LjAwNyIKICAgIH0sCiAgICB7CiAgICAgICJmaWVsZCI6ICJhbl9pbnRlZ2VyIiwKICAgICAgImVxdWFsVG8iOiAxMjM0NTY3ODkwCiAgICB9CiAgXQp9) example to see another example of formatting integers as well as examples of formatting other datatypes.

This is an optional property of the field object and will default to use no formatting.

<div id="fields-unique"></div>

## `unique`

Sets the field as unique. Unique fields can not be used within [grammatical constraints](#Grammatical-Constraints).

 This is an optional property of the field object and will default to false.


# Data Types

## Integer/Decimal

Within a profile, users can specify two numeric data types: integer and decimal. Under the hood both of these data types are considered numeric from a point of generation but the integer type enforces a granularity of 1, see below for more information on granularity.

Decimals and integers have a maximum value of 1E20, and a minimum value of -1E20.

In profile files, numbers must be expressed as JSON numbers, without quotation marks.

### Numeric granularity

The granularity of a numeric field is a measure of how small the distinctions in that field can be; it is the smallest positive number of which every valid value is a multiple. For instance:

- if a numeric field has a granularity of 1, it can only be satisfied with multiples of 1; the integer data type adds this constraint by default
- if a decimal field has a granularity of 0.1, it can be satisfied by (for example) 1, 2, 1.1 or 1.2, but not 1.11 or 1.12

Granularities must be powers of ten less than or equal to zero (1, 0.1, 0.01, etc). Note that it is possible to specify these granularities in scientific format eg 1E-10, 1E-15 where the _10_ and _15_ clearly distinguish that these numbers can have up to _10_ or _15_ decimal places respectively. Granularities outside these restrictions could be potentially useful (e.g. a granularity of 2 would permit only even numbers) but are not currently supported.

Decimal fields currently default to the maximum granularity of 1E-20 (0.00000000000000000001) which means that numbers can be produced with up to 20 decimal places. This numeric granularity also dictates the smallest possible step between two numeric values, for example the next biggest decimal than _10_ is _10.00000000000000000001_. A user is able to add a `granularTo` constraint for a decimal value with coarser granularity (1, 0.1, 0.01...1E-18, 1E-19) but no finer granularity than 1E-20 is allowed.

Note that granularity concerns which values are valid, not how they're presented. If the goal is to enforce a certain number of decimal places in text output, the `formattedAs` operator is required. See: [What's the difference between formattedAs and granularTo?](user/FrequentlyAskedQuestions.md#whats-the-difference-between-formattedas-and-granularto)

## Strings

Strings are sequences of unicode characters with a maximum length of 1000 characters. Currently, only basic latin characters (unicode 002c - 007e) are supported.

## DateTime

DateTimes represent specific moments in time, and are specified in profiles through specialised strings:

```javascript
"2000-01-01T09:00:00.000"
```

The format is a subset of [ISO-8601](https://en.wikipedia.org/wiki/ISO_8601); the date and time must be fully specified as above,
with precisely 3 digits of sub-second precision, plus an optional offset specifier of "Z". All datetimes are treated as UTC.

DateTimes can be in the range `0001-01-01T00:00:00.000Z` to `9999-12-31T23:59:59.999Z`
that is **_`midnight on the 1st January 0001`_** to **_`1 millisecond to midnight on the 31 December 9999`_**.

### DateTime granularity

The granularity of a DateTime field is a measure of how small the distinctions in that field can be; it is the smallest positive unit of which every valid value is a multiple. For instance:

- if a DateTime field has a granularity of years, it can only be satisfied by dates that are complete years (e.g. `2018-01-01T00:00:00.000Z`)

Granularities must be one of the units: `millis`, `seconds`, `minutes`, `hours`, `days`, `months`, `years`.

DateTime fields currently default to the most precise granularity of milliseconds. A user is able to add a `granularTo` constraint for a DateTime value with coarser granularity (seconds, minutes...years) but no finer granularity than milliseconds is currently allowed.

Note that granularity concerns which values are valid, not how they're presented. All values will be output with the full format defined by [ISO-8601](https://en.wikipedia.org/wiki/ISO_8601), so that a value granular to years will still be output as (e.g.) `0001-01-01T00:00:00.000Z`, rather than `0001` or `0001-01-01`.

## Date
The date type can be used as a shorthand to create a [datetime](#DateTime) with a granularity and formatting of days. Dates should be specified in profiles as ```"YYYY-MM-DD"```. For example:

```javascript
"2001-01-01"
```

## Time
Time represents a specific time in a day. Currently times down to milliseconds are supported. Similarly to datetime, time is specified by a string; either ```hh:mm:ss``` or ```hh:mm:ss.ms```.

Before/after ect. constraints compare times by treating ```00:00:00``` as the starting time.

Supported granularites are ```'half_days'```, ```'hours'```, ```'minutes'```, ```'seconds'``` and ```'millis'``` with the default being ```'millis'```.

## Boolean

Users can specify boolean data types which will take the values `true` and `false`.

Currently these types are only supported with the `equalTo` and `equalToField` constraints.

## Faker

Users can invoke the [Faker](https://github.com/DiUS/java-faker/) custom data generators to create values.

All of the types supplied on the [Faker class](https://github.com/DiUS/java-faker/blob/javafaker-1.0.1/src/main/java/com/github/javafaker/Faker.java) are accessible. Methods are invoked by entering the method signature chain. For example if we want to generate job titles, then we have to find `job` class in the [Java Faker API documentation](http://dius.github.io/java-faker/apidocs/index.html). From this we can see that one of the methods we can invoke on `job` is `title`. To use this in the profile we would supply the field type as `faker.job.title` as shown in the following profile.

```
{
  "fields":      [{"name": "Job-Title", "type": "faker.job.title"}]
}
```

# Predicate constraints

## Theory

* A **predicate constraint** defines any given value as being _valid_ or _invalid_
* The **universal set** contains all values that can be generated (`null`, any string, any date, any number, etc)
* The **denotation** of a constraint is the subset of the universal set that it defines as valid

See [set restriction and generation](user/SetRestrictionAndGeneration.md) for an in depth explanation of how the constraints are merged and data generated from them.

If no constraints are defined over a field, then it can accept any member of the universal set. Each constraint added to that field progressively limits the universal set.

The [grammatical `not` constraint](#Grammatical-Constraints) inverts a constraint's denotation; in other words, it produces the complement of the constraint's denotation and the universal set.

## General constraints

<div id="predicate-equalto"></div>

### [`equalTo`](https://finos.github.io/datahelix/playground/#ewogICAgImZpZWxkcyI6IFsKICAgIHsKICAgICAgIm5hbWUiOiAiZGF0ZSIsCiAgICAgICJ0eXBlIjogImRhdGV0aW1lIiwKICAgICAgIm51bGxhYmxlIjogZmFsc2UKICAgIH0KICBdLAogICJjb25zdHJhaW50cyI6IFsKICAgIHsKICAgICAgImZpZWxkIjogImRhdGUiLAogICAgICAiZXF1YWxUbyI6ICIyMDAxLTAyLTAzVDA0OjA1OjA2LjAwNyIKICAgIH0KICBdCn0%3D) _(field, value)_  

```javascript
{ "field": "type", "equalTo": "X_092" }
OR
{ "field": "type", "equalTo": 23 }
OR
{ "field": "type", "equalTo": "2001-02-03T04:05:06.007" }
OR
{ "field": "type", "equalTo": "03:02:59" }
OR
{ "field": "type", "equalTo": true }
```

Is satisfied if `field`'s value is equal to `value`

<div id="predicate-inset"></div>

### [`inSet`](https://finos.github.io/datahelix/playground/#ewogICAgImZpZWxkcyI6IFsKICAgIHsKICAgICAgIm5hbWUiOiAid2Vla2RheXMiLAogICAgICAidHlwZSI6ICJzdHJpbmciLAogICAgICAibnVsbGFibGUiOiBmYWxzZQogICAgfQogIF0sCiAgImNvbnN0cmFpbnRzIjogWwogICAgewogICAgICAiZmllbGQiOiAid2Vla2RheXMiLAogICAgICAiaW5TZXQiOiBbCiAgICAgICAgIk1vbmRheSIsCiAgICAgICAgIlR1ZXNkYXkiLAogICAgICAgICJXZWRuZXNkYXkiLAogICAgICAgICJUaHVyc2RheSIsCiAgICAgICAgIkZyaWRheSIKICAgICAgXQogICAgfQogIF0KfQ%3D%3D) _(field, values)_

```javascript
{ "field": "type", "inSet": [ "X_092", "2001-02-03T04:05:06.007" ] }
```

Is satisfied if `field`'s value is in the set `values`

Alternatively, sets can be populated from files.

```javascript
{ "field": "country", "inSet": "countries.csv" }
```

Populates a set from the new-line delimited file (with suffix `.csv`), where each line represents a string value to load.
The file should be location in the same directory as the jar, or in the directory explicitly specified using the command line argument `--set-from-file-directory`, and the name should match the `value` with `.csv` appended.
Alternatively an absolute path can be used which does not have any relation to the jar location.
In the above example, this would be `countries.csv`.

Example `countries.csv` excerpt:
```javascript
...
England
Wales
Scotland
...
```

Additionally, weights can be included in the source file, which will then weight each element proportionally to its weight.
Example `countries_weighted.csv` excerpt:
```javascript
...
England, 2
Wales, 1
Scotland, 3
...
```

After loading the set from the file, this constraint behaves identically to the [inSet](#predicate-inset) constraint. This includes its behaviour when negated. See [the inSet example](https://github.com/finos/datahelix/tree/master/examples/inSet) for an example showing the `inSet` constraint being used with a file.

<div id="predicate-inmap"></div>

### [`inMap`](https://github.com/finos/datahelix/tree/master/examples/inMap) _(field, file, key)_

```javascript
{
    "field": "country",
    "inMap": "countries.csv",
    "key": "Country"
}
```

Is satisfied if `field`'s value is in the map with the key `Country`.

For each field using the same map when one value is picked from the map all fields will use the same row.

It populates the map from a new-line delimited file (with suffix `.csv`), where each line represents a value to load. A header is required in the file to identify which column is related to a key.

The file should be location in the same directory as the jar, or in the directory explicitly specified using the command line argument `--set-from-file-directory`, and the name should match the `value` with `.csv` appended.
Alternatively an absolute path can be used which does not have any relation to the jar location.
In the above example, this would be `countries.csv`.

Example `countries.csv` excerpt:
```javascript
Country, Capital
England, London
Wales, Cardiff
Scotland, Edinburgh
...
```
<div id="predicate-null"></div>

### [`isNull`](https://finos.github.io/datahelix/playground/#ewogICJmaWVsZHMiOiBbCiAgICB7Im5hbWUiOiAiZmllbGQxIiwgInR5cGUiOiAiYm9vbGVhbiIsICJudWxsYWJsZSI6IHRydWV9LAogICAgeyJuYW1lIjogImZpZWxkMiIsICJ0eXBlIjogImJvb2xlYW4iLCAibnVsbGFibGUiOiB0cnVlfQogIF0sCiAgImNvbnN0cmFpbnRzIjogWwogICAgeyJmaWVsZCI6ICJmaWVsZDEiLCAiaXNOdWxsIjogdHJ1ZX0sCiAgICB7ImZpZWxkIjogImZpZWxkMiIsICJpc051bGwiOiBmYWxzZX0KICBdCn0%3D) _(field)_

```javascript
{ "field": "price", "isNull" : true }
```

Is satisfied if `field` is null or absent.


## String constraints

<div id="predicate-matchingregex"></div>

### [`matchingRegex`](https://finos.github.io/datahelix/playground/#ewogICAgImZpZWxkcyI6IFsKICAgIHsKICAgICAgIm5hbWUiOiAiZW1haWxfYWRkcmVzcyIsCiAgICAgICJ0eXBlIjogInN0cmluZyIKICAgIH0KICBdLAogICJjb25zdHJhaW50cyI6IFsKICAgIHsKICAgICAgImZpZWxkIjogImVtYWlsX2FkZHJlc3MiLAogICAgICAibWF0Y2hpbmdSZWdleCI6ICJeW2EtejAtOV0rXFxAW2EtejAtOV0rXFwuY28obXxcXC51aykkIgogICAgfQogIF0KfQ%3D%3D) _(field, value)_

```javascript
{ "field": "name", "matchingRegex": "[a-z]{0, 10}" }
```

Is satisfied if `field` is a string matching the regular expression expressed in `value`. The regular expression must match the entire string in `field`, start and end anchors `^` & `$` are ignored.

The following non-capturing groups are unsupported:
- Negative look ahead/behind, e.g. `(?!xxx)` and `(?<!xxx)`
- Positive look ahead/behind, e.g. `(?=xxx)` and `(?<=xxx)`

<div id="predicate-containingregex"></div>

### [`containingRegex`](https://finos.github.io/datahelix/playground/#ewogICAgImZpZWxkcyI6IFsKICAgIHsKICAgICAgIm5hbWUiOiAiRG9nIiwKICAgICAgInR5cGUiOiAic3RyaW5nIgogICAgfQogIF0sCiAgImNvbnN0cmFpbnRzIjogWwogICAgewogICAgICAiZmllbGQiOiAiRG9nIiwKICAgICAgImNvbnRhaW5pbmdSZWdleCI6ICJEb2ciCiAgICB9LAogICAgewogICAgICAiZmllbGQiOiAiRG9nIiwKICAgICAgInNob3J0ZXJUaGFuIjogNgogICAgfQogIF0KfQ%3D%3D) _(field, value)_

```javascript
{ "field": "name", "containingRegex": "[a-z]{0, 10}" }
```

Is satisfied if `field` is a string containing the regular expression expressed in `value`. Using both start and end anchors `^` & `$` make the constraint behave like `matchingRegex`.

The following non-capturing groups are unsupported:
- Negative look ahead/behind, e.g. `(?!xxx)` and `(?<!xxx)`
- Positive look ahead/behind, e.g. `(?=xxx)` and `(?<=xxx)`

<div id="predicate-oflength"></div>

### [`ofLength`](https://finos.github.io/datahelix/playground/#ewogICAgImZpZWxkcyI6IFsKICAgIHsKICAgICAgIm5hbWUiOiAiZmllbGQiLAogICAgICAidHlwZSI6ICJzdHJpbmciCiAgICB9CiAgXSwKICAiY29uc3RyYWludHMiOiBbCgogICAgewogICAgICAiZmllbGQiOiAiZmllbGQiLAogICAgICAib2ZMZW5ndGgiOiA1CiAgICB9CiAgXQp9) _(field, value)_

```javascript
{ "field": "name", "ofLength": 5 }
```

Is satisfied if `field` is a string whose length exactly matches `value`, must be a whole number between `0` and `1000`.

<div id="predicate-longerthan"></div>

### [`longerThan`](https://finos.github.io/datahelix/playground/#ewogICAgImZpZWxkcyI6IFsKICAgIHsKICAgICAgIm5hbWUiOiAiZmllbGQiLAogICAgICAidHlwZSI6ICJzdHJpbmciCiAgICB9CiAgXSwKICAiY29uc3RyYWludHMiOiBbCgogICAgewogICAgICAiZmllbGQiOiAiZmllbGQiLAogICAgICAibG9uZ2VyVGhhbiI6IDUKICAgIH0KICBdCn0%3D) _(field, value)_

```javascript
{ "field": "name", "longerThan": 3 }
```

Is satisfied if `field` is a string with length greater than `value`, must be a whole number between `-1` and `999`.

<div id="predicate-shorterthan"></div>

### [`shorterThan`](https://finos.github.io/datahelix/playground/#ewogICAgImZpZWxkcyI6IFsKICAgIHsKICAgICAgIm5hbWUiOiAiZmllbGQiLAogICAgICAidHlwZSI6ICJzdHJpbmciCiAgICB9CiAgXSwKICAiY29uc3RyYWludHMiOiBbCgogICAgewogICAgICAiZmllbGQiOiAiZmllbGQiLAogICAgICAic2hvcnRlclRoYW4iOiA1CiAgICB9CiAgXQp9) _(field, value)_

```javascript
{ "field": "name", "shorterThan": 3 }
```

Is satisfied if `field` is a string with length less than `value`, must be a whole number between `1` and `1001`.   

## Integer/Decimal constraints

<div id="predicate-greaterthan"></div>

### [`greaterThan`](https://finos.github.io/datahelix/playground/#ewogICAgImZpZWxkcyI6IFsKICAgIHsKICAgICAgIm5hbWUiOiAiZmllbGQiLAogICAgICAidHlwZSI6ICJpbnRlZ2VyIgogICAgfQogIF0sCiAgImNvbnN0cmFpbnRzIjogWwoKICAgIHsKICAgICAgImZpZWxkIjogImZpZWxkIiwKICAgICAgImdyZWF0ZXJUaGFuIjogMwogICAgfQogIF0KfQ%3D%3D) _(field, value)_

```javascript
{ "field": "price", "greaterThan": 0 }
```

Is satisfied if `field` is a number greater than `value`.

<div id="predicate-greaterthanorequalto"></div>

### [`greaterThanOrEqualTo`](https://finos.github.io/datahelix/playground/#ewogICAgImZpZWxkcyI6IFsKICAgIHsKICAgICAgIm5hbWUiOiAiZmllbGQiLAogICAgICAidHlwZSI6ICJpbnRlZ2VyIgogICAgfQogIF0sCiAgImNvbnN0cmFpbnRzIjogWwoKICAgIHsKICAgICAgImZpZWxkIjogImZpZWxkIiwKICAgICAgImdyZWF0ZXJUaGFuT3JFcXVhbFRvIjogNQogICAgfQogIF0KfQ%3D%3D) _(field, value)_

```javascript
{ "field": "price", "greaterThanOrEqualTo": 0 }
```

Is satisfied if `field` is a number greater than or equal to `value`.

<div id="predicate-lessthan"></div>

### [`lessThan`](https://finos.github.io/datahelix/playground/#ewogICAgImZpZWxkcyI6IFsKICAgIHsKICAgICAgIm5hbWUiOiAiZmllbGQiLAogICAgICAidHlwZSI6ICJpbnRlZ2VyIgogICAgfQogIF0sCiAgImNvbnN0cmFpbnRzIjogWwoKICAgIHsKICAgICAgImZpZWxkIjogImZpZWxkIiwKICAgICAgImxlc3NUaGFuIjogNQogICAgfQogIF0KfQ%3D%3D) _(field, value)_

```javascript
{ "field": "price", "lessThan": 0 }
```

Is satisfied if `field` is a number less than `value`.

<div id="predicate-lessthanorequalto"></div>

### [`lessThanOrEqualTo`](https://finos.github.io/datahelix/playground/#ewogICAgImZpZWxkcyI6IFsKICAgIHsKICAgICAgIm5hbWUiOiAiZmllbGQiLAogICAgICAidHlwZSI6ICJpbnRlZ2VyIgogICAgfQogIF0sCiAgImNvbnN0cmFpbnRzIjogWwoKICAgIHsKICAgICAgImZpZWxkIjogImZpZWxkIiwKICAgICAgImxlc3NUaGFuT3JFcXVhbFRvIjogNQogICAgfQogIF0KfQ%3D%3D) _(field, value)_

```javascript
{ "field": "price", "lessThanOrEqualTo": 0 }
```

Is satisfied if `field` is a number less than or equal to `value`.

<div id="predicate-granularto"></div>

### [`granularTo`](https://finos.github.io/datahelix/playground/#ewogICAgImZpZWxkcyI6IFsKICAgIHsKICAgICAgIm5hbWUiOiAiZmllbGQiLAogICAgICAidHlwZSI6ICJkZWNpbWFsIgogICAgfQogIF0sCiAgImNvbnN0cmFpbnRzIjogWwoKICAgIHsKICAgICAgImZpZWxkIjogImZpZWxkIiwKICAgICAgImdyYW51bGFyVG8iOiAwLjEKICAgIH0KICBdCn0%3D) _(field, value)_

```javascript
{ "field": "price", "granularTo": 0.1 }
```

Is satisfied if `field` has at least the [granularity](#NumericGranularity) specified in `value`.

## Time/DateTime constraints

The time and datetime are shared but must be used with the same type. For example an equal to constraint on a time field must have a value of time.

<div id="predicate-after"></div>

### [`after`](https://finos.github.io/datahelix/playground/#ewogICAgImZpZWxkcyI6IFsKICAgIHsKICAgICAgIm5hbWUiOiAiZmllbGQiLAogICAgICAidHlwZSI6ICJkYXRldGltZSIKICAgIH0KICBdLAogICJjb25zdHJhaW50cyI6IFsKCiAgICB7CiAgICAgICJmaWVsZCI6ICJmaWVsZCIsCiAgICAgICJhZnRlciI6ICIyMDA1LTA1LTEyIgogICAgfQogIF0KfQ%3D%3D) _(field, value)_

```javascript
{ "field": "date", "after": "2018-09-01T00:00:00.000" }
```

Is satisfied if `field` is a time or datetime occurring after `value`.

<div id="predicate-afterorat"></div>

### [`afterOrAt`](https://finos.github.io/datahelix/playground/#ewogICAgImZpZWxkcyI6IFsKICAgIHsKICAgICAgIm5hbWUiOiAiZmllbGQiLAogICAgICAidHlwZSI6ICJkYXRldGltZSIKICAgIH0KICBdLAogICJjb25zdHJhaW50cyI6IFsKCiAgICB7CiAgICAgICJmaWVsZCI6ICJmaWVsZCIsCiAgICAgICJhZnRlck9yQXQiOiAiMjAwNS0wNS0xMiIKICAgIH0KICBdCn0%3D) _(field, value)_

```javascript
{ "field": "time", "afterOrAt": "00:00:00" }
```

Is satisfied if `field` is a time or datetime occurring after or simultaneously with `value`.

<div id="predicate-before"></div>

### [`before`](https://finos.github.io/datahelix/playground/#ewogICAgImZpZWxkcyI6IFsKICAgIHsKICAgICAgIm5hbWUiOiAiZmllbGQiLAogICAgICAidHlwZSI6ICJkYXRldGltZSIKICAgIH0KICBdLAogICJjb25zdHJhaW50cyI6IFsKCiAgICB7CiAgICAgICJmaWVsZCI6ICJmaWVsZCIsCiAgICAgICJiZWZvcmUiOiAiMjAwNS0wNS0xMiIKICAgIH0KICBdCn0%3D) _(field, value)_

```javascript
{ "field": "date", "before": "2018-09-01T00:00:00.000" }
```

Is satisfied if `field` is a time or datetime occurring before `value`.

<div id="predicate-beforeorat"></div>

### [`beforeOrAt`](https://finos.github.io/datahelix/playground/#ewogICAgImZpZWxkcyI6IFsKICAgIHsKICAgICAgIm5hbWUiOiAiZmllbGQiLAogICAgICAidHlwZSI6ICJkYXRldGltZSIKICAgIH0KICBdLAogICJjb25zdHJhaW50cyI6IFsKCiAgICB7CiAgICAgICJmaWVsZCI6ICJmaWVsZCIsCiAgICAgICJiZWZvcmVPckF0IjogIjIwMDUtMDUtMTIiCiAgICB9CiAgXQp9) _(field, value)_

```javascript
{ "field": "date", "beforeOrAt": "2018-09-01T00:00:00.000" }
```

Is satisfied if `field` is a time or datetime occurring before or simultaneously with `value`.

<div id="predicate-granularto-datetime"></div>

### [`granularTo`](https://finos.github.io/datahelix/playground/#ewogICAgImZpZWxkcyI6IFsKICAgIHsKICAgICAgIm5hbWUiOiAiZmllbGQiLAogICAgICAidHlwZSI6ICJkYXRldGltZSIKICAgIH0KICBdLAogICJjb25zdHJhaW50cyI6IFsKCiAgICB7CiAgICAgICJmaWVsZCI6ICJmaWVsZCIsCiAgICAgICJncmFudWxhclRvIjogIkRBWVMiCiAgICB9CiAgXQp9) _(field, value)_

```javascript
{ "field": "date", "granularTo": "days" }
```

Is satisfied if `field` has at least the [granularity](#DateTime-granularity) specified in `value`. Note that in the case where you want to give a datetime a granularity of days, the [date](#date) type can be used as a short hand.

<div id="otherfield-constraints"></div>

## Dependent field constraints

<div id="predicate-otherfield"></div>

### [`afterField`, `afterOrAtField`, `beforeField`, `beforeOrAtField`](https://finos.github.io/datahelix/playground/#ewogICAgImZpZWxkcyI6IFsKICAgIHsKICAgICAgIm5hbWUiOiAiZmllbGQxIiwKICAgICAgInR5cGUiOiAiZGF0ZXRpbWUiLAogICAgICAibnVsbGFibGUiOiBmYWxzZQogICAgfSwKICAgIHsKICAgICAgIm5hbWUiOiAiZmllbGQyIiwKICAgICAgInR5cGUiOiAiZGF0ZXRpbWUiLAogICAgICAibnVsbGFibGUiOiBmYWxzZQogICAgfSwKICAgIHsKICAgICAgIm5hbWUiOiAiZmllbGQzIiwKICAgICAgInR5cGUiOiAiZGF0ZXRpbWUiLAogICAgICAibnVsbGFibGUiOiBmYWxzZQogICAgfSwKICAgIHsKICAgICAgIm5hbWUiOiAiZmllbGQ0IiwKICAgICAgInR5cGUiOiAiZGF0ZXRpbWUiLAogICAgICAibnVsbGFibGUiOiBmYWxzZQogICAgfQogIF0sCiAgImNvbnN0cmFpbnRzIjogWwogICAgewogICAgICAiZmllbGQiOiAiZmllbGQxIiwKICAgICAgImFmdGVyRmllbGQiOiAiZmllbGQyIgogICAgfSwKICAgIHsKICAgICAgImZpZWxkIjogImZpZWxkMyIsCiAgICAgICJiZWZvcmVPckF0RmllbGQiOiAiZmllbGQ0IgogICAgfQogIF0KfQ%3D%3D)

allows a time/datetime field to be dependent on the output of another time/datetime field.

```javascript
{ "field": "laterDateField", "after": "previousDateField" }
```


<div id="predicate-offset"></div>

### [`greaterThanField`, `greaterThanOrEqualToField`, `lessThan`, `lessThanOrEqualToField`](https://finos.github.io/datahelix/playground/#ewogICAgImZpZWxkcyI6IFsKICAgIHsKICAgICAgIm5hbWUiOiAiZmllbGQxIiwKICAgICAgInR5cGUiOiAiaW50ZWdlciIsCiAgICAgICJudWxsYWJsZSI6IGZhbHNlCiAgICB9LAogICAgewogICAgICAibmFtZSI6ICJmaWVsZDIiLAogICAgICAidHlwZSI6ICJpbnRlZ2VyIiwKICAgICAgIm51bGxhYmxlIjogZmFsc2UKICAgIH0sCiAgICB7CiAgICAgICJuYW1lIjogImZpZWxkMyIsCiAgICAgICJ0eXBlIjogImludGVnZXIiLAogICAgICAibnVsbGFibGUiOiBmYWxzZQogICAgfSwKICAgIHsKICAgICAgIm5hbWUiOiAiZmllbGQ0IiwKICAgICAgInR5cGUiOiAiaW50ZWdlciIsCiAgICAgICJudWxsYWJsZSI6IGZhbHNlCiAgICB9CiAgXSwKICAiY29uc3RyYWludHMiOiBbCiAgICB7CiAgICAgICJmaWVsZCI6ICJmaWVsZDEiLAogICAgICAiZ3JlYXRlclRoYW5PckVxdWFsVG9GaWVsZCI6ICJmaWVsZDIiCiAgICB9LAogICAgewogICAgICAiZmllbGQiOiAiZmllbGQzIiwKICAgICAgImxlc3NUaGFuRmllbGQiOiAiZmllbGQ0IgogICAgfQogIF0KfQ%3D%3D)

allows a numeric field to be dependent on the output of another numeric field.
```javascript
{ "field": "laterNumericField", "greaterThanField": "previousNumericField" }
```

### [`offset`](https://finos.github.io/datahelix/playground/#ewogICAgImZpZWxkcyI6IFsKICAgIHsKICAgICAgIm5hbWUiOiAicmFuZG9tSW50ZWdlciIsCiAgICAgICJ0eXBlIjogImludGVnZXIiLAogICAgICAibnVsbGFibGUiOiBmYWxzZQogICAgfSwKICAgIHsKICAgICAgIm5hbWUiOiAib25lR3JlYXRlciIsCiAgICAgICJ0eXBlIjogImludGVnZXIiLAogICAgICAibnVsbGFibGUiOiBmYWxzZQogICAgfQogIF0sCiAgImNvbnN0cmFpbnRzIjogWwogICAgewogICAgICAiZmllbGQiOiAib25lR3JlYXRlciIsCiAgICAgICJlcXVhbFRvRmllbGQiOiAicmFuZG9tSW50ZWdlciIsCiAgICAgICJvZmZzZXQiOiAxCiAgICB9CiAgXQp9Cg%3D%3D)
Allows a dependent time/datetime/numeric field to always be a certain offset away from another time/datetime/numeric field.

The syntax is slightly different depending on the type.

##### Numeric offset
```javascript
{ "field": "field1", "equalToField": "field2", "offset": 3}
```

##### Time/datetime offset

```javascript
{ "field": "field1", "equalToField": "field2", "offset": 3, "offsetUnit": "days" }
```

Note that offsetUnit can be any of the [granularites](#DateTime-granularity) supported by datahelix.
Additionally in the case that the field is a datetime then the `working days` offsetUnit can be used to specify an offset of working days. See [an example of this in the online playground](https://finos.github.io/datahelix/playground/#ewogICAgImZpZWxkcyI6IFsKICAgIHsKICAgICAgIm5hbWUiOiAiZmlyc3QiLAogICAgICAidHlwZSI6ICJkYXRldGltZSIsCiAgICAgICJudWxsYWJsZSI6IGZhbHNlCiAgICB9LAogICAgewogICAgICAibmFtZSI6ICJzZWNvbmQiLAogICAgICAidHlwZSI6ICJkYXRldGltZSIsCiAgICAgICJudWxsYWJsZSI6IGZhbHNlCiAgICB9CiAgXSwKICAiY29uc3RyYWludHMiOiBbCiAgICB7CiAgICAgICJmaWVsZCI6ICJmaXJzdCIsCiAgICAgICJhZnRlciI6ICI4MDAxLTAyLTAzVDA0OjA1OjA2LjAwNyIKICAgIH0sCiAgICB7CiAgICAgICJmaWVsZCI6ICJzZWNvbmQiLAogICAgICAiZXF1YWxUb0ZpZWxkIjogImZpcnN0IiwKICAgICAgIm9mZnNldCI6IDMsCiAgICAgICJvZmZzZXRVbml0IjogImRheXMiCiAgICB9CiAgXQp9).


# Grammatical constraints
<div id="Grammatical-constraints"></div>

**Grammatical constraints** combine or modify other constraints. They are fully recursive; any grammatical constraint is a valid input to any other grammatical constraint.

See [set restriction and generation](user/SetRestrictionAndGeneration.md) for an in depth explanation of how the constraints are merged and data generated from them.

## [`not`](https://finos.github.io/datahelix/playground/#ewogICAgImZpZWxkcyI6IFsKICAgIHsKICAgICAgIm5hbWUiOiAiYW5faW50ZWdlciIsCiAgICAgICJ0eXBlIjogImludGVnZXIiLAogICAgICAibnVsbGFibGUiOiBmYWxzZQogICAgfQogIF0sCiAgImNvbnN0cmFpbnRzIjogWwogICAgewogICAgICAiZmllbGQiOiAiYW5faW50ZWdlciIsCiAgICAgICJncmVhdGVyVGhhbk9yRXF1YWxUbyI6IDEKICAgIH0sCiAgICB7CiAgICAgICJmaWVsZCI6ICJhbl9pbnRlZ2VyIiwKICAgICAgImxlc3NUaGFuT3JFcXVhbFRvIjogNAogICAgfSwKICAgIHsKICAgICAgIm5vdCI6IHsKICAgICAgICAiZmllbGQiOiAiYW5faW50ZWdlciIsCiAgICAgICAgImVxdWFsVG8iOiAyCiAgICAgIH0KICAgIH0KICBdCn0%3D)

```javascript
{ "not": { "field": "foo", "equalTo": "bar" } }
```

Wraps a constraint. Is satisfied if, and only if, its inner constraint is _not_ satisfied.

## [`anyOf`](https://finos.github.io/datahelix/playground/#ewogICAgImZpZWxkcyI6IFsKICAgIHsKICAgICAgIm5hbWUiOiAiZmllbGQxIiwKICAgICAgInR5cGUiOiAic3RyaW5nIiwKICAgICAgIm51bGxhYmxlIjogZmFsc2UKICAgIH0KICBdLAogICJjb25zdHJhaW50cyI6IFsKICAgIHsKICAgICAgImFueU9mIjogWwogICAgICAgIHsKICAgICAgICAgICJmaWVsZCI6ICJmaWVsZDEiLAogICAgICAgICAgImVxdWFsVG8iOiAiZm9vIgogICAgICAgIH0sCiAgICAgICAgewogICAgICAgICAgImZpZWxkIjogImZpZWxkMSIsCiAgICAgICAgICAiZXF1YWxUbyI6ICJiYXIiCiAgICAgICAgfQogICAgICBdCiAgICB9CiAgXQp9)

```javascript
{ "anyOf": [
    { "field": "foo", "isNull": true },
    { "field": "foo", "equalTo": 0 }
]}
```

Contains a number of sub-constraints. Is satisfied if any of the inner constraints are satisfied.

## [`allOf`](https://finos.github.io/datahelix/playground/#ewogICAgImZpZWxkcyI6IFsKICAgIHsKICAgICAgIm5hbWUiOiAiYWxsZXJnaWVzX2ZsYWciLAogICAgICAidHlwZSI6ICJib29sZWFuIiwKICAgICAgIm51bGxhYmxlIjogZmFsc2UKICAgIH0sCiAgICB7CiAgICAgICJuYW1lIjogImFsbGVyZ2llcyIsCiAgICAgICJ0eXBlIjogInN0cmluZyIsCiAgICAgICJudWxsYWJsZSI6IHRydWUKICAgIH0KICBdLAogICJjb25zdHJhaW50cyI6IFsKICAgIHsKICAgICAgImlmIjogewogICAgICAgICJmaWVsZCI6ICJhbGxlcmdpZXNfZmxhZyIsCiAgICAgICAgImVxdWFsVG8iOiB0cnVlCiAgICAgIH0sCiAgICAgICJ0aGVuIjogewogICAgICAgICJhbGxPZiI6IFsKICAgICAgICAgIHsKICAgICAgICAgICAgImZpZWxkIjogImFsbGVyZ2llcyIsCiAgICAgICAgICAgICJpblNldCI6IFsKICAgICAgICAgICAgICAiZG9ncyIsCiAgICAgICAgICAgICAgInBlYW51dHMiLAogICAgICAgICAgICAgICJzaGVsbGZpc2giCiAgICAgICAgICAgIF0KICAgICAgICAgIH0sCiAgICAgICAgICB7CiAgICAgICAgICAgICJmaWVsZCI6ICJhbGxlcmdpZXMiLAogICAgICAgICAgICAiaXNOdWxsIjogZmFsc2UKICAgICAgICAgIH0KICAgICAgICBdCiAgICAgIH0sCiAgICAgICJlbHNlIjogewogICAgICAgICJmaWVsZCI6ICJhbGxlcmdpZXMiLAogICAgICAgICJpc051bGwiOiB0cnVlCiAgICAgIH0KICAgIH0KICBdCn0%3D)

```javascript
{ "allOf": [
    { "field": "foo", "greaterThan": 15 },
    { "field": "foo", "lessThan": 100 }
]}
```

Contains a number of sub-constraints. Is satisfied if all of the inner constraints are satisfied.

## [`if`](https://finos.github.io/datahelix/playground/#ewogICAgImZpZWxkcyI6IFsKICAgIHsKICAgICAgIm5hbWUiOiAiYWxsZXJnaWVzX2ZsYWciLAogICAgICAidHlwZSI6ICJib29sZWFuIiwKICAgICAgIm51bGxhYmxlIjogZmFsc2UKICAgIH0sCiAgICB7CiAgICAgICJuYW1lIjogImFsbGVyZ2llcyIsCiAgICAgICJ0eXBlIjogInN0cmluZyIsCiAgICAgICJudWxsYWJsZSI6IHRydWUKICAgIH0KICBdLAogICJjb25zdHJhaW50cyI6IFsKICAgIHsKICAgICAgImlmIjogewogICAgICAgICJmaWVsZCI6ICJhbGxlcmdpZXNfZmxhZyIsCiAgICAgICAgImVxdWFsVG8iOiB0cnVlCiAgICAgIH0sCiAgICAgICJ0aGVuIjogewogICAgICAgICJhbGxPZiI6IFsKICAgICAgICAgIHsKICAgICAgICAgICAgImZpZWxkIjogImFsbGVyZ2llcyIsCiAgICAgICAgICAgICJpblNldCI6IFsKICAgICAgICAgICAgICAiZG9ncyIsCiAgICAgICAgICAgICAgInBlYW51dHMiLAogICAgICAgICAgICAgICJzaGVsbGZpc2giCiAgICAgICAgICAgIF0KICAgICAgICAgIH0sCiAgICAgICAgICB7CiAgICAgICAgICAgICJmaWVsZCI6ICJhbGxlcmdpZXMiLAogICAgICAgICAgICAiaXNOdWxsIjogZmFsc2UKICAgICAgICAgIH0KICAgICAgICBdCiAgICAgIH0sCiAgICAgICJlbHNlIjogewogICAgICAgICJmaWVsZCI6ICJhbGxlcmdpZXMiLAogICAgICAgICJpc051bGwiOiB0cnVlCiAgICAgIH0KICAgIH0KICBdCn0%3D)

```javascript
{
    "if":   { "field": "foo", "lessThan": 100 },
    "then": { "field": "bar", "greaterThan": 0 },
    "else": { "field": "bar", "equalTo": "N/A" }
}
```

Is satisfied if either:

- Both the `if` and `then` constraints are satisfied
- The `if` constraint is not satisfied, and the `else` constraint is

While it's not prohibited, wrapping conditional constraints in any other kind of constraint (eg, a `not`) may cause unintuitive results.



# Custom Generators
<div id="custom-generator"></div>

You can add your own custom java generators to the project with the following instructions.

## Adding Custom Generators
<div id="adding-custom-generator"></div>

To add a custom generator you will need to
 - clone the datahelix source code
 - go to the "custom" package
 - either
   - implement the CustomGenerator.java interface
   - use the CustomGeneratorBuilder.java to build a custom generator
 - add your custom generator to the list in the CustomGeneratorList.java class

There is an example folder in the "custom" package which shows an example using the CustomGeneratorBuilder to build a generator called "lorem ipsum"

## using Custom Generators
<div id="using-custom-generator"></div>

To use your custom generator you add it to the field definition in your profile like this

```javascript
{
    "name": "field1",
    "type": "string",
    "generator": "lorem ipsum"
}
```

This will use the "lorem ipsum" example custom generator.

To use your own, put the name of your generator instead of "lorem ipsum"

## using Custom Generators as Constraint
<div id="using-custom-generator-as-constraint"></div>

You can also use custom generators as constraints


```javascript
{ "field": "field1", "generator": "lorem ipsum" }
```
Custom generators can be used in "anyOf" grammatical constraints, as well as in the "then" and "else" parts of conditional constraints

To combine generators with sets and equalTo, you will need to create a 'matchingFunction' when building the custom generator. Which should be a function that returns true if a value is one the custom generator could produce.

To be able negate the custom generator, or use in the 'if' section of an if then statement, you must define the 'negated Generator' when building the custom generator. Which should return values that the custom generator should not produce.

# Running a Profile
<div id="Running-a-Profile"></div>

Profiles can be run against a jar using the command line.

## Command Line Arguments
<div id="Command-Line-Arguments"></div>

An example command would be something like

```shell script
java -jar datahelix.jar --max-rows=100 --replace --profile-file=profile.json --output-path=output.csv
```

it is also possible to execute the generator using a wrapper script:

on windows:

```shell script
datahelix --max-rows=100 --replace --profile-file=profile.json --output-path=output.csv
```

and on linux
```shell script
datahelix.sh --max-rows=100 --replace --profile-file=profile.json --output-path=output.csv
```

These presume that the scripts (datahelix.zip\datahelix\bin) are in the path, or you're currently working in the bin directory.

### Command Line Arguments
Option switches are case-sensitive, arguments are case-insensitive

* `--version` (or `-V`)
    * Displays generator version information.
* `--profile-file=<PATH>` (or `-p <PATH>`)
    * Path to the input profile file.
* `--output-path=<PATH>` (or `-o <PATH>`)
    * Path to the output file.  If not specified, output will be to standard output.
* `--replace=<true|false>`
    * Overwrite/replace existing output files. Defaults to false.
* `-n <ROWS>` or `--max-rows=<ROWS>`
   * Emit at most `<ROWS>` rows to the output file, if not specified will limit to 10,000,000 rows.
   * Mandatory in `RANDOM` mode.
* `--generation-type=<GENERATION_TYPE>`
    * Determines the type of (data generation)[Link] performed. 
    Where `<GENERATION_TYPE>` can be one of `FULL_SEQUENTIAL` or `RANDOM`(default).
* `--combination-strategy=<COMBINATION_STRATEGY>`
    * Determines the type of combination strategy used in full sequential mode. 
    `<COMBINATION_STRATEGY>` can be one of `MINIMAL`(default), `EXHAUSTIVE` or `PINNING`.
* `--output-format=<OUTPUT_FORMAT>`
    * Determines the output format. `<OUTPUT_FORMAT>` can be one of `csv`(default) or `json`.
    * If no `output-path` is provided then the JSON data will be streamed in [ndjson](http://ndjson.org/) format.
* `--visualiser-level=<VISUAL_LEVEL>`
    * Determines level of visualisation using. Where `<VISUAL_LEVEL>` can be one of `OFF` (default), `STANDARD` or `DETAILED`.
* `--visualiser-output-folder=<PATH>`
    * The path to the folder to write the generated visualiser files to (defaults to current directory (`.`).
      Its only used if `visualiser-level` is not set to`OFF`.

By default the generator will report how much data has been generated over time, the other options are below:
* `--verbose`
    * Will report in-depth detail of data generation.
* `--quiet`
    * Will disable velocity reporting.

`--quiet` will be ignored if `--verbose` is supplied.

## Generation Strategies
<div id="Generation Strategies"></div>
The generation mode can be specified by the `--generation-type` flag.

The generator supports the following data generation types

* Random (_default_)
* Full Sequential

### Random Mode
<div id="Random Mode"></div>
Generate some random data that abides by the given set of constraints.

Examples:

| Constraint | Emitted valid data |
| ---- | ---- |
| `Field 1 > 10 AND Field 1 < 20` | _(any values > 10 & < 20)_ |
| `Field 1 in set [A, B, C]` | _(A, B or C in any order, repeated as needed)_ |

Notes:
- Random generation of data is infinite and is limited to 1000 by default, use `--max-rows` to enable generation of more data.

### Full Sequential Mode
<div id="Full-Sequential-Mode"></div>
Generate all data that can be generated in order from lowest to highest.

Examples:

| Constraint | Emitted valid data |
| ---- | ---- |
| `Field 1 > 0 AND Field 1 < 5` | _(null, 1, 2, 3, 4)_ |
| `Field 1 in set [A, B, C]` | _(null, A, B, C)_ |

* Note that null will only be produced depending on the [properties](https://github.com/finos/datahelix/blob/master/docs/UserGuide.md#Fields) of Field 1.

#### Combination Strategies
<div id="Combination-Strategies"></div>

There are a few different combination strategies which can be used in `FULL_SEQUENTIAL` mode with minimal being the default. In modes other than full sequential, combination strategy will have no effect.

It is simplest to see how the different combination strategies work by look at the effect on a simple example profile. The following [profile](https://github.com/finos/datahelix/tree/master/examples/multipleFields/profile.json) contains two fields:
  * field1 - has values in set [ "A", "B" ]
  * field2 - has values in set [ 1, 2, 3 ]  

##### Minimal
<div id="Minimal"></div>

The minimal strategy outputs the minimum data required to exemplify each value at least once. Per the example, the output would be:

* "A",1
* "B",2
* "B",3

Note that minimal is the default combination strategy.

##### Exhaustive
<div id="Exhaustive"></div>

The exhaustive strategy outputs all possible combinations. Given the fields as defined above, possible outputs would be:

* "A",1
* "B",1
* "A",2
* "B",2
* "A",3
* "B",3


##### Pinning
<div id="Pinning"></div>

The pinning strategy establishes a baseline for each field (generally by picking the first available value for that field) and then creates outputs such that either:

* All values equal the baseline for the respective field
* All values except one equal the baseline for the respective field

To generate these outputs, we first output the first case (all values from baseline) and then iterate through each field, F, fixing all other fields at their baseline and generating the full range of values for F. For the example, the output would be:

* "A",1
* "A",2
* "A",3
* "B",1

# Visualising Decision Trees
<div id="Visualising-Decision-Trees"></div>

_This is an alpha feature. Please do not rely on it. If you find issues with it, please [report them](https://github.com/finos/datahelix/issues)._

This feature generates a <a href=https://en.wikipedia.org/wiki/DOT_(graph_description_language)>DOT</a> compliant representation of the decision tree,
for manual inspection, in the form of a DOT formatted file.

If use the `--visualiser-level` and `--visualiser-output-folder` command line options when generating data then
you can get visualisations of the decision tree outputted as graphs in DOT files.

* See [Developer Guide](DeveloperGuide.md) for more information on the decision tree structure.
* See [Command Line Arguments](#Command-Line-Arguments) for more information on the command line arguments.

The visualiser levels can have the following values:
* OFF - the default and it means no graphs outputted
* STANDARD - it means graphs of the decision tree outputted after each pre-walking stage is done
* DETAILED - it means the standard decision trees and the various decision trees created during walking stage are outputted

## How to Read the DOT Files produced

* You may read a DOT file with any text editor
* You can also use this representation with a visualiser such as [Graphviz](https://www.graphviz.org/).

    There may be other visualisers that are suitable to use. The requirements for a visualiser are known (currently) as:
    - DOT files are encoded with UTF-8, visualisers must support this encoding.
    - DOT files can include HTML encoded entities, visualisers should support this feature.
