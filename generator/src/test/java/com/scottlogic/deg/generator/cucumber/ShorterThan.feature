Feature: User can specify that a string length is lower than, a specified number of characters

Background:
     Given the generation strategy is full
      And there is a field foo
      And foo is of type "string"

Scenario: Running a 'shorterThan' request using a number to specify a the length of a generated string should be successful
       And foo is shorter than 5
       And foo is matching regex /[x]{0,5}/
     Then the following data should not be included in what is generated:
       | foo      |
       | "xxxxx"  |
       | "xxxxxx" |

Scenario: Running a 'shorterThan' request using a number (zero) to specify a the length of a generated string should fail with an error message
       And foo is shorter than 0
     Then I am presented with an error message
       And no data is created

Scenario: Running a 'shorterThan' request using a number (negative number) to specify a the length of a generated string should fail with an error message
       And foo is shorter than -1
     Then I am presented with an error message
       And no data is created

@ignore
Scenario: Running a 'shorterThan' request using a number (decimal number) to specify a the length of a generated string should fail with an error message
       And foo is shorter than 1.1
     Then I am presented with an error message
       And no data is created

@ignore
Scenario: Running a 'shorterThan' request using a number (comma seperated) to specify a the length of a generated string should fail with an error message
       And foo is shorter than 1,000
     Then I am presented with an error message
       And no data is created

Scenario: Running a 'shorterThan' request using a string (number) to specify a the length of a generated string should fail with an error message
       And foo is shorter than "5"
     Then I am presented with an error message
       And no data is created

Scenario: Running a 'shorterThan' request using a string (a-z character) to specify a the length of a generated string should fail with an error message
       And foo is shorter than "five"
     Then I am presented with an error message
       And no data is created

Scenario: Running a 'shorterThan' request using a string (A-Z character) to specify a the length of a generated string should fail with an error message
       And foo is shorter than "FIVE"
     Then I am presented with an error message
       And no data is created

Scenario: Running a 'shorterThan' request using a string (A-Z character) to specify a the length of a generated string should fail with an error message
       And foo is shorter than "FIVE"
     Then I am presented with an error message
       And no data is created

Scenario: Running a 'shorterThan' request using a string (special character) to specify a the length of a generated string should fail with an error message
       And foo is shorter than "!"
     Then I am presented with an error message
       And no data is created

@ignore
Scenario: Running a 'shorterThan' request using a number to specify a the length of a generated numeric type field should be successful
       And foo is shorter than 5
       And foo is equal to 1234
       And foo is of type "numeric"
     Then the following data should be generated:
       | foo  |
       | null |
       | 1234 |

@ignore
Scenario: Running a 'shorterThan' request using a number to specify a the length of a generated temporal type field should be successful
       And foo is shorter than 25
       And foo is equal to 2010-01-01T00:00:00.000
       And foo is of type "temporal"
     Then the following data should be generated:
       | foo                     |
       | 2010-01-01T00:00:00.000 |

Scenario: Running a 'shorterThan' request using an empty string "" to specify a the length of a generated string field should fail with an error message
       And foo is shorter than ""
     Then I am presented with an error message
       And no data is created

Scenario: Running a 'shorterThan' request using null to specify a the length of a generated string field should fail with an error message
       And foo is shorter than null
     Then I am presented with an error message
       And no data is created

Scenario: Running a 'shorterThan' request alongside a non-contradicting equalTo constraint should be successful
       And foo is shorter than 5
       And foo is equal to "1234"
     Then the following data should be generated:
       | foo    |
       | null   |
       | "1234" |

Scenario: Running a 'shorterThan' request alongside a contradicting equalTo constraint should produce null
       And foo is shorter than 5
       And foo is equal to "12345"
     Then the following data should be generated:
       | foo    |
       | null   |

Scenario: Running a 'shorterThan' request alongside a non-contradicting inSet constraint should be successful
       And foo is shorter than 5
       And foo is in set:
       | "1234" |
       | "123"  |
     Then the following data should be generated:
       | foo    |
       | null   |
       | "1234" |
       | "123"  |

Scenario: Running a 'shorterThan' request alongside a non-contradicting inSet constraint should produce null
       And foo is shorter than 5
       And foo is in set:
       | "12345"  |
       | "123456" |
     Then the following data should be generated:
       | foo    |
       | null   |

Scenario: Running a 'shorterThan' request alongside a null constraint should be successful
       And foo is shorter than 5
       And foo is null
     Then the following data should be generated:
       | foo  |
       | null |

Scenario: Running a 'shorterThan' request alongside a non-contradicting matchingRegex constraint should be successful
       And foo is shorter than 3
       And foo is matching regex /[☠]{2}/
     Then the following data should be generated:
       | foo    |
       | null   |
       | "☠☠" |

@ignore
Scenario: Running a 'shorterThan' request alongside a contradicting matchingRegex constraint should produce null
       And foo is shorter than 2
       And foo is matching regex /[💾]{2}/
     Then the following data should be generated:
       | foo    |
       | null   |

@ignore
Scenario: Running a 'shorterThan' request alongside a non-contradicting containingRegex constraint should be successful
       And foo is shorter than 3
       And foo is containing regex /[🍩]{2}/
     Then the following data should be included in what is generated:
       | foo    |
       | null   |
       | "🍩🍩" |

Scenario: Running a 'shorterThan' request alongside a non-contradicting matchingRegex constraint should be successful
       And foo is shorter than 1
       And foo is matching regex /[💾]{2}/
      Then the following data should be generated:
        | foo    |
        | null   |
Scenario: Running a 'shorterThan' request alongside a contradicting containingRegex constraint should be successful
       And foo is shorter than 1
       And foo is containing regex /[🍩]{2}/
      Then the following data should be generated:
        | foo    |
        | null   |

Scenario: Running a 'shorterThan' request alongside a non-contradicting ofLength constraint should be successful
       And foo is shorter than 3
       And foo is of length 2
       And foo is matching regex /[♀]{0,2}/
     Then the following data should be generated:
       | foo  |
       | null |
       | "♀♀" |

@ignore
Scenario: Running a 'shorterThan' request alongside a contradicting ofLength (too short) constraint should produce null
       And foo is shorter than 3
       And foo is of length 10
    Then the following data should be generated:
      | foo  |
      | null |

Scenario: Running a 'shorterThan' request alongside a non-contradicting longerThan constraint should be successful
       And foo is shorter than 3
       And foo is longer than 1
       And foo is matching regex /[♀]{0,3}/
     Then the following data should be included in what is generated:
       | foo  |
       | null |
       | "♀♀" |

@ignore
Scenario: Running a 'shorterThan' request alongside a contradicting longerThan (too long) constraint should produce null
       And foo is shorter than 3
       And foo is longer than 10
    Then the following data should be generated:
      | foo  |
      | null |

Scenario: Running a 'shorterThan' request alongside a non-contradicting shorterThan constraint should be successful
       And foo is shorter than 4
       And foo is shorter than 3
       And foo is matching regex /[♀]{0,5}/
     Then the following data should be included in what is generated:
       | foo  |
       | null |
       | "♀♀" |
       And the following data should not be included in what is generated:
       | foo    |
       | "♀aa"  |
       | "♀aaa" |

@ignore
Scenario: Running a 'shorterThan' request alongside a greaterThan constraint should be successful
       And foo is shorter than 2
       And foo is greater than 10
      Then the following data should be included in what is generated:
       | foo    |
       | "a"    |
       | null   |

@ignore
Scenario: Running a 'shorterThan' request alongside a greaterThanOrEqualTo constraint should be successful
       And foo is shorter than 2
       And foo is greater than or equal to 3
     Then the following data should be included in what is generated:
       | foo    |
       | "a"    |
       | null   |

@ignore
Scenario: Running a 'shorterThan' request alongside a lessThan constraint should be successful
       And foo is shorter than 2
       And foo is less than 15
     Then the following data should be included in what is generated:
       | foo    |
       | "a"    |
       | null   |

@ignore
Scenario: Running a 'shorterThan' request alongside a lessThanOrEqualTo constraint should be successful
       And foo is shorter than 2
       And foo is less than or equal to 19
     Then the following data should be included in what is generated:
       | foo    |
       | "a"    |
       | null   |

Scenario: Running a 'shorterThan' request as part of a non-contradicting anyOf constraint should be successful
       And there is a constraint:
       """
       { "anyOf": [
         { "field": "foo", "is": "shorterThan", "value": 2 },
         { "field": "foo", "is": "shorterThan", "value": 3 }
       ]}
       """
       And foo is containing regex /[%]{1}/
     Then the following data should be included in what is generated:
       | foo  |
       | null |
       | "%1" |
       | "%"  |
       And the following data should not be included in what is generated:
       | foo   |
       | "%12" |

Scenario: Running a 'shorterThan' request as part of a non-contradicting allOf constraint should be successful
       And there is a constraint:
       """
       { "allOf": [
         { "field": "foo", "is": "shorterThan", "value": 3 },
         { "field": "foo", "is": "shorterThan", "value": 2 }
       ]}
       """
       And foo is containing regex /[%]{1}/
     Then the following data should be included in what is generated:
       | foo  |
       | null |
       | "%"  |
       And the following data should not be included in what is generated:
       | foo   |
       | "%1"  |
       | "%12" |