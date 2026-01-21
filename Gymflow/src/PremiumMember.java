public class PremiumMember extends Member {
    private boolean hasPersonalTrainer;

    public PremiumMember(String name, String id, String phone, boolean hasPersonalTrainer) {
        super(name, id, phone);
        this.hasPersonalTrainer = hasPersonalTrainer;
    }

    public boolean hasPersonalTrainer() {
        return hasPersonalTrainer;
    }

    public void setHasPersonalTrainer(boolean hasPersonalTrainer) {
        this.hasPersonalTrainer = hasPersonalTrainer;
    }

    @Override
    public double calculateFee() {
        double baseFee = 5000.0;
        if (hasPersonalTrainer) {
            return baseFee + 3000.0; // Extra charge for trainer
        }
        return baseFee;
    }
}