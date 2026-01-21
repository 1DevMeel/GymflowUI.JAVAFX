import java.util.ArrayList;
import java.util.List;

public class GymManagement {
    // Encapsulation: List private hai
    private List<Member> membersList;

    public GymManagement() {
        this.membersList = new ArrayList<>();
        // Dummy Data (Viva ke liye pehle se kuch members daal diye)
        addMember(new PremiumMember("Ali Khan", "P001", "0300-1234567", true));
        addMember(new StudentMember("Saad Ahmed", "S001", "0321-9876543", "Al Kawthar Uni"));
    }

    public void addMember(Member m) {
        membersList.add(m);
    }

    public void removeMember(Member m) {
        membersList.remove(m);
    }

    public List<Member> getAllMembers() {
        return membersList;
    }
}