---
name: spec-reading
description: skill that explains how to properly go over specs and implement them using correct development workflow
---

# Overview

This skill explains how to properly implement new features and solve open issue of this project while following correct development work flows

# Steps

## Understanding the available tasks

all the features and issue of the project will be inside the folder named 'SPECS'. inside that folder there will be a catalog file.
the catalog file will contain a table with all the specs that needs to be implemented
each task will have number, title, filename, priority, status, branch name, pr number
each task will have it own file inside the specs folder with more details about the task

## Deciding on what task to work

after you saw the available tasks you now have 3 options to choose from according to the users request.

1. go over them one by one, by order of priority and implement all of them
2. implement a specific one the user requested
3. implement something else

make sure you do not work on finished tasks. look at the status in the catalog for this info


## Work flow of working on one task

when working on a task you should follow this workflow exactly

1. change the status in the catalog to in progress
2. read the task's file to fully understand the requirements
3. switch branches to master
4. create a new branch for the task
5. implement the requirements
6. write tests that verify that every thing working as expected (when possible)
7. update the status in the catalog to in review
8. commit the changes. include the catalog and any spec changes
9. create a pr for this branch
10. update the catalog with the pr number and the branch name
11. commit the updates to the catalog

# guidelines

* always make sure everything works as expected
* each pr should be small yet complete. for example if adding an infra of some sort, add the most basic functionality and one usage of it to make sure its work
* do not forget tests 