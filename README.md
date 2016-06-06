# COMP - JAST2DyPDG

## SUMMARY

This tool is a **Program Dependence Graph** generator for a given input file in the programming language _Java_. 
It's wrapped around an easy to use GUI for a better analysis of the code provided to the application through the intermediate
representation of a static **PDG**.

### Feature Set:

* Syntactic checks of the given code(Error reporting through the GUI);
* Semantic checks of the given code(Error reporting through the GUI);
* Generation of a static Program Dependence Graph(Control Flow edges and Data Dependecy edges) for the entire file provided;
* Exporting the generated graph into a dotfile format.

## Syntactic Analysis

The analysis is performed by the _javaparser library_ used on this project. The execution of the program is terminated upon
the first syntactic error and reported on the program's GUI. 
The javaparser used provides an AST of the input code through the object CompilationUnit.

## Symbol Table

* ClassScopes	
 * Has hashtable for methods of a class with name(key) and their return type(value);
 * Has hashtable with fields;
 * Parameter for className;
* MethodScopes
 * Has hashtable for parameters of each method name(key) and their type(value);
 * Has hashtable for localvariables of each method name(key) and their type(value);
 * Parameter for respective className, methodName and Type;
* LoopScopes
 * Has hashtable for localvariables of each loop name(key) and their type(value);
 * Parameter for respective className and methodName;
* Allows for multiple variable declarations in one statement(ex: String d,e,f,g;), they are all added to the Symbol Table.

## Semantic Analysis

* Variable Declaration duplicates
 * Checks for duplicate variable declarations;
 * Checks for duplicate parameters in method Declarations;
 * Checks for duplicated fields in classes.

* Variable Declaration check in
 * Assignment Expressions(ex:a=a+2);
 * Binary Expressions(ex:a<3);
 * Unary Expressions(ex:c++);
 * Method Call Expressions;
 * Check if they are Parameters or fields(fieldExpressions ex:myclass.field1).

* Method Declaration check
 * Will report if a method is calling another undeclared method;
 * Method can be declared after or before any other function calls it, accurate to Java.

* Method Arguments check
 * Checks number of arguments and  if they are declared;
 * Supports object function calls(example: Person p; p.getName()).

* Method Return type check
 * Checks if functions return variable is of the same type as the method declaration type(not checking literals).

## Intermidiate Representations (IRs)

//FILL IN WITH GRAPH STUFF

## Overview

For the syntactic analysis, an open source parser of Java was used as described above.
Recursive algorithmns were used to process each node of the AST, branch by branch and to perform the construction of the Symbol Table, the semantic analysis and graph generation on a node by node basis.

//Talk about the use of JGraphT

## TestSuite and Test Infrastructure

Certain test files with Java code were used to test the syntatic and semantic features and graph generation of the program, no automated tests were used.
These test files were specifically designed to test each feature and nuance of the program and very effective in bug detection and consequent fixing.

## Task Distribution

Overall we consider that all the group elements worked equally to the delivery of this assignment:

* Francisco Couto - GUI, AST, TableSymbol, Semantic (25% of Total Work);
* Francisco Rodrigues - Parser Interpretation, GUI, AST, Graph (25% of Total Work);
* João Nogueira - AST, Graph, TableSymbol, Semantic (25% of Total Work);
* Marta Lopes - Parser Interpretation, TableSymbol, Semantic, AST (25% of Total Work).

## Pros


## Cons

The semantic analysis for the Java language is very complex, the error reporting is not very extensive as a result. The HashTable data structures used were a limitation when it came to the symbol table of method parameters, as type checking of arguments in a method call required an ordered set, this was only discovered very late into development and the amount of code restructuring needed was going to be too large to make the deadline, as a result type checking for method call arguments had to be scraped. 
