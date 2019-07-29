Feature: User can specify that data must be created to conform to each of multiple specified constraints.

  Background:
    Given the generation strategy is full

  Scenario: Running an 'allOf' request that contains a valid nested allOf request should be successful
    Given there is a field foo
    And there is a constraint:
      """
      { "allOf": [
        { "allOf": [
          { "field": "foo", "is": "matchingRegex", "value": "[a-b]{2}" },
          { "field": "foo", "is": "ofLength", "value": 2 }
        ]},
        { "field": "foo", "is": "ofType", "value": "string" }
      ]}
      """
    Then the following data should be generated:
      | foo  |
      | "aa" |
      | "ab" |
      | "bb" |
      | "ba" |
      | null |

  @ignore  #91 Reduce duplication where (eg) decisions have overlapping options
  Scenario: Running an 'allOf' request that contains a valid nested anyOf request should be successful
    Given there is a field foo
    And foo is of type "string"
    And there is a constraint:
      """
      { "allOf": [
        { "anyOf": [
          { "field": "foo", "is": "ofLength", "value": 1 },
          { "field": "foo", "is": "ofLength", "value": 2 }
        ]},
        { "field": "foo", "is": "matchingRegex", "value": "[1]{1,2}" }
      ]}
      """
    Then the following data should be generated:
      | foo  |
      | "1"  |
      | "11" |
      | null |

  Scenario: Running an 'allOf' request that contains an invalid nested allOf request should generate null
    Given there is a field foo
    And there is a constraint:
      """
      { "allOf": [
        { "allOf": [
          {"field": "foo", "is": "matchingRegex", "value": "[a-k]{3}" },
          {"field": "foo", "is": "matchingRegex", "value": "[1-5]{3}" }
        ]},
        { "field": "foo", "is": "ofType", "value": "string" }
      ]}
      """
    Then the following data should be generated:
      | foo  |
      | null |

  Scenario: Running a 'allOf' request that includes multiple values within the same statement should be successful
    Given there is a field foo
    And foo is of type "string"
    And there is a constraint:
      """
      { "allOf": [
        { "field": "foo", "is": "equalTo", "value": "Test01" },
        { "field": "foo", "is": "equalTo", "value": "Test01" }
      ]}
      """
    Then the following data should be generated:
      | foo      |
      | "Test01" |

  Scenario: User attempts to combine two constraints that only intersect at the empty set within an allOf operator should not generate data
    Given there is a field foo
    And there is a constraint:
      """
      { "allOf": [
        { "field": "foo", "is": "equalTo", "value": "Test0" },
        { "field": "foo", "is": "equalTo", "value": 5 }
      ]}
      """
    Then no data is created

  Scenario: User constrains type with not allOf construction should generate only datetimes
    Given there is a field foo
    And untyped fields are allowed
    And there is a constraint:
      """
      { "allOf": [
        { "not": { "field": "foo", "is": "ofType", "value": "string" }},
        { "not": { "field": "foo", "is": "ofType", "value": "decimal" }}
      ]}
      """
    Then some data should be generated
    And foo contains anything but string data
    And foo contains anything but numeric data
