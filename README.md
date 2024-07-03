# README #

This README documents steps which are necessary to get your application up and running.

### What is this repository for? ###

This is the check-in/check-out system that keeps track of parts belonging to the ECBE TSC.

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

Original developers are Daniel Lang, Matthew Karcz, Bailey Terry, and Joe Gilpin. Code was updated in 2024 by Martina Leppek.

Product Owner: Jim Frommell

## Known Issues ##
* Sometimes inventory tables show parts with only name fields filled while everything else shows as null, these parts cannot be edited
* Some parts do not have a popup when double-clicked in inventory screen, assumed due to deleting history associated with part
* Some tables auto-double click on single click or after sort function

## Priority List ##
* allow smarter clear history
  * Potentially allow adjustable dates for this
* improve searches with space delim
* QA test table functionality
* Testing suite
* Scan all of part type in inventory
* scrollbar for checkin/out page (or other fix to infinite barcode smush problem) (like if you press enter on empty barcodefield it submits)
* Clean formatting / code conformity
