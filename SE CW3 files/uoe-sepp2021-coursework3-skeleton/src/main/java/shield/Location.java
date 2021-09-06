package shield;

public class Location {
    private String postcode;
    private String address;

    /**
     * Constructor for the Location class
     *
     * @param postcode represents the postcode
     * @param address represents the address (excluding the postcode)
     */
    public Location(String postcode, String address) {
        assert postcode.length() >= 6;
        this.postcode = postcode;
        this.address = address;
    }

    /**
     * Method to check if one location is near to another
     *
     * @param other represents the other location
     * @return True if near to one another otherwise false
     * @throws IllegalArgumentException error if condition is not met
     */
    public boolean isNearTo(Location other) throws IllegalArgumentException {
        if (getPostcode().substring(0,2).equals(other.getPostcode().substring(0,2))) {
            return true;
        }

        else {
            return false;
        }
    }

    public String getPostcode() {
        return postcode;
    }

    public String getAddress() {
        return address;
    }
}
