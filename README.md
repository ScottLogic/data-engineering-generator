# DataHelix Generator [![CircleCI](https://circleci.com/gh/finos/datahelix.svg?style=svg)](https://circleci.com/gh/finos/datahelix) [![FINOS - Active](https://cdn.jsdelivr.net/gh/finos/contrib-toolbox@master/images/badge-active.svg)](https://finosfoundation.atlassian.net/wiki/display/FINOS/Active)

![DataHelix logo](docs/logo.png)

The generation of representative test and simulation data is a challenging and time-consuming task. Although DataHelix was created to address a specific challenge in the financial services industry, you will find it a useful tool for the generation of realistic data for simulation and testing, regardless of industry sector. All this from a straightforward JSON data profile document.

DataHelix is a proud member of the [Fintech Open Source Foundation](https://www.finos.org/) and operates within the [FINOS Data Technologies Program](https://www.finos.org/dt).

## Key documents

* For information on how to get started with DataHelix see our [Getting Started guide](docs/GettingStarted.md).

* For information on the syntax of DataHelix profiles see the [User Guide](docs/UserGuide.md).

* For information on how to contribute to the project, and more technical information about DataHelix, see the [Developer Guide](docs/DeveloperGuide.md).

* For a high level road map see [Road Map](docs/RoadMap.md).

## The Problem
When performing a wide range of software development tasks - functional or load testing on a system, prototyping an API or pitching a service to a potential customer - sample data is a necessity, but generating and maintaining it can be difficult. The nature of some industries makes it particularly difficult to cleanly manage data schemas, and sample datasets:

* Regulatory and methodological change often forces data schema changes.
* It is often difficult to completely remove legacy data due to obligations to maintain deprecated products.  Because of this, schemas tend to be progressively complicated with special cases and exceptions.
* Errors can be costly and reputation-damaging.
* For legal and/or privacy reasons, it is normally impossible to include real data in samples.

For all the above reasons, it is common to handcraft sample datasets. This approach brings several problems:

* It costs significant time up-front, and thereafter every time the schema changes.
* It's very easy to introduce errors.
* The sample data is unlikely to exhaustively cover all test cases.
* The sample data is not self-documenting, and documentation is likely to become out of date.

For data generation, partial solutions are available in services/libraries such as TSimulus, Mockaroo or GenRocket. However, these have limitations:

* They are limited to relatively simple data schemas with limited dependencies between fields
* None of them offer a complete end-to-end solution of profiling existing data to discover trends and constraints, generating from those constraints, and validating against them.
* Complex behaviour (if available) is modelled in an *imperative* style, forcing the user to design the process for generating the data using the library's toolbox, rather than a *declarative* style that describes the shape of the data and leaves it to the library to determine how to create it.

## The Mission

We aim to solve (at least) the following user needs:
* "I want to generate test cases for my validation procedures."
* "I want to generate sample data to document my API, or to use in a non-production version of my API."
* "I want to validate some data against a known specification or implementation."
* "I want to measure my existing test data's coverage against the range of possible data."
* "I want to generate an exhaustive set of data, for testing my API's robustness."

## The Product
A suite of tools:
* To generate data based on a declarative profile, either from the command-line, or through a restful API which can be called manually or through a web front end.
* To create a data profile from a dataset, including identifying constraints and relationships between the dataset's fields, so that similarly-shaped mock data can be generated using the profile.
* To validate a dataset against a data profile.

## Contributing

1. Fork it (<https://github.com/yourname/yourproject/fork>)
2. Create your feature branch (`git checkout -b feature/fooBar`)
3. Read our [contribution guidelines](.github/CONTRIBUTING.md) and [Community Code of Conduct](https://www.finos.org/code-of-conduct)
4. Commit your changes (`git commit -am 'Add some fooBar'`)
5. Push to the branch (`git push origin feature/fooBar`)
6. Create a new Pull Request

_NOTE:_ Commits and pull requests to FINOS repositories will only be accepted from those contributors with an active, executed Individual Contributor License Agreement (ICLA) with FINOS OR who are covered under an existing and active Corporate Contribution License Agreement (CCLA) executed with FINOS. Commits from individuals not covered under an ICLA or CCLA will be flagged and blocked by the FINOS Clabot tool. Please note that some CCLAs require individuals/employees to be explicitly named on the CCLA.

*Need an ICLA? Unsure if you are covered under an existing CCLA? Email [help@finos.org](mailto:help@finos.org)*


## License

Copyright 2019 Scott Logic Ltd.

Distributed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).

SPDX-License-Identifier: [Apache-2.0](https://spdx.org/licenses/Apache-2.0).
