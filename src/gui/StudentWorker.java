package gui;

public class StudentWorker extends Worker {

    private boolean canOverrideOverdue;

    private boolean canManageStudents, canAddStudents, canEditStudents, canRemoveStudents;

    public StudentWorker(String name, String email, String password) {
        super(name, email, password, false);
        canOverrideOverdue = false;
        canManageStudents = false;
        canAddStudents = false;
        canEditStudents = false;
        canRemoveStudents = false;
    }

    public String writeWorker() {
        return super.writeWorker();
    }

    public boolean canOverrideOverdue() {
        return canOverrideOverdue;
    }

    public void setCanOverrideOverdue(boolean canOverrideOverdue) {
        this.canOverrideOverdue = canOverrideOverdue;
    }

    public boolean canManageStudents() {
        return canManageStudents;
    }

    public void setCanManageStudents(boolean canManageStudents) {
        this.canManageStudents = canManageStudents;
    }

    public boolean canAddStudents() {
        return canAddStudents;
    }

    public void setCanAddStudents(boolean canAddStudents) {
        this.canAddStudents = canAddStudents;
    }

    public boolean canEditStudents() {
        return canEditStudents;
    }

    public void setCanEditStudents(boolean canEditStudents) {
        this.canEditStudents = canEditStudents;
    }

    public boolean canRemoveStudents() {
        return canRemoveStudents;
    }

    public void setCanRemoveStudents(boolean canRemoveStudents) {
        this.canRemoveStudents = canRemoveStudents;
    }
}