package shield;

public class FoodItem {
    private int id;
    private String name;
    private int quantity;

    /**
     * Constructor for the FoodItem class
     *
     *  @param id represents the id number
     * @param name represents the name of the food item
     * @param quantity represents the quantity of the food item
     */
    public FoodItem(int id, String name, int quantity) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "FoodItem{" +
                "id=" + id + '\n' +
                ", name=" + name + '\n' +
                ", quantity=" + quantity + '\n' +
                '}';
    }
}
