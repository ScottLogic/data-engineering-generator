Feature: Constraint only satisfied if all inner constraints are satisfied

  Scenario: User attempts to combine contradicting constraints within an allOf operator
    Given there is a field foo
    And there is a constraint:
    """
      { "allOf": [
         { "field": "foo", "is": "equalTo", "value": "Test0" },
         { "field": "foo", "is": "equalTo", "value": 5 }
      ]}
    """
    Then no data is created


  Scenario:
    Given there is a field price
      | price |

    And there is a constraint:
    """
      { "allOf": [
         { "field": "price", "is": "ofType", "value": "number" },
         { "field": "price", "is": "equalTo", "value": 5 }
      ]}
    """

    Then the following data should be generated:
      | price |
      |   5   |


  Scenario:
    Given the following fields exist:
      | foo |

    And there is a constraint:
    """
      { "allOf": [
         { "field": "foo", "is": "equalTo", "value": "Test0" },
         { "field": "foo", "is": "ofType", "value": "string" },
      ]}
    """

    Then the following data should be generated:
      | foo   |
      | Test0 |


