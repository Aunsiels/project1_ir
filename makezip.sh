#!/bin/bash
# Information Retrieval, Project 1. Group 11 (Merki, Romero, Greiner).
# Everything has to be zipped to a directory called 'code'
# Create the zip package by first zipping the relevant folders, then unzipping them to 'code', then zipping again.
zip -rX temp.zip build.sbt lib src README.md
unzip temp.zip -d code
zip -rX project1_group11.zip code
rm -rf code