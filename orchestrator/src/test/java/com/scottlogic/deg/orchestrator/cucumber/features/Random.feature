Feature: User can generate valid data for all types (string, integer, decimal, or datetime) in random generation mode. Actual randomness of the data not tested.

  Background:
    Given the generation strategy is random
    And there is a field foo

  Scenario: The generator produces valid 'DateTime' data in random mode
    Given foo is of type "datetime"
    And foo is anything but null
    And the generator can generate at most 5 rows
    And foo is before or at 2019-01-01T00:00:00.000Z
    Then 5 rows of data are generated
    And foo contains anything but null
    And foo contains datetimes between 0001-01-01T00:00:00.000Z and 2019-01-01T00:00:00.000Z inclusively

  Scenario: The generator produces valid 'Integer' data in random mode
    Given foo is of type "integer"
    And foo is anything but null
    And the generator can generate at most 5 rows
    And foo is less than or equal to 10
    Then 5 rows of data are generated
    And foo contains numeric values less than or equal to 10
    And foo contains anything but null

  Scenario: The generator produces valid 'Decimal' data in random mode
    Given foo is of type "decimal"
    And foo is anything but null
    And the generator can generate at most 5 rows
    And foo is less than or equal to 10
    Then 5 rows of data are generated
    And foo contains numeric values less than or equal to 10
    And foo contains anything but null

  Scenario: The generator produces valid 'String' data in random mode
    Given foo is of type "string"
    And foo is anything but null
    And the generator can generate at most 5 rows
    And foo is shorter than 10
    Then 5 rows of data are generated
    And foo contains strings of length between 0 and 9 inclusively
    And foo contains anything but null

  Scenario: The generator produces valid RegEx restricted 'String' data in random mode
    Given foo is of type "string"
    And foo is anything but null
    And the generator can generate at most 5 rows
    And foo is matching regex /[a-z]{0,9}/
    Then 5 rows of data are generated
    And foo contains strings matching /[a-z]{0,9}/
    And foo contains anything but null

  Scenario: The generator produces valid inverted RegEx restricted 'String' data in random mode
    Given foo is of type "string"
    And foo is anything but null
    And the generator can generate at most 5 rows
    And foo is anything but matching regex /[a-z]{0,9}/
    Then 5 rows of data are generated
    And foo contains string data
    And foo contains anything but strings matching /[a-z]{0,9}/
    And foo contains anything but null

  Scenario: The generator produces valid 'ISIN' data in random mode (general format is checked here, not the checksum)
    Given foo is a valid "ISIN"
    And foo is anything but null
    And the generator can generate at most 5 rows
    Then 5 rows of data are generated
    And foo contains strings matching /[A-Z]{2}[A-Z0-9]{9}[0-9]{1}/

  @ignore #933: Getting IndexOutOfBoundsException
  Scenario: The generator produces valid 'ISIN' data in random mode when combined with "not equalTo" constraint
    Given foo is a valid "ISIN"
    And foo is anything but equal to "GB009CJ9GB62"
    And the generator can generate at most 5 rows
    Then 5 rows of data are generated
    And foo contains strings matching /[A-Z]{2}[A-Z0-9]{9}[0-9]{1}/

  Scenario: The generator produces valid 'SEDOL' data in random mode (general format is checked here, not the checksum)
    Given foo is a valid "SEDOL"
    And foo is anything but null
    And the generator can generate at most 5 rows
    Then 5 rows of data are generated
    And foo contains strings matching /[B-DF-HJ-NP-TV-Z0-9]{6}[0-9]/

  @ignore #933: Getting IndexOutOfBoundsException
  Scenario: The generator produces valid 'SEDOL' data in random mode when combined with "not equalTo" constraint
    Given foo is a valid "SEDOL"
    And foo is anything but equal to "3091357"
    And the generator can generate at most 5 rows
    Then 5 rows of data are generated
    And foo contains strings matching /[B-DF-HJ-NP-TV-Z0-9]{6}[0-9]/

  Scenario: The generator produces valid 'Null' data in random mode
    Given foo is null
    And the generator can generate at most 5 rows
    Then the following data should be generated:
      | foo  |
      | null |
      | null |
      | null |
      | null |
      | null |
