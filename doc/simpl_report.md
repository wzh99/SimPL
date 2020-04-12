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

In package `typing` resides classes representing types and typing environment. Substitution and unification methods should be implemented for all types, which form the basis of type inference. 



## Typing

### Type Inference

Procedures required to perform type inference are listed in abstract class `Type`. These methods are shown in the listing:

```java
public abstract class Type {
    public abstract boolean isEqualityType();
    public abstract boolean contains(TypeVar tv);
    public abstract Type replace(TypeVar a, Type t);
    public abstract Substitution unify(Type other) throws TypeError;
    /* Other members ... */
}
```

Method `isEqualityType` is mainly used for type checking in equality expression, so I will skip it here. In the following, implementation of the rest three methods will be discussed. 

The discussion may be divided with respect to different categories of types: 'Primitive type' refers to unit, boolean and integer. 'Compound type` refers to arrow, pair, list, reference, etc. Type variable may also appear as a separate category.

#### Substitution

`contains` and `replace` are related to type substitution. `contains` tests whether a certian type variable ever appears in this type, and `replace` replaces a type variable with another type if that type variable appears in this type. 

Primitive types cannot contain any type variable, and they cannot be replaced. Compound types may contain type variables, depending on whether its components contains that. And it can call `replace` on its components, and then combine them. Type variable contains another type variable if it shares the same name with that, and it replaces by changing its identifier to the one of that.

#### Unification

Type unification finds a substitution that can make a certain type uniform with another type. This algorithm is implemented in method `unify`. 

At any type, if one but not both types is a type variable, and that type variable appears on the RHS, the two variables are swapped. Primitive type can only be unified with the same type, yielding an identity substitution. Compound type can only be unified with another compound type of the same kind, and then unifies its component pairwise. For other cases, report a type mismatch error. The substitution returns by the components should be composed. 

For type variables, if another type is also a type variable, do nothing if they share the same name, or substitute that for this type if they don't. If that type still contains this type, there is a type circularity. The unification algorithm cannot handle this, so report a circularity error. 

### Type Checking

Type checking algorithm is implemeted in `typeCheck` methods of AST nodes. The procedure is supported by type environment `TypeEnv`, which records mapping from names to types. When a `Name` node is visited, the algorithm look the name up in `TypeEnv`.

Implementation of type checking are directed by typing rules listed in the language specification. Generally, this implementation can follow a certain pattern. First, call `typeCheck` on sub-expressions and get their respective `TypeResult`s. Then, perform unification on the types of sub-expressions. Finally, return type of this expression. Once a new substitution is derived, it is immediately composed with previous one. The implementation of `typeCheck` in `ArithExpr` class can serve as a good example for this pattern.

```java
@Override public TypeResult typeCheck(TypeEnv E) throws TypeError {
    // Check types of both operands
    var lhsTr = l.typeCheck(E);
    var rhsTr = r.typeCheck(E);
    var subst = lhsTr.s.compose(rhsTr.s);

    // Unify both types to `int`
    subst = subst.compose(lhsTr.t.unify(Type.INT));
    subst = subst.compose(rhsTr.t.unify(Type.INT));

    // Return typing result
    return TypeResult.of(subst, Type.INT);
}
```

Since there are tens of AST node classes in package `parser.ast` and many of them share the same pattern with the example shown above, discussing them all is tedious and meaningless. In the following I will just discuss some cases that need further consideration.

#### `Fn`

`Fn` node is special in that there is no way to know what the parameter type is. Here I can bind a type variable to the name of the parameter, and check type of function body. The type variable for the parameter may be unified with another type. Then the type checking algorithm can replace the type variable with that type, and return the infered type for this function.

`Rec` node works in the similar way as `Fn`. The only difference is that the 'parameter' type and body type should be unified, and the unified type should be returned. 

#### `App`

It is straightforward if the type of function sub-expression of `App` is already an arrow type. But the case where it is a type variable needs special care. In this case, this type variable should be replaced with a parameterized arrow type, with type of argument sub-expression of `App` as its parameter type, and a new type variable as its body type. 

#### `EqExpr`

Only equality type can appear on either side of `EqExpr`. This is the place where `isEqualityType` is called. Inference rules for equality type are given in the specification, and they can be coded in `isEqualityType` of `Type` subclasses. 



## Semantics

### State

From the specification, it can be known that the machine state is composed of environment $E$, memory $M$, and memory pointer $p$. 

#### Environment

Environment $E$ stores mapping from names to values. It can be composed with another mapping, if we want to create a new binding. We can also query the environment to find value bound to a certian name.

#### Memory

Memory $M$ stores mapping from integer addresses to values. Memory pointer $p$ stores the address for next reference cell. It can also be understood as the number of total reference cells at that time. This pointer helps allocation of new reference cells. 

### Values

Package `interpreter` contains several classes representing values. Since the language specification adopts big-step semantics, the evaluation result of any expression must be a value. For `Value` subclasses, only the following method need to be implemented:

```java
@Override public boolean equals(Object other) { /* Implementation ... */ }
```

 This is actually the method inherited from `Object`. Only values that are of equality type can be compared. Otherwise, it always returns `false`. Implementation of this method for values is trivial, so I skip it here.

### Evaluation

`eval` method of AST nodes need to be implemented to support evaluation. Like in type checking, I just discuss the common pattern and list cases that need special care. 

The pattern is quite simple. Just call `eval` on sub-expressions according to the order specified by the corresponding evaluation rule. Usually we need to check if the values returned match a certain pattern. Finally, perform operation on the values and return as result. `eval` method in `Add` could serve  as a good example:

```java
@Override public Value eval(State s) throws RuntimeError {
    var v1 = l.eval(s);
    if (!(v1 instanceof IntValue)) {
        throw new RuntimeError("lhs is not an integer");
    }
    var v2 = r.eval(s);
    if (!(v2 instanceof IntValue)) {
        throw new RuntimeError("rhs is not an integer");
    }
    return new IntValue(((IntValue) v1).n + ((IntValue) v2).n);
}
```

#### Name-Value Binding

Environment $E$ stores all name-value bindings at runtime. It is queried when evaluating `Name` expressions. A new environment can be created by composing a new binding with a previous environment. There are three places where new binidngs are created: `App`, `Let` and `Rec`. Name bindings created in `eval` method of `App` and `Let` are straightforward. `eval` method of `Rec` also create new binding, but it is stored together with previous environment in `RecValue`, instead of affecting machine state.  

#### Reference Cells 

`Ref`, `Deref` and `Assign` nodes have something to do with reference cells in memory. `Ref` creates new reference cell and assign value to it. `Deref` reads value from a reference cell. `Assign` assigns another value to an exisiting reference cell, overwriting previous value. In my implementation, I abstract basic operations of memory as methods in `Mem` for better readability and extendability: 

```java
public class Mem extends HashMap<Integer, Value> {
    
    public Value read(int ptr) { return get(ptr); }

    public void write(int ptr, Value val) { put(ptr, val); }

    public int alloc(State s) {
        var ptr = p.get();
        p.set(ptr + 1);
        return ptr;
    }
}
```



## Predefined Functions

There are seven predefined functions that I have to implement: four library functions `fst`, `snd`, `hd` and `tl`; three PCF functions `iszero`, `pred` and `succ`. They follow similar implementation patterns. Here I choose `fst` as the example and go through all the work that have to be done.

### Semantics

Semantics for predefined functions are defined by calling the constructor of its super class `FunValue`. Top level functions are in empty environment. Its parameter name can be arbitrary, as long as it is consistent with the one in function body. The internal expresson is anonymous subclass of `Expr`. There is nothing to do with `typeCheck` method because it will never be called. `eval` method is our concern. The implementation is quite straightforward. Finally, the implementation looks like this: 

```java
super(Env.empty, Symbol.symbol("x"), new Expr() {
    Symbol x = Symbol.symbol("x");

    @Override public TypeResult typeCheck(TypeEnv E) {
        return null;
    }

    @Override public Value eval(State s) throws RuntimeError {
        var pairVal = s.E.get(x, s);
        if (!(pairVal instanceof PairValue)) {
            throw new RuntimeError("not a pair");
        }
        return ((PairValue) pairVal).v1;
    }
});
```

### Environment Definition

Predefined functions should be added to environment to make it detectable in later evaluation. This work should be done in `initialEnv`, which is a static method of `InitialState`. 

```java
E = Env.of(E, Symbol.symbol("fst"), new Fst());
```

### Type Definition

Type definition of predefined functions should be added to type environment to ensure the type checking for its call will not fail. This work is done in constructor of `DefaultTypeEnv`. We start by an empty type environment, and successively add type definitions of functions to it. Types of the four library functions are all parameterized types. One thing to be notice is that, if two type parameters are definitely the same, they should be assigned the same type variable. 

```java
public DefaultTypeEnv() {
    // Create empty type environment as the starting point
    E = empty;

    // Add type declarations for predefined functions
    var fstLhs = new TypeVar(true);
    var fstRhs = new TypeVar(true);
    E = of(E, Symbol.symbol("fst"), new ArrowType(new PairType(fstLhs, fstRhs), fstLhs));

    /* Other predefined functions ... */
}
```



## Bonus Features

### Garbage Collection

#### Implementation

In SimPL interpreter, garbage collection means freeing reference cells that can no longer be used, allowing these locations to be allocated again in later evaluation. The key problem is how to know a reference cell is used. In SimPL, we can know this by checking if that reference cell is bound to one or more names. 

#### Demonstration

### Lazy Evaluation

#### Implementation

#### Demonstration

### Polymorphic Type

By implementing type inference, the imterpreter already supports polymorphic type. The details are  discussed in previous sections.



## Appendix

### Notice



### Output

