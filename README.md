SUMMARY:

This tool is a Program Dependence Graph generator for a given input file in the programming language Java. 
It's wrapped around an easy to use GUI for a better analysis of the code provided to the application through the intermediate
representation of a static PDG.
Feature Set:
-Syntactic checks of the given code(Error reporting through the GUI)
-Semantic checks of the given code(Error reporting through the GUI)
-Generation of a static Program Dependence Graph(Control Flow edges and Data Dependecy edges) for the entire file provided
-Exporting the generated graph into a dotfile format

Syntactic Analysis:

The analysis is performed by the javaparser library used on this project. The execution of the program is terminated upon
the first syntactic error and reported on the program's GUI. 
The javaparser used provides an AST of the input code through the object CompilationUnit.

Table Symbol:

ClassScopes -> has hashtable for methods of a class with name(key) and their return type(value)
	    -> has hashtable with fields
	    -> parameter for className
MethodScopes -> has hashtable for parameters of each method name(key) and their type(value)
             -> has hashtable for localvariables of each method name(key) and their type(value)
	     -> parameter for respective className, methodName and Type
loopScopes   -> has hashtable for localvariables of each loop name(key) and their type(value)
             -> parameter for respective className and methodName
-allows for multiple variable declarations in one statement(ex: String d,e,f,g;), they are all added to the table Symbol

Semantic Analysis:

Variable Declaration duplicates:
-checks for duplicate variable declarations
-checks for duplicate parameters in method Declarations
-checks for duplicated fields in classes

Variable Declaration check in:
-Assignment Expressions(ex:a=a+2)
-Binary Expressions(ex:a<3)
-Unary Expressions(ex:c++)
-Method Call Expressions
-check if they are Parameters or fields(fieldExpressions ex:myclass.field1)

Method Declaration check:

-Will report if a method is calling another undeclared method
-Method can be declared after or before any other function calls it, accurate to Java

Method Arguments check:

-Checks number of arguments and  if they are declared
-supports object function calls(example: Person p; p.getName())

Method Return type check;
-checks if functions return variable is of the same type as the method declaration type(not checking literals)

**INTERMEDIATE REPRESENTATIONS (IRs): (for example, when applicable, describe the HLIR (high-level IR) and the LLIR (low-level IR) used
- FILL IN WITH GRAPH STUFF


**OVERVIEW: (refer the approach used in your tool, the main algorithms, the third-party tools and/or packages, etc.)
For the syntactic analysis, an open source parser of Java was used as described above.
Recursive algorithmns were used to process each node of the AST, branch by branch and to perform the 
construction of the Symbol Table, the semantic analysis and graph generation on a node by node basis.
-Talk about the use of JGraphT

**TESTSUITE AND TEST INFRASTRUCTURE: (Describe the content of your testsuite regarding the number of examples, the approach to automate the test, etc.)
Certain test files with Java code were used to test the semantic features and graph generation of the program, no automated tests were used.
These test files were specifically designed to test each feature and nuance of the program and very effective in bug detection and consequent
fixing.

**TASK DISTRIBUTION: (Identify the set of tasks done by each member of the project.)


**PROS: (Identify the most positive aspects of your tool)


**CONS: (Identify the most negative aspects of your tool)

The semantic analysis for the Java language is very complex, the error reporting is not very extensive as a result. The
HashTable data structures used were a limitation when it came to the symbol table of method parameters, as type checking
of arguments in a method call required an ordered set, this was only discovered very late into development and the
amount of code restructuring needed was going to be too large to make the deadline, as a result type checking for method
call arguments had to be scraped. 



