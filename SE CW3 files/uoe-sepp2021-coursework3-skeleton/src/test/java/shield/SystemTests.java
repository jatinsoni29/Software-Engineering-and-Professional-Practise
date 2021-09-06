package shield;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class SystemTests {
    private final static String clientPropsFilename = "client.cfg";

    private Properties clientProps;
    private CateringCompanyClientImp cateringCompanyClient;
    private ShieldingIndividualClientImp shieldingIndividualClient;
    private SupermarketClientImp supermarketClient;

    private Properties loadProperties(String propsFilename) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Properties props = new Properties();

        try {
            InputStream propsStream = loader.getResourceAsStream(propsFilename);
            props.load(propsStream);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return props;
    }
    @BeforeEach
    public void setup() {
        clientProps = loadProperties(clientPropsFilename);
        cateringCompanyClient = new CateringCompanyClientImp(clientProps.getProperty("endpoint"));
        shieldingIndividualClient = new ShieldingIndividualClientImp(clientProps.getProperty("endpoint"));
        supermarketClient = new SupermarketClientImp(clientProps.getProperty("endpoint"));
    }
    @Test
    public void testRegisterSupermarket() {
        Random rand = new Random();
        String name = String.valueOf(rand.nextInt(10000));
        String postCode = "EH" + String.valueOf(rand.nextInt(17)) + "_" + String.valueOf(rand.nextInt(9))+ (char)(rand.nextInt(26) + 'A') + (char)(rand.nextInt(26) + 'A');

        assertTrue(supermarketClient.registerSupermarket(name, postCode));
        assertTrue(supermarketClient.isRegistered());
        assertEquals(supermarketClient.getName(), name);
    }
    @Test
    public void testRegisterCateringCompany() {
        Random rand = new Random();
        String name = String.valueOf(rand.nextInt(10000));
        String postCode = "EH" + String.valueOf(rand.nextInt(17)) + "_" + String.valueOf(rand.nextInt(9))+ (char)(rand.nextInt(26) + 'A') + (char)(rand.nextInt(26) + 'A');

        assertTrue(cateringCompanyClient.registerCateringCompany(name, postCode));
        assertTrue(cateringCompanyClient.isRegistered());
        assertEquals(cateringCompanyClient.getName(), name);
    }
    @Test
    public void testRegisterShieldingIndividual() {
        Random rand = new Random();
        String chi = String.valueOf(rand.nextInt(10000));

        assertTrue(shieldingIndividualClient.registerShieldingIndividual(chi));
        assertTrue(shieldingIndividualClient.isRegistered());
        assertEquals(shieldingIndividualClient.getCHI(), chi);
    }
    @Test
    public void testRegisterAndPlaceOrder() {
        Random rand = new Random();
        String chi = String.valueOf(rand.nextInt(10000));
        assertTrue(shieldingIndividualClient.registerShieldingIndividual(chi));
        assertTrue(shieldingIndividualClient.isRegistered());
        assertEquals(shieldingIndividualClient.getCHI(), chi);
        int foodboxID = 1;
        assertTrue(shieldingIndividualClient.pickFoodBox(foodboxID));
        assertTrue(shieldingIndividualClient.placeOrder());
        ArrayList<Order> orders = shieldingIndividualClient.getOrders();
        Order lastOrder = orders.get(orders.size() - 1);
        assertEquals(lastOrder.getIndividualId(), chi);
    }
    @Test
    public void testRegisterAndPlaceEditedFoodBoxOrder() {
        Random rand = new Random();
        String chi = String.valueOf(rand.nextInt(10000));
        assertTrue(shieldingIndividualClient.registerShieldingIndividual(chi));
        assertTrue(shieldingIndividualClient.isRegistered());
        assertEquals(shieldingIndividualClient.getCHI(), chi);
        int foodboxID = 1;
        assertTrue(shieldingIndividualClient.pickFoodBox(foodboxID));
        int itemID = 2;
        int newQuantity = 1;
        shieldingIndividualClient.changeItemQuantityForPickedFoodBox(itemID, newQuantity);
        assertTrue(shieldingIndividualClient.placeOrder());
        ArrayList<Order> orders = shieldingIndividualClient.getOrders();
        Order lastOrder = orders.get(orders.size() - 1);
        assertEquals(lastOrder.getIndividualId(), chi);
        for (FoodItem item: lastOrder.getContents()) {
            if(item.getId() == itemID) {
                assertEquals(item.getQuantity(), newQuantity);
                break;
            }
        }
    }
    @Test
    public void testEditOrder() {
        Random rand = new Random();
        String chi = String.valueOf(rand.nextInt(10000));
        assertTrue(shieldingIndividualClient.registerShieldingIndividual(chi));
        assertTrue(shieldingIndividualClient.isRegistered());
        assertEquals(shieldingIndividualClient.getCHI(), chi);
        int foodboxID = 1;
        assertTrue(shieldingIndividualClient.pickFoodBox(foodboxID));
        assertTrue(shieldingIndividualClient.placeOrder());
        ArrayList<Order> orders = shieldingIndividualClient.getOrders();
        Order lastOrder = orders.get(orders.size() - 1);
        assertEquals(lastOrder.getIndividualId(), chi);
        int orderID = lastOrder.getOrderId();
        Collection<Integer> itemIDs = shieldingIndividualClient.getItemIdsForOrder(orderID);
        for (int itemID : itemIDs) {
            int currQuantity = shieldingIndividualClient.getItemQuantityForOrder(itemID, orderID);
            assertNotEquals(currQuantity, -1);
            if (currQuantity > 0) {
                assertTrue(shieldingIndividualClient.setItemQuantityForOrder(itemID, orderID, currQuantity - 1));
            }
        }
    }
    @Test
    public void testCancelFoodBoxOrder(){
        Random rand = new Random();
        String chi = String.valueOf(rand.nextInt(10000));
        assertTrue(shieldingIndividualClient.registerShieldingIndividual(chi));
        assertTrue(shieldingIndividualClient.isRegistered());
        assertEquals(shieldingIndividualClient.getCHI(), chi);
        int foodboxID = 1;
        assertTrue(shieldingIndividualClient.pickFoodBox(foodboxID));
        assertTrue(shieldingIndividualClient.placeOrder());
        ArrayList<Order> orders = shieldingIndividualClient.getOrders();
        Order lastOrder = orders.get(orders.size() - 1);
        assertEquals(lastOrder.getIndividualId(), chi);
        int orderID = lastOrder.getOrderId();
        assertTrue(shieldingIndividualClient.cancelOrder(orderID));
    }
    @Test
    public void testRequestOrderStatus() {
        Random rand = new Random();
        String chi = String.valueOf(rand.nextInt(10000));
        assertTrue(shieldingIndividualClient.registerShieldingIndividual(chi));
        assertTrue(shieldingIndividualClient.isRegistered());
        assertEquals(shieldingIndividualClient.getCHI(), chi);
        int foodboxID = 1;
        assertTrue(shieldingIndividualClient.pickFoodBox(foodboxID));
        assertTrue(shieldingIndividualClient.placeOrder());
        ArrayList<Order> orders = shieldingIndividualClient.getOrders();
        Order lastOrder = orders.get(orders.size() - 1);
        assertEquals(lastOrder.getIndividualId(), chi);
        int orderID = lastOrder.getOrderId();
        assertNotNull(shieldingIndividualClient.getStatusForOrder(orderID));
        int orderStatus = Integer.parseInt(shieldingIndividualClient.getStatusForOrder(orderID));
        assertTrue(orderStatus >= 0 && orderStatus <= 4);
    }
    @Test
    public void testUpdateOrderStatusAndRequestStatus() {
        Random rand = new Random();
        String chi = String.valueOf(rand.nextInt(10000));
        assertTrue(shieldingIndividualClient.registerShieldingIndividual(chi));
        assertTrue(shieldingIndividualClient.isRegistered());
        assertEquals(shieldingIndividualClient.getCHI(), chi);
        int foodboxID = 1;
        assertTrue(shieldingIndividualClient.pickFoodBox(foodboxID));
        assertTrue(shieldingIndividualClient.placeOrder());
        ArrayList<Order> orders = shieldingIndividualClient.getOrders();
        Order lastOrder = orders.get(orders.size() - 1);
        assertEquals(lastOrder.getIndividualId(), chi);
        int orderID = lastOrder.getOrderId();
        String newOrderStatus = "delivered";
        int expectedOrderStatus = 3;
        assertTrue(cateringCompanyClient.updateOrderStatus(orderID, newOrderStatus));
        assertNotNull(shieldingIndividualClient.getStatusForOrder(orderID));
        int orderStatus = Integer.parseInt(shieldingIndividualClient.getStatusForOrder(orderID));
        assertEquals(expectedOrderStatus, orderStatus);
    }


}
