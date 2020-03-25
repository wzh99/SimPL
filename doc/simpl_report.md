# SimPL Interpreter

###### 王梓涵　517021911179

## Introduction

### Objective

In this project I am required to implement an interpreter for the programming language SimPL. SimPL is a simplified dialect of ML, which can be used for both functional and imperative programming. 

### Project Structure

#### Parser

In package `parser`, infrastructures for lexical and syntactical analysis are already provided. The parser parses the source program into AST representation. My task is to implement type checking and evaluation methods for all the AST nodes.

#### Interpreter

In package `interpreter`, class `Interpreter` serves as the program entry for the whole project. Besides, there are classes representing runtime environment and values in this packages. Library functions should also be defined in this package. 

#### Typing

In package `typing` resides classes representing types and typing environment. Unification and substitution methods should be implemented, which form the basis of type inference. 

## Typing

### Type Inference



### Type Checking



## Semantics

### State



### Evaluation



### Library Functions



## Bonus Features

### Garbage Collection



### Lazy Evaluation



### Polymorphic Type



## Appendix

