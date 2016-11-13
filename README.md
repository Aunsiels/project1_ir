# Information Retrival Project 1 Group 11

Michael Merki, Julien Romero, Markus Greiner

November 14, 2016

## Installation

Provided is the zip file `project1_group11.zip`. Unzip it:

    $ unzip project1_group11.zip
    
This creates the directory `code`. Then run:

    $ cd code
    $ sbt "run-main Main <path-to-zip-folder>"
    
The `<path-to-zip-folder>` must contain the directories `train`, `test`, and `validate`.
 
Upon running the program, it will

- *Naive Bayes Classifier*: train and generate list of tested documents and their codes
- *Logistic Regression Classifier*: train and generate the list
- *SVM*: train and generate the list.

The project report is under `ir-2016-1-report-11.docx`

