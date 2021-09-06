package shield;

import java.util.ArrayList;
import java.util.Objects;

public class Order {

    private String individualId;
    private int orderId;
    private int cateringId;
    private int status;
    private ArrayList<FoodItem> contents;

    /**
     * Constructor for the Order class
     *
     * @param individualId represents the shielding individuals id
     * @param orderId represents the order id
     * @param status represents the status of the order
     * @param contents is the arraylist of type FoodItem
     */
    public Order(String individualId, int orderId, int status, ArrayList<FoodItem> contents) {
        this.individualId = individualId;
        this.orderId = orderId;
        this.status = status;
        this.contents = contents;
    }

    public String getIndividualId() {
        return this.individualId;
    }

    public int getOrderId() {
        return this.orderId;
    }

    public int getStatus() {
        return this.status;
    }

    public ArrayList<FoodItem> getContents() {
        return this.contents;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setContent(ArrayList<FoodItem> contents) {
        this.contents = contents;
    }

    /**
     * Method that edits the contents of the foodbox
     *
     * @param contentID represents the id number
     * @param quantity represents the quantity
     * @return True if contents have been edited otherwise false
     */
    public boolean editContents(int contentID, int quantity) {
        for (FoodItem item: contents) {
            if(contentID == item.getId()) {
                if (quantity < item.getQuantity()) {
                    item.setQuantity(quantity);
                }
                else {
                    System.out.println("You can only reduce the quantity of an item.");
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return orderId == order.orderId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }
}
