# README #

This README documents steps which are necessary to get your application up and running.

### What is this repository for? ###

This is the check-in/check-out system that keeps track of parts belonging to the ECBE TSC.

### How do I get set up? ###

* Summary of set up:
  * Set-up instructions are shown with pictures in Documentation/Inventory set up guide.pdf
* Dependencies:
  * Gradle
  * JDK 8
  * JavaFX

### Contribution ###

Original developers are Daniel Lang, Matthew Karcz, Bailey Terry, and Joe Gilpin. Code was updated in 2024 by Martina Leppek.

Product Owner: Jim Frommell

## Known Issues ##
* Sometimes inventory tables show parts with only name fields filled while everything else shows as null, these parts cannot be edited
* The auto-submit function for Checkin/out attempts to run on any page that is open 5 minutes after no input is detected, this only causes the side popup to appear on any screen other than checkin/out
* 

## Priority List ##
* Documentation
* Testing suite
