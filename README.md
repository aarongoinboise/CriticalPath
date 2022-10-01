# CriticalPath
Finds the Critical Path of a topological sequence. Prints the earliest completion time, latest completion time, and SlackTime for each node in the sequence. Properly formatted text files are requied for usage, and four are provided in this directory.

## Usage
java CriticalPath <file name>

file name (required): the name of a file containing a correctly formatted and evenly spaced
adjacency-matrix, where the axes are equivalent and have all of the nodes, -1 represents
an edge that doesnt exist, and all other values represent non-negative edge weights between
existing nodes. All values in the S column must be -1, and all values in the F row must be
-1, and all values in the F column must be -1 or 0. All non-negative numbers in rows should
be next to each other, no node should point to a previous node,
and each node must only be one letter.

Example:\
___S__a__F\
S_-1__2__0\
a_-1_-1__0\
F_-1_-1_-1\
  
I would recommend having a basic proficency on Critical Paths and Directed Acylic Graphs before using this program.

## Compiling and Using
To compile, execute the following command in the main project directory:
```
$ javac CriticalPath.java
```

Run the compiled classes with the command:
```
$ java CriticalPath
```
