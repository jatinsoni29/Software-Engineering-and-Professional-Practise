/**
 *
 */

package shield;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.Properties;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 */

public class CateringCompanyClientImpTest {
  private final static String clientPropsFilename = "client.cfg";

  private Properties clientProps;
  private CateringCompanyClient client;

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

    client = new CateringCompanyClientImp(clientProps.getProperty("endpoint"));
  }


  @Test
  public void testCateringCompanyNewRegistration() {
    Random rand = new Random();
    String name = String.valueOf(rand.nextInt(10000));
    String postCode = String.valueOf(rand.nextInt(10000));

    assertTrue(client.registerCateringCompany(name, postCode));
    assertTrue(client.isRegistered());
    assertEquals(client.getName(), name);
  }

  @Test
  public void testUpdateOrderStatus(){
    Random rand = new Random();
    int orderNumber = 124351;
    String status = "dispatched";

    assertTrue(client.updateOrderStatus(orderNumber, status));

  }
}
