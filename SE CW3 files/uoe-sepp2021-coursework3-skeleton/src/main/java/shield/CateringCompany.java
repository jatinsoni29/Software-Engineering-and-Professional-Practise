package shield;

import java.util.Objects;


public class CateringCompany {
    private String name;
    private String postcode;

    /**
     * Constructor for the CateringCompany class
     * @param name represents the name of the catering company
     * @param postcode represents the postcode of the catering company
     */
    public CateringCompany(String name, String postcode) {
        this.name = name;
        this.postcode = postcode;
    }

    public String getName() {
        return name;
    }

    public String getPostcode() {
        return postcode;
    }


    @Override
    public String toString() {
        return "CateringCompany{" +
                "name='" + name + '\'' +
                ", postcode='" + postcode + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CateringCompany that = (CateringCompany) o;
        return name.equals(that.name) &&
                postcode.equals(that.postcode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, postcode);
    }
}
