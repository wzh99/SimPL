# SimPL Interpreter

###### 王梓涵　517021911179

## Introduction

### Objective

In this project I am asked to implement an interpreter for the programming language SimPL. SimPL is a simplified dialect of ML, which can be used for both functional and imperative programming. 

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

Primitive types cannot contain any type variable, and they cannot be replaced. Compound types may contain type variables, depending on whether its components contains that. And it can call `replace` on its components, and then combine the resulting substitutions. Type variable contains another type variable if it shares the same name with that, and it replaces by changing its identifier to the one of that.

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

Memory $M$ stores mappings from integer addresses to values. Memory pointer $p$ stores the address for next reference cell. It can also be understood as the number of total reference cells at that time. This pointer helps allocation of new reference cells. 

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

Environment $E$ stores all name-value bindings at runtime. It is queried when evaluating `Name` expressions. A new environment can be created by composing a new binding with a previous environment. There are three places where new bindings are created: `App`, `Let` and `Rec`. Name bindings created in `eval` method of `App` and `Let` are straightforward. `eval` method of `Rec` also create new binding, but the binding is stored in environment of the closure, instead of altering machine state.  

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

Semantics for predefined functions are defined by calling the constructor of its super class `FunValue`. Top level functions are in empty environment. Its parameter name can be arbitrary, as long as it is consistent with the one in function body. The body expresson is an anonymous subclass of `Expr`. There is nothing to do with `typeCheck` method because it will never be called. `eval` method is our concern. The implementation is quite straightforward. Finally, the implementation looks like this: 

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

In SimPL interpreter, garbage collection means freeing reference cells that can no longer be used, allowing these locations to be allocated again in later evaluation. The key problem is how to know a reference cell is used. In SimPL, a reference is in use means it is bound to one or more names. The most  convenient way to know this is to check the environment. If the environment contains mapping to this reference cell, it is used. Otherwise, it is not, and we can free this cell for a later allocation. 

Thanks to encapsulation work done before, only `alloc` method in `Mem` needs to be modified. Mark-sweep algorithm is implemented here. The method iterates through all the name-value bindings in the environment, and marks all the cells in use. If all cells are in use, it increments the address counter and return address for the new cell. Otherwise, it returns the first cell not in use. The code is shown as follows:

```java
public int alloc(State s) {
    // Mark all cells that are bound to a name
    var marked = new TreeSet<Integer>();
    var env = s.E;
    while (env != null) {
        if (env.v instanceof RefValue)
            marked.add(((RefValue) env.v).p);
        env = env.E;
    }

    // Extend address space if all cells are in use
    if (marked.size() == s.p.get()) {
        var ptr = s.p.get();
        s.p.set(ptr + 1);
        return ptr;
    }

    // Get the first available cell in current address space
    for (int i = 0; i < s.p.get(); i++) {
        if (!marked.contains(i))
            return i;
    }
    throw new RuntimeException("unreachable");
}
```

#### Result

Consider program `gc.spl`:

```ocaml
let a = ref 0 in
    let b = ref 1 in
        b := 2
    end;
    let d =
        let c = ref 3 in
            ref 4;
            ref 5;
            c
        end in
        !d
    end
end
```

This example demonstrates several possible cases where a reference cell could be used. The first cell is in use throughout the whole program. The second is bound to `b` but later lives out its scope. The third is bound to `c` first, and then `d`. The fourth and fifth are never bound to any name. Without GC, five difference cells are created. But with GC, only three are actually created. 

Let's do a little hack on the `eval` method of `Ref` and see what happens:

```java
@Override public Value eval(State s) throws RuntimeError {
    var cellVal = e.eval(s);
    var ptr = s.M.alloc(s);
    s.M.write(ptr, cellVal);
    System.out.println("ref@" + ptr); // print address of allocated cell
    return new RefValue(ptr);
}
```

Interesting part of output:

```
ref@0
ref@1
ref@1
ref@2
ref@2
```

It can be seen that only three different cells are allocated. GC works properly. 

### Lazy Evaluation

#### Implementation

In eager (call-by-value) evaluation strategy, $E$ stores mappings from name to value. However, in lazy evaluation, $E$ could also store mappings from name to expression *and* environment. For any expression whose value should be bound to a name in eager evaluation, we directly create a mapping from that name to the expression, plus the environment required to evaluate this expression. When a name is being evaluated, the interpreter should first evaluate that expression with corresponding environment, replace that entry with name-value binding, and finally return the value.

The implementation is divided to two parts: creation of the mappings and evaluation of mapped expressions. To support this feature, `Env` class should be firstly modified as follows:

```java
public class Env {
    public final Env E;
    private final Symbol x;
    public Value v;
    private final Expr e;
    private final Env Ee;
    
    // Name-expression-environment mapping
    public Env(Env E, Symbol x, Expr e, Env Ee) {
        this.E = E;
        this.x = x;
        this.v = null;
        this.e = e;
        this.Ee = Ee;
    }

    public static Env of(Env E, Symbol x, Expr e, Env Ee) {
        return new Env(E, x, e, Ee);
    }
    
    /* Other members ... */
}
```

Only two places should create this kind of mapping: `App` and `Let`. `Rec` also creates new mapping, but it actually maps to a `RecValue`, and no expression or environment is involved. Evaluation of mapped expression is done in `get` method of `Env`. The algorithm is already stated, so I just show the code here: 

```java
 public Value get(Symbol y, State s) throws RuntimeError {
     if (x != y) { // symbol not found at this level
         if (E == null)
             return null;
         else
             return E.get(y, s);
     }
     if (v == null) { // not evaluated yet
         assert e != null;
         v = e.eval(State.of(Ee, s.M, s.p));
     }
     return v;
 }
```

#### Result

Run all the provided test cases with lazy evaluation and GC enabled. 

```
doc/examples/plus.spl
int
3
doc/examples/factorial.spl
int
24
doc/examples/gcd1.spl
int
1029
doc/examples/gcd2.spl
int
0
doc/examples/max.spl
int
2
doc/examples/sum.spl
int
6
doc/examples/map.spl
((tv59 -> tv66) -> (tv59 list -> tv66 list))
fun
doc/examples/pcf.sum.spl
(int -> (int -> int))
fun
doc/examples/pcf.even.spl
(int -> bool)
fun
doc/examples/pcf.minus.spl
int
46
doc/examples/pcf.factorial.spl
int
720
doc/examples/pcf.fibonacci.spl
int
6765
```

The output of programs are the same as in eager evaluation, except for `gcd2.spl` (see Appendix for reference). It is expected to output `1029`, but we got `0` instead. Let's consider this program:

```ocaml
let gcd = fn x => fn y =>
    let a = ref x in
    	let b = ref y in
    		let c = ref 0 in
    			(while !b <> 0 do c := !a; a := !b; b := !c % !b);
    			!a
    		end
    	end
    end
in  gcd 34986 3087
end
```

Since it is an imperative program, it is likely that the use of reference cells causes this problem. Let's take a look at address of each allocation, just like in the previous section:

```
ref@0
ref@1
ref@0
```

In call-by-value evaluation, three different cells should be created, but here we only got two. The first cell is bound to `b` in when evaluating `!b <> 0`, the second bound to `c` and the third to `a` when evaluating `c := !a`. The reason why `a` and `b` share the same cell is that the environment for `ref x` contains nothing. When evaluating `!a`, the allocation procedure with GC takes it for granted that all cells are not in use, so it assigns `ref@0` to `a`. If we follow the code, it is easy to find that value stored in `ref@0` is zero in the first iteration of `while` loop, and it exits the loop because `!b` is also zero. By `!a`, we get result `0` for this program.  

We can draw a conclusion that GC could lead to surprising result for an imperative program in lazy evaluation. If we disable GC, and still run `gcd2.spl` in lazy mode, the result is correct:

```
doc/examples/gcd2.spl
int
1029
```

### Mutually Recursive Combinator

#### Design

I have to extend the original SimPL definition to support this feature. When designing this syntax feature, I refer to [F# language reference](https://docs.microsoft.com/en-us/dotnet/fsharp/language-reference/functions/recursive-functions-the-rec-keyword). In F#, the syntax for MRC looks like this:

```F#
let rec function1-nameparameter-list =
function1-body
and function2-nameparameter-list =
function2-body
```

Combining this definition and characteristics of SimPL, I design syntax for MRC as follows:
$$
\begin{align}
e::=\ &\dots &expressions \\
|\quad&\mathtt{let}\ x=e\ \mathtt{and}\ x=e\ \mathtt{in}\ e\ \mathtt{end} &mutually\ recursive\ combinator
\end{align}
$$
The evaluation rule is similar to E-Let, but somehow trickier than that. It seems that when evaluating $e_1$, $E$ have to contain mapping from $y$, and it requires $e_2$ to be evaluated first. But evaluation of $e_2$ also needs $x$ from environment, which requires $e_1$ to be evaluated first. Circularity is intrinsic for MRC here. There's a way to solve this. Since $e_1$ must be a function definition, it could evaluates to a closure, whose environment just contains neither $x$ or $y$. After evaluating $e_1$ and $e_2$ to two closures, add two mappings $x\mapsto(\mathtt{fun},E',s,e_1')$ and $y\mapsto(\mathtt{fun},E',t,e_2')$ to the environment, with the environment of both closures the newly created environment. That's why $E'$ appears on both sides of the third premise. 
$$
\frac{E,M,p;e_1\Downarrow M',p';(\mathtt{fun},E_1,s,e_1')\quad E,M',p';e_2\Downarrow M'',p'';(\mathtt{fun},E_2,t,e_2') \\ 
E'=E[x\mapsto(\mathtt{fun},E',s,e_1')][y\mapsto(\mathtt{fun},E',t,e_2')]\quad E',M'',p'';e_3\Downarrow M''',p''';v}
{E,M,p;\mathtt{let}\ x=e_1\ \mathtt{and}\ y=e_2\ \mathtt{in}\ e_3\Downarrow M''',p''';v}
\tag{E-LetAnd}
$$
Similar circularity problem arises in terms of typing rule. We can solve this by assuming the type form of $e_1$ and $e_2$. Since $e_1$ and $e_2$ are all functions, they must have form $t\rightarrow t$. We can assume $e_1$ to be of type $t_1\rightarrow t_2$ and $e_2$ of type $t_3\rightarrow t_4$, where $t_i,i\in1..4$ are type variables, instead of concrete types. Types can be checked for $e_1$ and $e_2$, whose results are $t_1'\rightarrow t_2'$ and $t_3'\rightarrow t_4'$ respectively. Then we check type of $e_3$ with mapping $x:t_1'\rightarrow t_2'$ and $y:t_3'\rightarrow t_4'$, resulting $t$, which is the type for the whole expression. 
$$
\frac{\Gamma[y:t_3\rightarrow t_4]\vdash e_1:t_1'\rightarrow t_2'\quad \Gamma[x:t_1\rightarrow t_2]\vdash e_2:t_3'\rightarrow t_4'\quad \Gamma[x:t_1'\rightarrow t_2'][y:t_3'\rightarrow t_4']\vdash e_3:t}
{\Gamma\vdash \mathtt{let}\ x=e_1\ \mathtt{and}\ y=e_2\ \mathtt{in}\ e_3:t} 
\tag{T-LetAnd}
$$

#### Implementation

Since the syntax is defined by myself, I have to modify the grammar file and regenerate lexer and parser classes for the new grammar. In file `simpl.lex`, I add `and` keyword in `<YYINITIAL>` block. This enables lexer to recognize this keyword as a token.

```
<YYINITIAL> {
	...
	"and"     { return token(AND); }
	...
}
```

In `simpl.grm`, I first add `AND` as a terminal:

```
terminal LET, AND, IN, END;
```

Then specify the syntax of MRC:

```
e :== ...
	| LET ID:x EQ e:e1 AND ID:y EQ e:e2 IN e:e3 END {: RESULT = new LetAnd(symbol(x), e1, symbol(y), e2, e3); :}
	;
```

In package `parser.ast`, write a new class `LetAnd`:

```java
public class LetAnd extends Expr {
    public Symbol x, y;
    public Expr e1, e2, e3;

    public LetAnd(Symbol x, Expr e1, Symbol y, Expr e2, Expr e3) {
        this.x = x;
        this.y = y;
        this.e1 = e1;
        this.e2 = e2;
        this.e3 = e3;
    }
    
    /* Other methods */
}
```

Then run the `Makefile` script in `parser` directory, `Lexer` and `Parser` are automatically generated in this directory. The rest work is to implement `typeCheck` and `eval` methods. The implementation just follows the rules stated before.

```java
@Override public TypeResult typeCheck(TypeEnv E) throws TypeError {
    // Bind parametric arrow types to `x` and `y`
    var xTy = new ArrowType(new TypeVar(true), new TypeVar(true));
    var yTy = new ArrowType(new TypeVar(true), new TypeVar(true));

    // Check types of both recursive functions
    var env = TypeEnv.of(TypeEnv.of(E, x, xTy), y, yTy);
    var e1Tr = e1.typeCheck(env);
    var e2Tr = e2.typeCheck(env);
    var subst = e1Tr.s.compose(e2Tr.s);
    xTy = (ArrowType) subst.apply(xTy);
    yTy = (ArrowType) subst.apply(yTy);

    // Check type of expression
    var e3Tr = e3.typeCheck(TypeEnv.of(TypeEnv.of(E, x, xTy), y, yTy));
    subst.compose(e3Tr.s);
    return TypeResult.of(subst, subst.apply(e3Tr.t));
}

@Override public Value eval(State s) throws RuntimeError {
    // Evaluate either function without binding
    var v1 = e1.eval(s);
    if (!(v1 instanceof FunValue))
        throw new RuntimeError("v1 is not a function");
    var v2 = e2.eval(s);
    if (!(v2 instanceof FunValue))
        throw new RuntimeError("v2 is not a function");

    // Modify closures to create circular name-value binding
    var env = Env.of(Env.of(s.E, x, v1), y, v2);
    ((FunValue) v1).E = env;
    ((FunValue) v2).E = env;

    // Evaluate the rest part
    return e3.eval(State.of(env, s.M, s.p));
}
```

#### Result

Consider program `mrc.even.spl` which decides whether a number is even or odd:

```ocaml
let iseven = fn n =>
    if iszero n then true else isodd (pred n)
and isodd = fn n =>
    if iszero n then false else iseven (pred n)
in iseven 3
end
```

This program contains two mutually recursive functions: `iseven` and `isodd`. I pass `3` to `iseven` so that either function will be called at least twice. This could test whether the environment is correct. Run the interpreter on this program and it outputs:

```
doc/examples/mrc.even.spl
bool
false
```

The typing and evaluation results are all correct. This feature is properly implemented.

### Polymorphic Type

By implementing type inference, the interpreter already supports polymorphic type. The details are  discussed in previous sections.



## Appendix

### Notice

* If there are too many levels of recursion in input program, JVM could possibly throws `StackOverflow` exception. If you are sure that the program will not cause infinite recursion, try set stack size of JVM larger through `-Xss` argument, for example `-Xss8m` if a stack of 8MB is desired. 

* Two bonus features: GC and lazy evaluation, can be enabled and disabled by setting constants in `Feature` class in package `simpl.interpreter`. Whether to enable these features should be decided before compilation. Mutually recursive combinator just works in eager evaluation.

    ```java
    public class Feature {
        // Whether to enable garbage collection
        public static final boolean GC = true;
        // Whether to enable lazy evaluation
        public static final boolean LAZY = false;
    }
    ```

### Output

Output of all provided test cases with eager (call-by-value) evaluation strategy and GC enabled. The exported `jar` also uses this configuration.

```
doc/examples/plus.spl
int
3
doc/examples/factorial.spl
int
24
doc/examples/gcd1.spl
int
1029
doc/examples/gcd2.spl
int
1029
doc/examples/max.spl
int
2
doc/examples/sum.spl
int
6
doc/examples/map.spl
((tv59 -> tv66) -> (tv59 list -> tv66 list))
fun
doc/examples/pcf.sum.spl
(int -> (int -> int))
fun
doc/examples/pcf.even.spl
(int -> bool)
fun
doc/examples/pcf.minus.spl
int
46
doc/examples/pcf.factorial.spl
int
720
doc/examples/pcf.fibonacci.spl
int
6765
```

