public abstract class Member {
    private String name;
    private String id;
    private String phone;
    private double discount = 0.0;

    public Member(String name, String id, String phone) {
        this.name = name;
        this.id = id;
        this.phone = phone;
    }

    // Abstract method (Polymorphism ke liye)
    public abstract double calculateFee();

    public double getNetFee() {
        return Math.max(0, calculateFee() - discount);
    }

    // Getters
    public String getName() { return name; }
    public String getId() { return id; }
    public String getPhone() { return phone; }
    public double getDiscount() { return discount; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setId(String id) { this.id = id; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setDiscount(double discount) { this.discount = discount; }
}