# SSP-Client
Simple Storage Platform Client

# Overview
This project was created in order to practice development using Java Spring boot, MongoDB and containerization.
*** This project is a POC-level only code, not intended for any real-world use *** 

This client interacts with SSP-Server and provides a simple set of REST APIs enabling the creating of files
and dirs on the server, as well as the downloading files, and removing files and dirs.
The main class of the project is basically a test.

# Usage
Pass 3 arguments:
- A path to a file which will be replicated (not a large file)
- A path in which the replicated files will be created
- A path in which files will be downloaded from the server to