/**
 *
 */

package shield;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.Properties;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 */

public class ShieldingIndividualClientImpTest {
  private final static String clientPropsFilename = "client.cfg";

  private Properties clientProps;
  private ShieldingIndividualClientImp client;

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

    client = new ShieldingIndividualClientImp(clientProps.getProperty("endpoint"));
  }

  @Test
  public void testShieldingIndividualNewRegistration() {
    Random rand = new Random();
    String chi = String.valueOf(rand.nextInt(10000));

    assertTrue(client.registerShieldingIndividual(chi));
    assertTrue(client.isRegistered());
    assertEquals(client.getCHI(), chi);
  }

  @Test
  public void testShowFoodBoxes() {
    assertEquals(client.showFoodBoxes("none").size(), 3);
  }

  @Test
  public void testPlaceOrder() {
    Random rand = new Random();
    String chi = String.valueOf(rand.nextInt(10000));
    assertTrue(client.registerShieldingIndividual(chi));
    assertTrue(client.isRegistered());
    assertEquals(client.getCHI(), chi);
    client.pickFoodBox(1);
    assertTrue(client.placeOrder());
  }

  @Test
  public void testEditOrder(){
    Random rand = new Random();
    String chi = String.valueOf(rand.nextInt(10000));

    assertTrue(client.registerShieldingIndividual(chi));
    assertTrue(client.isRegistered());
    assertEquals(client.getCHI(), chi);
    assertEquals(client.showFoodBoxes("vegan").size(), 1 );
    assertEquals(client.showFoodBoxes("pollotarian").size(), 1);
    assertTrue(client.placeOrder());
    assertTrue(client.editOrder(Integer.parseInt(client.getOrderNumber())));
  }

  @Test
  public void testCancelOrder(){
    Random rand = new Random();
    String chi = String.valueOf(rand.nextInt(10000));

    assertTrue(client.registerShieldingIndividual(chi));
    assertTrue(client.isRegistered());
    assertEquals(client.getCHI(), chi);
    assertEquals(client.showFoodBoxes("vegan").size(), 1);
    assertEquals(client.showFoodBoxes("pollotarian").size(), 1);
    assertTrue(client.editOrder(Integer.parseInt(client.getOrderNumber())));
    assertTrue(client.cancelOrder(Integer.parseInt(client.getOrderNumber())));
  }

  @Test
  public void testRequestOrderStatus(){
    Random rand = new Random();
    String chi = String.valueOf(rand.nextInt(10000));

    assertTrue(client.registerShieldingIndividual(chi));
    assertTrue(client.isRegistered());
    assertEquals(client.getCHI(), chi);
    assertEquals(client.showFoodBoxes("vegan").size(), 1 );
    assertEquals(client.showFoodBoxes("pollotarian").size(), 1);
    assertTrue(client.placeOrder());
    assertTrue(client.requestOrderStatus(Integer.parseInt(client.getOrderNumber())));
  }

  @Test
  public void testGetCateringCompanies(){

  }
  @Test
  public void testGetDistance(){

  }
  @Test
  public void testIsRegistered(){
    Random rand = new Random();
    String chi = String.valueOf(rand.nextInt(10000));
    assertTrue(client.registerShieldingIndividual(chi));
    assertTrue(client.isRegistered());
  }

  @Test
  public void testGetCHI(){
    Random rand = new Random();
    String chi = String.valueOf(rand.nextInt(10000));

    assertEquals(client.getCHI(), chi);
  }

  @Test
  public void testGetFoodBoxNumber(){
  }

  @Test
  public void testGetDietaryPreferenceForFoodBox(){

    Random rand = new Random();
    String chi = String.valueOf(rand.nextInt(10000));
    client.registerShieldingIndividual(chi);
    client.placeOrder();


    assertEquals(client.getDietaryPreferenceForFoodBox(3),"none");

    assertEquals(client.getDietaryPreferenceForFoodBox(456), null);

    assertEquals(client.getDietaryPreferenceForFoodBox(-1), null);

  }

  @Test
  public void testGetItemsNumberForFoodBox(){

    Random rand = new Random();
    String chi = String.valueOf(rand.nextInt(10000));
    client.registerShieldingIndividual(chi);
    client.placeOrder();

    assertEquals(client.getItemsNumberForFoodBox(4), 4);
  }

  @Test
  public void testGetItemIdsForFoodBox(){
    assertEquals(client.getItemIdsForFoodBox(312).size(),0);

  }
  @Test
  public void testGetItemNameForFoodBox(){

    Random rand = new Random();
    String chi = String.valueOf(rand.nextInt(10000));
    client.registerShieldingIndividual(chi);
    client.placeOrder();

    assertEquals(client.getItemNameForFoodBox(3, 3), "onions");
    assertEquals(client.getItemNameForFoodBox(4, 3), "carrots");
    assertEquals(client.getItemNameForFoodBox(13, 4), "cabbage");
    assertEquals(client.getItemNameForFoodBox(987, 7941), null);
  }

  @Test
  public void testGetItemQuantityForFoodBox(){

    Random rand = new Random();
    String chi = String.valueOf(rand.nextInt(10000));
    client.registerShieldingIndividual(chi);
    client.placeOrder();

    assertEquals(client.getItemQuantityForFoodBox(4,4),1);
    assertEquals(client.getItemQuantityForFoodBox(-999,9999),0);
    assertEquals(client.getItemQuantityForFoodBox(999,9999),0);
  }

  @Test
  public void testPickFoodBox(){

    assertTrue(client.pickFoodBox(3));
    assertFalse(client.pickFoodBox(-123456));
    assertFalse(client.pickFoodBox(99999));
  }

  @Test
  public void testChangeItemQuantityForPickedFoodBox(){

    Random rand = new Random();
    String chi = String.valueOf(rand.nextInt(10000));
    client.registerShieldingIndividual(chi);
    client.placeOrder();

    assertFalse(client.changeItemQuantityForPickedFoodBox(123456,0));
    assertFalse(client.changeItemQuantityForPickedFoodBox(987654,1111111));

  }

  @Test
  public void testGetOrderNumbers(){
  }

  @Test
  public void testGetStatusForOrder(){

    Random rand = new Random();
    String chi = String.valueOf(rand.nextInt(10000));
    client.registerShieldingIndividual(chi);
    client.placeOrder();

    assertEquals(client.getStatusForOrder(Integer.parseInt(client.getOrderNumber())),"0");
  }

  @Test
  public void testGetItemIdsForOrder(){

    Random rand = new Random();
    String chi = String.valueOf(rand.nextInt(10000));
    client.registerShieldingIndividual(chi);
    client.placeOrder();

    assertEquals(client.getItemIdsForOrder(999999),null);
  }

  @Test
  public void testGetItemNameForOrder(){

    Random rand = new Random();
    String chi = String.valueOf(rand.nextInt(10000));
    client.registerShieldingIndividual(chi);
    client.placeOrder();

    Object[] identifications = (client.getItemIdsForOrder(Integer.parseInt(client.getOrderNumber()))).toArray();
    assertEquals(client.getItemNameForOrder(9876,999), null);
    assertEquals(client.getItemNameForOrder(Integer.parseInt(identifications[0].toString()), Integer.parseInt(client.getOrderNumber())),"cucumbers");
  }

  @Test
  public void testGetItemQuantityForOrder(){

    Random rand = new Random();
    String chi = String.valueOf(rand.nextInt(10000000));
    client.registerShieldingIndividual(chi);
    client.placeOrder();

    assertEquals(client.getItemQuantityForOrder(9876,999), 0);

  }

  @Test
  public void testSetItemQuantityForOrder() {
    Random rand = new Random();
    String chi = String.valueOf(rand.nextInt(10000));
    client.registerShieldingIndividual(chi);
    client.placeOrder();

    assertFalse(client.setItemQuantityForOrder(999, 98765, 100));
  }

  @Test
  public void testGetClosestCateringCompany(){
    Random rand = new Random();
    String chi = String.valueOf(rand.nextInt(10000));

    assertTrue(client.registerShieldingIndividual(chi));
    String closestCC = client.getClosestCateringCompany();
    assertEquals(closestCC, client.getClosestCateringCompany());

  }

}
