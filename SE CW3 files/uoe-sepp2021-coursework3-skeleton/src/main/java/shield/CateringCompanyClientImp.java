package shield;

import java.io.IOException;

public class CateringCompanyClientImp implements CateringCompanyClient {
  private final String endpoint;
  private String postcode;
  private String name;
  private boolean registered = false;

  public CateringCompanyClientImp(String endpoint) {
    this.endpoint = endpoint;
  }

  @Override
  public boolean registerCateringCompany(String name, String postCode) {
    String request = "/registerCateringCompany?business_name=" + name +"&postcode=" + postCode;
    this.name = name;
    this.postcode = postCode;
    try{
      String response = ClientIO.doGETRequest(endpoint + request);
      System.out.println(response);
      this.registered = true;
    }
    catch(IOException e) {
      e.printStackTrace();
    }
    return this.registered;
  }

  @Override
  public boolean updateOrderStatus(int orderNumber, String status) {
    String request = "/updateOrderStatus?order_id=" + orderNumber + "&newStatus=" + status;
    boolean isSuccessful = false;
    try{
      String response = ClientIO.doGETRequest(endpoint + request);
      System.out.println(response);
      isSuccessful = true;
    }
    catch(IOException e) {
      e.printStackTrace();
    }
    return isSuccessful;
  }

  @Override
  public boolean isRegistered() {
    return this.registered;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public String getPostCode() {
    return this.postcode;
  }
}
