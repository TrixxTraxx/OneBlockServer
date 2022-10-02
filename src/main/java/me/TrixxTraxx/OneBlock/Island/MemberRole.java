package me.TrixxTraxx.OneBlock.Island;

public enum MemberRole
{
    Admin,
    Member(Admin),
    Invited(Member);

    MemberRole next = null;

    MemberRole() {
        this.next = this;
    }

    MemberRole(MemberRole next) {
        this.next = next;
    }

    public MemberRole getNext() {
        return next;
    }
}
