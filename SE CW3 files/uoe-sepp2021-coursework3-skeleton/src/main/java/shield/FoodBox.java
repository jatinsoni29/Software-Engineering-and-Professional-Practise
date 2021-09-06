package shield;

import java.util.ArrayList;

public class FoodBox {

    private ArrayList<FoodItem> contents;
    private String deliveredBy;
    private String diet;
    private int id;
    private String name;

    /**
     * Constructor for the FoodBox class
     *
     *  @param deliveredBy represents the date that it should be delivered by
     * @param diet represents the diet type i.e. vegetarian, vegan
     * @param id represents the id
     * @param name represents the name of the foodbox
     */
    public FoodBox(String deliveredBy, String diet, int id, String name) {
        this.deliveredBy = deliveredBy;
        this.diet = diet;
        this.id = id;
        this.name = name;
    }

    /**
     * Method that checks if the contents of the foodbox has been edited i.e. only reduction is possible
     *
     * @param contentID represents the contentID
     * @param quantity represents the quantity
     * @return True if successfully edited and false otherwise
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

    public ArrayList<FoodItem> getContents() {
        return contents;
    }

    public String getDiet() {
        return diet;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDeliverdBy() {
        return this.deliveredBy;
    }

    public void setDeliverdBy(String deliveredBy) {
        this.deliveredBy = deliveredBy;
    }

    @Override
    public String toString() {
        return "FoodBox{" +
                "contents=" + contents + '\n' +
                ", deliveredBy=" + deliveredBy + '\n' +
                ", diet=" + diet + '\n' +
                ", id=" + id + '\n' +
                ", name=" + name + '\n' +
                '}';
    }

}
