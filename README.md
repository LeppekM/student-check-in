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

### How do I update the code on the laptops? ###

This is a very analog process as the laptops are not hooked up to the internet.

1. Build the .jar file in gradle (should be under Gradle: student-check-in-gradle/javafx/jfxJar)
2. Get the built .jar file and dependencies from build/jfx/app onto a flash drive or other file transfer device
3. Plug the flash drive into the laptops up front
4. Right-click the program shortcut and select 'Open file location'
5. Transfer the newly built dependencies into the folder it opens and re-open the app
   - You might also want to back up the old versions of the files on the laptops before overriding, just in case

### Contribution ###

Original developers are Daniel Lang, Matthew Karcz, Bailey Terry, and Joe Gilpin. Code was updated in 2024 by Martina Leppek.

Product Owner: Jim Frommell

## Known Issues ##
* Sometimes inventory tables show parts with only name fields filled while everything else shows as null, these parts cannot be edited
* The auto-submit function for Checkin/out attempts to run on any page that is open 5 minutes after no input is detected, this only causes the side popup to appear on any screen other than checkin/out
* There is some amount of lag/loading time when email is used instead of RFID when checking in parts
* 

## Priority List ##
* Documentation
* Testing suite
