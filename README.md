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

Original developers are Daniel Lang, Matthew Karcz, Bailey Terry, and Joe Gilpin. Code was updated in 2024 by Martina Leppek.

Product Owner: Jim Frommell

## Known Issues ##
* Sometimes inventory tables show parts with only name fields filled while everything else shows as null, these parts cannot be edited
* Some parts do not have a popup when double-clicked in inventory screen, assumed due to deleting history associated with part
* Some tables auto-double click on single click or after sort function

## Priority List ##
* allow smarter clear history
  * Potentially allow adjustable dates for this
* Finish table overhaul
  * Fix leading 00s in one of the table searches
  * Fix double click issues (Overdue table specifically)
* Testing suite
* Documentation, for the love of god
* improve autofill for checkin/out to allow tab to autofill rest of top email
* Create generic popups for fill (?)
* Clean formatting / code conformity
