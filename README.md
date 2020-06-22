# SimPL Interpreter

In this project I implemented an interpreter for the programming language SimPL. See [report](doc/simpl_report.md) for further details. SimPL is a simplified dialect of ML, which can be used for both functional and imperative programming. 

## Project Structure

### Parser

In package `parser`, infrastructures for lexical and syntactical analysis are already provided. The parser parses the source program into AST representation. I implemented type checking and evaluation methods for all the AST nodes.

### Interpreter

In package `interpreter`, class `Interpreter` serves as the program entry for the whole project. Besides, there are classes representing runtime environment and values in this packages. Library functions should also be defined in this package. 

### Typing

In package `typing` resides classes representing types and typing environment. Substitution and unification methods should be implemented for all types, which form the basis of type inference. 

## Bonus Features

* Garbage Collection
* Lazy Evaluation
* Mutually Recursive Combinator
* Infinite Stream
