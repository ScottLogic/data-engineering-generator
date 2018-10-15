Feature: User can specify that a numeric value is higher than, or equal to, a specified threshold
    
Scenario: User requires to create a numeric field with data values that are greater or the same as zero
     Given there is a field foo
       And foo is greater than or equal to 0
       And foo is less than 10
       And foo is granular to 1
       And foo is not null
     Then the following data should be generated:
       | foo |
       | 0   |
       | 1   |
       | 2   |
       | 3   |
       | 4   |
       | 5   |
       | 6   |
       | 7   |
       | 8   |
       | 9   |

Scenario: User requires to create a numeric field with data values that are greater than or the same as zero but constrained to not be greater than one
     Given there is a field foo
       And foo is greater than or equal to 0
       And foo is less than 1
       And foo is granular to 1
       And foo is not null
     Then the following data should be generated:
       | foo |
       | 0   |

Scenario: User requires to create a field with decimal values that are greater than or the same as zero, specified as an integer
     Given there is a field foo
       And foo is greater than or equal to 0
       And foo is less than 2
       And foo is granular to 0.1
       And foo is not null
     Then the following data should be generated:
       | foo |
       | 0.0 |
       | 0.1 |
       | 0.2 |
       | 0.3 |
       | 0.4 |
       | 0.5 |
       | 0.6 |
       | 0.7 |
       | 0.8 |
       | 0.9 |
       | 1.0 |
       | 1.1 |
       | 1.2 |
       | 1.3 |
       | 1.4 |
       | 1.5 |
       | 1.6 |
       | 1.7 |
       | 1.8 |
       | 1.9 |

Scenario: User requires to create a field with decimal values that are greater than or the same as zero, specified as a decimal
     Given there is a field foo
       And foo is greater than or equal to 0.0
       And foo is less than 2.0
       And foo is granular to 0.1
       And foo is not null
     Then the following data should be generated:
       | foo |
       | 0.0 |
       | 0.1 |
       | 0.2 |
       | 0.3 |
       | 0.4 |
       | 0.5 |
       | 0.6 |
       | 0.7 |
       | 0.8 |
       | 0.9 |
       | 1.0 |
       | 1.1 |
       | 1.2 |
       | 1.3 |
       | 1.4 |
       | 1.5 |
       | 1.6 |
       | 1.7 |
       | 1.8 |
       | 1.9 |

Scenario: User requires to create a numeric field with data values that are greater than or the same as a negative number
     Given there is a field foo
       And foo is greater than or equal to -10
       And foo is less than 0
       And foo is granular to 1
       And foo is not null
     Then the following data should be generated:
       | foo |
       | -10 |
       | -9  |
       | -8  |
       | -7  |
       | -6  |
       | -5  |
       | -4  |
       | -3  |
       | -2  |
       | -1  |

Scenario: User requires to create a numeric field with data values that are greater than zero and greater than or the same as one
     Given there is a field foo
       And foo is greater than 0
       And foo is greater than or equal to 1
       And foo is less than 10
       And foo is granular to 1
       And foo is not null
     Then the following data should be generated:
       | foo |
       | 1   |
       | 2   |
       | 3   |
       | 4   |
       | 5   |
       | 6   |
       | 7   |
       | 8   |
       | 9   |

Scenario: User requires to create a numeric field with data values that are greater than or the same as zero and greater than one
     Given there is a field foo
       And foo is greater than or equal to 0
       And foo is greater than 1
       And foo is less than 10
       And foo is granular to 1
       And foo is not null
     Then the following data should be generated:
       | foo |
       | 2   |
       | 3   |
       | 4   |
       | 5   |
       | 6   |
       | 7   |
       | 8   |
       | 9   |

Scenario: User requires to create a numeric field with data values that are greater than or the same as zero and greater than or the same as one
     Given there is a field foo
       And foo is greater than or equal to 0
       And foo is greater than or equal to 1
       And foo is less than 10
       And foo is granular to 1
       And foo is not null
     Then the following data should be generated:
       | foo |
       | 1   |
       | 2   |
       | 3   |
       | 4   |
       | 5   |
       | 6   |
       | 7   |
       | 8   |
       | 9   |

Scenario: User attempts to create a numeric field with data value that are greater than or the same as zero using an incorrect field value type of string
     Given there is a field foo
       But the profile is invalid as foo can't be greater than or equal to "Zero"
     Then I am presented with an error message
        And no data is created