# COMP - JAST2DyPDG

### Class 3MIEIC01 - Group 4
    Name: Francisco Pinho, Nr: 201303744, Grade: 17, Contribution: 25%
    Name: Francisco Rodrigues, Nr: 201305627, Grade: 17, Contribution: 25%
    Name: João Nogueira, Nr: 201303882, Grade: 17, Contribution: 25%
    Name: Marta Lopes, Nr: 201208067, Grade: 17, Contribution: 25%

## SUMMARY

This tool is a **Program Dependence Graph** generator for a given input file in the programming language _Java_ that can be outputed as a dot file. 
It's wrapped around an easy to use GUI for a better analysis of the code provided to the application through the intermediate representation of a **PDG**.

### Feature Set:

* Syntactic checks of the given code(Error reporting through the GUI);
* Semantic checks of the given code(Error reporting through the GUI);
* Generation of a Program Dependence Graph(Control Flow edges and Data Dependecy edges) for the entire file provided;
* Exporting the generated graph into a dot file format.

## Syntactic Analysis

The analysis is performed by the _javaparser library_ used on this project. The execution of the program is terminated upon
the first syntactic error and reported on the program's GUI. 
The javaparser used provides an AST of the input code through the object CompilationUnit.

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

### Symbol Table

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

### PDG - Program Dependence Graph

To develop the graph in the Java application we decided to use a **DirectedGraph** object with edges with more information than the one allowed by the **DefaultEdge** class. Therefore we created a subclass called **RelationshipEdge**.

To fill the graph with the needed information we took advantage of the semantic analysis algorithm. This algorithm goes through the AST to verify specific criteria spread throughout the nodes. As a result, the filling of the graph is mixed with the semantic analysis.

#### FDG - Flow Depenence Graph

To implement the **Flow Dependence Graph** we had to keep a record of all accesses and definitions of all variables. To do so, we had to create a superclass of all the scope classes (**LoopScope**, **ClassScope** and **MethodScope**) to guarantee that all Scopes have an **ArrayList** containing all the changed and accessed variables in its own Scope.
```java
class Scope {
	ArrayList<VarChanges> varChanges = new ArrayList<>();
	ArrayList<VarChanges> varAccesses = new ArrayList<>();
}
```
Each time a variable is accessed, it is stored in the actual scope's array and a search is done through the scopes we're inside in the moment. When a definition is found a new edge is added. As the algorithm is recursive when a scope ends then the scope is no longer inside the **ArrayList**.

#### CDG - Control Dependence Graph

The **Control Dependence Graph** was filled in the order already provided by the AST. As said before, we use a recursive algorithm which allows us to fill this branch of the graph quite effectively without the need to create specific structures for its implementation.

## Overview

For the syntactic analysis, an open source parser of Java was used as described above.
Recursive algorithmns were used to process each node of the AST, branch by branch and to perform the construction of the Symbol Table, the semantic analysis and graph generation on a node by node basis.

To be able to display the graph we needed to convert our **DirectedGraph** to a **JGraphT**. To do so we had to override the **toString** and **equals** functions. It's override was also useful to export the graph into _.dot_ files as they do not accept some of the carracters displayed in the graph.

## TestSuite and Test Infrastructure

Certain test files with Java code were used to test the syntatic and semantic features and graph generation of the program, no automated tests were used.
These test files were specifically designed to test each feature and nuance of the program and very effective in bug detection and consequent fixing.

## Task Distribution

Overall we consider that all the group elements worked equally to the delivery of this assignment.

* Francisco Pinho - Parser Interpretation, TableSymbol, Semantic;
* Francisco Rodrigues - Parser Interpretation, AST, Graph;
* João Nogueira - AST, Graph, Semantic;
* Marta Lopes - AST, TableSymbol, Semantic.

        NOTE: We consider that the GUI was developed by all members of the group.

## Pros

* Our application helps the user understand what a **Program Dependence Graph** is and what it is supposed to represent. The fact that it shows the different edges as **Flow Dependencies** or **Control dependencies** allows us to understand the difference between node interactions, and understand how all the variables, methods and classes works in Java. It has a complete syntactic and lexical analysis implemented with the parser and a semantic analysis done by us, covering all the major aspects of this language, if any error occurs in any of this analysis the user will be notified in the GUI.
* The user-friendly GUI allows any user to use our application without any difficulty, regardless of the previous usage of the application.
* The built-in console allows the user to quickly acknowledge what the errors are and where they are located.
* In conclusion, a **Program Dependence Graph** has many uses in code analysis, particularly when it comes to its optimization.

## Cons

The semantic analysis for the Java language is very complex, the error reporting is not very extensive as a result. The HashTable data structures used were a limitation when it came to the symbol table of method parameters, as type checking of arguments in a method call required an ordered set, this was only discovered very late into development and the amount of code restructuring needed was going to be too large to make the deadline, as a result type checking for method call arguments had to be scraped. 
