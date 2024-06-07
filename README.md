# README #

This README documents steps which are necessary to get your application up and running.

### What is this repository for? ###

This is the check-in/check-out system that keeps track of parts belonging to the EECS TSC.

### How do I get set up? ###

* Summary of set up:
  * Set-up instructions are shown with pictures in Inventory set up guide.pdf
* Configuration
* Dependencies
  * Gradle, JDK 8, JavaFX 
* Database configuration
* How to run tests
* Deployment instructions

### Contribution ###

Original developers are Daniel Lang, Matt, Bailey Terry, and Joe Gilpin. Code was updated in 2024 by Martina Leppek.

Product Owner: Jim Frommell

## Known Issues ##
* Cannot delete student with no transaction history, shows up as having a part checked out
* Sometimes inventory tables show parts with only name fields filled while everything else shows as null, these parts cannot be edited
