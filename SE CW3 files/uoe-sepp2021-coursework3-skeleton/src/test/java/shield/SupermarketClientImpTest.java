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

public class SupermarketClientImpTest {
  private final static String clientPropsFilename = "client.cfg";

  private Properties clientProps;
  private SupermarketClient client;

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

    client = new SupermarketClientImp(clientProps.getProperty("endpoint"));
  }

  @Test
  public void testSupermarketNewRegistration() {
    Random rand = new Random();
    String name = String.valueOf(rand.nextInt(10000));
    String postCode = String.valueOf(rand.nextInt(10000));

    assertTrue(client.registerSupermarket(name, postCode));
    assertTrue(client.isRegistered());
    assertEquals(client.getName(), name);
  }

  @Test
  public void testSupermarketOrder(){
    Random rand = new Random();
    String chi = String.valueOf(rand.nextInt(10000));
    int orderNo = rand.nextInt(10000);
    ShieldingIndividualClientImp test_shield = new ShieldingIndividualClientImp(clientProps.getProperty("endpoint"));
    test_shield.registerShieldingIndividual(chi);
    assertTrue(client.registerSupermarket(client.getName(), client.getPostCode()));
    assertTrue(client.isRegistered());
    assertTrue(client.recordSupermarketOrder(chi,orderNo));
    assertFalse(client.recordSupermarketOrder("9999999999",99999999));
  }


  @Test
  public void testUpdateOrder(){
    Random rand = new Random();
    String chi = String.valueOf(rand.nextInt(10000));
    int orderNo = rand.nextInt(10000);
    ShieldingIndividualClientImp test_shield = new ShieldingIndividualClientImp(clientProps.getProperty("endpoint"));
    test_shield.registerShieldingIndividual(chi);
    assertTrue(client.registerSupermarket(client.getName(), client.getPostCode()));
    assertTrue(client.isRegistered());
    assertTrue(client.recordSupermarketOrder(chi,orderNo));
    client.updateOrderStatus(orderNo, "dispatched");
    assertFalse(client.updateOrderStatus(orderNo,"orderPacked"));
    assertTrue(client.updateOrderStatus(orderNo,"delivered"));
    assertFalse(client.updateOrderStatus(orderNo,"abcd1234"));

  }
}

