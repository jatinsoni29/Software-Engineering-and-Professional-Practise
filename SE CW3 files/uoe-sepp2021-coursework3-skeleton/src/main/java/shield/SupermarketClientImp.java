/**
 *
 */

package shield;

import java.io.IOException;
import java.util.ArrayList;

public class SupermarketClientImp implements SupermarketClient {

  private String endpoint;
  private String supermarketName;
  private String postcode;
  private String orderNumber;
  private boolean isRegistered;

  public String getOrderNumber() {
    return orderNumber;
  }

  private ArrayList<String> ordered = new ArrayList<String>();

  public SupermarketClientImp(String endpoint){
    this.endpoint = endpoint;

  }

  /**
   * Method that performs a GET HTTP request that register's a supermarket with the server if they are not registered already
   *
   * @param name name of the business
   * @param postCode post code of the business
   * @return a boolean value depending on if the request is successful or not
   */
  @Override
  public boolean registerSupermarket(String name, String postCode) {
    String request = "/registerSupermarket?business_name=" + name + "&postcode=" + postCode;

    try {
      String response = ClientIO.doGETRequest(endpoint + request);
      System.out.println(response);
      this.isRegistered = true;
      supermarketName = name;
      this.postcode = postCode;

      return true;
    }
    catch (IOException e) {

      this.isRegistered = false;
      return false;
    }

  }

  /**
   * Method to record an order made to a supermarket by a request to the server
   *
   * @param CHI CHI number of the shielding individual associated with this order
   * @param orderNumber the order number
   * @return boolean value if the request returns a response successfully or not
   */
  // **UPDATE2** ADDED METHOD
  @Override
  public boolean recordSupermarketOrder(String CHI, int orderNumber) {
    String request = "/recordSupermarketOrder?individual_id=" + CHI + "&order_number=" + orderNumber + "&supermarket_business_name=" + this.supermarketName + "&supermarket_postcode=" + this.postcode;

    try {
      String response = ClientIO.doGETRequest(endpoint + request);
      if (response.equals("True")) {
        this.orderNumber = Integer.toString(orderNumber);
        this.ordered.add(response);
        return true;
      }

      else {
        return false;
      }
    }

    catch (Exception e) {
      return false;
    }
  }

  /**
   * Method to update the status of the order for the shielding individuals order
   *
   * @param orderNumber the order number
   * @param status status of the order for the requested number
   * @return True if request is successful otherwise false if unsuccessful
   */
  // **UPDATE**
  @Override
  public boolean updateOrderStatus(int orderNumber, String status) {
    String request = "/updateSupermarketOrderStatus?order_id=" + orderNumber + "&newStatus=" + status;

    try {
      String response = ClientIO.doGETRequest(endpoint + request);

      if (response.equals("True")) {
        System.out.println("Status of the order has been updated to: " + status);
        return true;
      }
      else {
        System.out.println("Status of the order could not be updated");
        return false;
      }
    }
    catch (IOException e) {
      e.printStackTrace();
      return false;
    }

  }

  @Override
  public boolean isRegistered() {
    return this.isRegistered;
  }

  @Override
  public String getName() {
    return this.supermarketName;
  }


  @Override
  public String getPostCode() {
    return this.postcode;
  }


}

