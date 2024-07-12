# Feature List #

1. Manage database containing information associated with: Parts (and kits), Students, Workers/Employees, Checkout/Checkin instances, and Part Vendors
   - For more information on database structure go to /checkInInfo/Student_Check_IN_ERD.mwb
2. Login as worker with associated permissions:
   - Editing other workers
   - Editing/adding parts
   - Removing parts
   - Some workers are administrators who have all of the above permissions and the ability to grant permission to grant non-admins the above permissions via a pin, as well as clear old history and unused students
3. Login with RFID or msoe email
4. Check in / out parts (and kits) from inventory to a specific student (or whoever is checking in/out a part ie: professor)
    - Prevent checkout of parts already checked out by other students
    - Automatically assign a due date based on current date
5. Display what parts a student already has checked out
   - Specify if the checked out parts are overdue
6. Allow extended checkout for parts given that the student has permission from a professor (with course code) to push back the due date
7. Track all parts that are inventoried in the system, including displaying the last time the part had been checked out and by whom
8. Add more parts to the system with the following information:
   - Part name
   - Barcode
   - Serial number
   - Physical location
   - Price
   - Manufacturer / vendor
9. Add batches of parts with the same name either with the same barcode or with different barcodes AND serial numbers
10. Edit individual parts 
11. Edit all parts with the same name
12. Delete individual parts 
13. Delete all parts with the same name 
14. Track and display all transaction history, including whether the transaction was to check in or out an item, when, and by whom 
15. Clear old transaction history where the part was checked in longer than 2 years ago 
16. Display all parts that are currently checked out, when they are due, and who has them checked out 
17. Display all parts that are overdue, who has them checked out, and when they were due 
18. The ability to export to an Excel spreadsheet total inventory, transaction history, checked out parts, and overdue parts 
19. Can inventory all of one type of part by searching its part name, and scanning barcodes or manually checking if the part is present 
20. Display all students in system with the following information:
    - Name (as FirstName LastName)
    - RFID
    - Email
    - Currently checked out parts
    - Currently overdue parts
21. Import a batch of students from a .csv or spreadsheet
22. Clear students with no transaction history, including having no parts currently checked out 
23. Add individual students 
24. Delete individual students
25. Display all workers currently in system
26. Add individual workers with the following information:
    - MSOE email
    - First Name
    - Last Name
    - 8+ character password
    - RFID
27. Add admins with all the same information and a 4 digit PIN
28. Delete individual workers
29. Switch which worker is currently logged in by logging out and redirecting to login screen