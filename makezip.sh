#!/bin/bash
# Information Retrieval, Project 1. Group 11 (Merki, Romero, Greiner).
# Everything has to be zipped to a directory called 'code'

# create the readme.pdf
./makepdf.sh

# Create the zip package by first zipping the relevant folders, then unzipping them to 'code', then zipping again.
rm temp.zip project1_group11.zip
zip -rX temp.zip build.sbt lib src README.md README.pdf ir-2016-1-report-11.pdf labelingtestdocs
unzip temp.zip -d project1_group11
zip -rX project1_group11.zip project1_group11 
rm -rf project1_group11
rm -rf temp.zip
