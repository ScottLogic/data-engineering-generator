# Data constraints

Data constraints are either **epistemic** (concerned with what values are valid) or **presentational** (concerned with how values are outputted).

## General

### `equalTo` _(field, value)_

```javascript
{ "field": "type", "is": "equalTo", "value": "X_092" }
OR
{ "field": "type", "is": "equalTo", "value": 23 }
```

Is satisfied if `field`'s value is equal to `value`

### `inSet` _(field, values)_

```javascript
{ "field": "type", "is": "inSet", "values": [ "X_092", "X_094" ] }
```

Is satisfied if `field`'s value is in the set `values`

### `null` _(field)_

```javascript
{ "field": "price", "is": "null" }
```

Is satisfied if `field` is null or absent.

### `ofType` _(field, value)_

```javascript
{ "field": "price", "is": "ofType", "value": "string" }
```

Is satisfied if `field` is of type represented by `value` (valid options: `numeric`, `string`, `temporal`)

## Textual
These constraints imply `ofType string`.

### `matchingRegex` _(field, value)_

```javascript
{ "field": "name", "is": "matchingRegex", "value": "[a-z]{0, 10}" }
```

Is satisfied if `field` is a string matching the regular expression expressed in `value`. 

### `ofLength` _(field, value)_

```javascript
{ "field": "name", "is": "ofLength", "value": 5 }
```

Is satisfied if `field` is a string whose length exactly matches `value`.
    
### `longerThan` _(field, value)_

```javascript
{ "field": "name", "is": "longerThan", "value": 3 }
```

Is satisfied if `field` is a string with length greater than `value`.

### `shorterThan` _(field, value)_

```javascript
{ "field": "name", "is": "shorterThan", "value": 3 }
```

Is satisfied if `field` is a string with length less than `value`.   


## Numeric
These constraints imply `ofType numeric`.

### `greaterThan` _(field, value)_

```javascript
{ "field": "price", "is": "greaterThan", "value": 0 }
```

Is satisfied if `field` is a number greater than `value`.

### `greaterThanOrEqualTo` _(field, value)_

```javascript
{ "field": "price", "is": "greaterThanOrEqualTo", "value": 0 }
```

Is satisfied if `field` is a number greater than or equal to `value`.

### `lessThan` _(field, value)_

```javascript
{ "field": "price", "is": "lessThan", "value": 0 }
```

Is satisfied if `field` is a number less than `value`.

### `lessThanOrEqualTo` _(field, value)_

```javascript
{ "field": "price", "is": "lessThanOrEqualTo", "value": 0 }
```

Is satisfied if `field` is a number less than or equal to `value`.


## Temporal
These constraints imply `ofType temporal`.

### `after` _(field, value)_

```javascript
{ "field": "date", "is": "after", "value": "2018-09-01" }
```

Is satisfied if `field` is a datetime occurring after `value`.

### `afterOrAt` _(field, value)_

```javascript
{ "field": "date", "is": "afterOrAt", "value": "2018-09-01" }
```

Is satisfied if `field` is a datetime occurring after or simultaneously with `value`.

### `before` _(field, value)_

```javascript
{ "field": "date", "is": "before", "value": "2018-09-01" }
```

Is satisfied if `field` is a datetime occurring before `value`.

### `beforeOrAt` _(field, value)_

```javascript
{ "field": "date", "is": "beforeOrAt", "value": "2018-09-01" }
```

Is satisfied if `field` is a datetime occurring before or simultaneously with `value`.

## Presentational
  
### `formattedAs` _(field, value)_

```javascript
{ "field": "price", "formattedAs": "%.5s" }
```

Used by output serialisers where string output is required. `value` must be:
 
 * a string recognised by Java's `String.format` method
 * appropriate for the data type of `field`
 
