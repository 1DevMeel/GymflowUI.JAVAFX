public class StudentMember extends Member {
    private String universityName;

    public StudentMember(String name, String id, String phone, String universityName) {
        super(name, id, phone);
        this.universityName = universityName;
    }

    public String getUniversityName() {
        return universityName;
    }

    public void setUniversityName(String universityName) {
        this.universityName = universityName;
    }

    @Override
    public double calculateFee() {
        return 2500.0; // Discounted fee for students
    }
}