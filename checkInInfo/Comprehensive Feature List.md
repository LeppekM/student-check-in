# Feature List #

1. Manage database containing information associated with: Parts (and kits), Students, Workers/Employees, Checkout/Checkin instances, and Part Vendors
    - For more information on database structure go to /checkInInfo/Student_Check_IN_ERD.mwb
2. Login as worker with associated permissions
3. Login with RFID or msoe email
4. Check in / out parts (and kits) from inventory to a specific student (or whoever is checking in/out a part ie: professor)
    - Prevent checkout of parts already checked out by other students
    - Automatically assign a due date based on current date
5. Display what parts a student already has checked out
   - Specify if the checked out parts are overdue
6. Allow extended checkout for parts given that the student has permission from a professor (with course code) to push back the due date
7. Track the inventory 