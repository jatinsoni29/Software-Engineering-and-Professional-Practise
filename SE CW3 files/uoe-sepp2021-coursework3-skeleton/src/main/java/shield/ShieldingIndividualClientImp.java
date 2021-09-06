package shield;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

public class ShieldingIndividualClientImp implements ShieldingIndividualClient {

  private String endpoint;
  private boolean isRegistered;
  private String CHI;
  private String postcode;
  private String name;
  private String surname;
  private String phoneNumber;
  private ArrayList<FoodBox> availableFoodBoxes;
  private FoodBox markedForOrder;
  private List<Integer> orderNumbers = new ArrayList<>();
  private HashSet<CateringCompany> cateringCompanies = new HashSet<>();
  private ArrayList<Order> orders = new ArrayList<>();
  private String orderNumber = null;

  public String getOrderNumber() {
    return orderNumber;
  }

  public ShieldingIndividualClientImp(String endpoint) {
    this.endpoint = endpoint;
  }

  /**
   * Method that performs a GET HTTP request that register's an individual with the server if they are not already registered
   * and returns true if successful and false upon failure. function will throw an exception if request if of incorrect format
   *
   * @param CHI represents the shielding individuals unique identifier
   * @return boolean of True if registration success or false if input is invalid or already registered
   */
  @Override
  public boolean registerShieldingIndividual(String CHI) {
    String request = "/registerShieldingIndividual?CHI=" + CHI;
    boolean isSuccessful = false;
    try {
      var response = ClientIO.doGETRequest(this.endpoint + request);
      if (response.equals("already registered")) {
        System.out.println("Shielding individual registered already");
      } else {
        List<String> parse_list = stringArrToArray(response);
        this.postcode = formatPostcode(parse_list.get(0));
        this.name = parse_list.get(1);
        this.surname = parse_list.get(2);
        this.phoneNumber = parse_list.get(3);
      }
      isSuccessful = true;
      this.isRegistered = true;
      this.CHI = CHI;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return isSuccessful;
  }

  /**
   * Method to obtain food boxes available and show what they contain.
   * It updates all the boxes that have been shown over the entire instance of the users order
   *
   * @param dietaryPreference represents the data from the shielding individual of which boxes based on diet they wish to see
   * @return collection of boxes individual ID values
   */
  @Override
  public Collection<String> showFoodBoxes(String dietaryPreference) {
    // construct the endpoint request
    String request = "/showFoodBox?orderOption=catering&dietaryPreference=" + dietaryPreference;

    // setup the response recepient
    List<FoodBox> responseBoxes = new ArrayList<FoodBox>();
    try {
      // perform request
      String response = ClientIO.doGETRequest(endpoint + request);
      // unmarshal response
      Type listType = new TypeToken<List<FoodBox>>() {
      }.getType();
      responseBoxes = new Gson().fromJson(response, listType);
    } catch (Exception e) {
      e.printStackTrace();
    }
    Collection<String> responses = new ArrayList<>();
    for (FoodBox box: responseBoxes) {
      responses.add(box.toString());
    }
    return responses;
  }

  /**
   * Method that performs a post request of users order based on inputs provided by locating the nearest caterer
   *
   * @return True if successful and false otherwise
   */
  // **UPDATE2** REMOVED PARAMETER
  @Override
  public boolean placeOrder() {
    this.getCateringCompanies();
    // find nearest catering company
    String businessName = getClosestCateringCompany();
    String postcode = null;
    for (CateringCompany company : this.cateringCompanies) {
      if (businessName.equals(company.getName())) {
        postcode = company.getPostcode();
        break;
      }
    }
    ArrayList<FoodItem> contents = markedForOrder.getContents();
    String jsonBody = new Gson().toJson(contents);
    jsonBody = "{\"contents\":" + jsonBody + "}";
    System.out.println(jsonBody);
    String request = "/placeOrder?individual_id=" + this.CHI + "&catering_business_name=" + businessName + "&catering_postcode=" + postcode;
    boolean isSuccessful = false;
    try {
      var response = ClientIO.doPOSTRequest(this.endpoint + request, jsonBody);
      isSuccessful = true;
      int orderID = Integer.parseInt(response);
      Order order = new Order(this.CHI, orderID, 0, contents);
      this.orders.add(order);
      this.orderNumbers.add(orderID);
      System.out.println(orderID);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return isSuccessful;
  }

  /**
   * Method performs a HTTP POST request with the edited food box
   *
   * @param orderNumber represents the shielding individual's unique identifier to allow the correct order to be edited
   * @returns True if the order was successfully changed and False otherwise
   */
  @Override
  public boolean editOrder(int orderNumber) {
    assert orderNumbers.contains(orderNumber);
    ArrayList<FoodItem> contents = null;
    for (Order o: orders) {
      if (orderNumber == o.getOrderId()) {
        contents = o.getContents();
      }
    }
    String request = "/editOrder?order_id=" + orderNumber;
    String jsonBody = new Gson().toJson(contents);
    jsonBody = "{\"contents\":" + jsonBody + "}";
    System.out.println(jsonBody);
    try {
      String response = ClientIO.doPOSTRequest(this.endpoint + request, jsonBody);
      System.out.println(response);
      return true;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return false;
  }

  /**
   * Method performs a HTTP GET request using the shielding individuals unique order number to cancel an order on the server
   * if the order has not already been dispatched or delivered or cancelled
   *
   * @param orderNumber represents the shielding individuals unique order identifier for there instance order
   * @return True if order cancellation was successful and False if the status did not allow for cancellation
   */
  @Override
  public boolean cancelOrder(int orderNumber) {
    assert orderNumbers.contains(orderNumber);
    orderNumbers.remove(orderNumbers.indexOf(orderNumber));
    ArrayList<Order> cloneOrders = new ArrayList<>(orders);
    for (Order o: cloneOrders) {
      if (orderNumber == o.getOrderId()) {
        this.orders.remove(o);
        break;
      }
    }
    String request = "/cancelOrder?order_id=" + orderNumber;
    try {
      var response = ClientIO.doGETRequest(this.endpoint + request);
      return true;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return false;
  }

  /**
   * Method returns the server request on unique order states and then assigns instance variable based on the return
   *
   * @param orderNumber represents the shielding individual's unique order identification
   * @returns True if the return is of the correct format and order status can be updated and False if the return is not
   */
  @Override
  public boolean requestOrderStatus(int orderNumber) {
    assert orderNumbers.contains(orderNumber);
    String request = "/requestStatus?order_id=42" + orderNumber;
    try {
      var response = ClientIO.doGETRequest(this.endpoint + request);
      System.out.println(response);
      return true;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return false;
  }

  /**
   * Method performs a HTTP GET request to return all of the currently registered catering companies as a collection
   *
   * @return String collection of all the currently available catering companies
   */
  // **UPDATE**
  @Override
  public Collection<String> getCateringCompanies() {
    String request = "/getCaterers";
    try {
      var response = ClientIO.doGETRequest(this.endpoint + request);
      List<String> cateringCompaniesList = stringArrToArray(response);
      cateringCompaniesList = cateringCompaniesList.subList(2, cateringCompaniesList.size());
      System.out.println(cateringCompaniesList);
      int i = 0;
      while (i < cateringCompaniesList.size() - 1) {
        String companyName = cateringCompaniesList.get(i);
        String postcode = formatPostcode(cateringCompaniesList.get(i + 1));
        if(companyName.length() == 1) {
          i++;
          continue;
        }
        CateringCompany company = new CateringCompany(companyName, postcode);
        this.cateringCompanies.add(company);
        i = i+2;
      }
      System.out.println(this.cateringCompanies);
      Collection<String> companies = new ArrayList<>();
      for (CateringCompany company: this.cateringCompanies) {
        companies.add(company.toString());
      }
      return companies;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Method performs a HTTP GET request to return the distance between 2 individual postcodes and return the response as a float
   *
   * @param postCode1 represents the individual postcode of one location
   * @param postCode2 represents the individual postcode of another location
   * @return a float value of the calculated distance upon server
   */
  // **UPDATE**
  @Override
  public float getDistance(String postCode1, String postCode2) {
    postCode1 = formatPostcode(postCode1);
    postCode2 = formatPostcode(postCode2);
    // assert postCode1.contains("EH") && postCode2.contains("EH");
    String request = "/distance?postcode1=" + postCode1 + "&postcode2=" + postCode2;
    try {
      String response = ClientIO.doGETRequest(this.endpoint + request);
      float parsedResponse = Float.parseFloat(response);
      return parsedResponse;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return -1;
  }

  @Override
  public boolean isRegistered() {
    return this.isRegistered;
  }

  @Override
  public String getCHI() {
    return this.CHI;
  }

  /**
   * Method that obtains the number of foodboxes that are available
   *
   * @return integer value of the number of available food boxes
   */
  @Override
  public int getFoodBoxNumber() {
    // construct the endpoint request
    String request = "/showFoodBox?orderOption=catering&dietaryPreference=";
    // setup the response recepient
    this.availableFoodBoxes = new ArrayList<FoodBox>();

    try {
      // perform request
      String response = ClientIO.doGETRequest(endpoint + request);
      // unmarshal response
      Type listType = new TypeToken<List<FoodBox>>() {
      }.getType();
      this.availableFoodBoxes = new Gson().fromJson(response, listType);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return this.availableFoodBoxes.size();
  }

  /**
   * Method iterates through all the boxes previously shown to the shielding individual
   * and returns the diet based on the box id passed
   *
   * @param foodBoxId represents unique identifier for each for box in system
   * @returns null if id does not match available boxes or returns the corresponding diet to food box requested
   */
  @Override
  public String getDietaryPreferenceForFoodBox(int foodBoxId) {
    getFoodBoxNumber();
    for (FoodBox box : availableFoodBoxes) {
      if (foodBoxId == box.getId()) {
        return box.getDiet();
      }
    }
    return "FoodBoxId not found.";
  }

  /**
   * Method iterates through all shown food boxes of users request and returns the number of items in box based on the box id
   *
   * @param foodBoxId represents the unique identifier for each for food box
   * @return the number of items in the food box that has been specified
   */
  @Override
  public int getItemsNumberForFoodBox(int foodBoxId) {
    getFoodBoxNumber();
    for (FoodBox box : availableFoodBoxes) {
      if (foodBoxId == box.getId()) {
        return box.getContents().size();
      }
    }
    return -1;
  }

  /**
   * Method iterates through all food boxes and returns all the unique IDs for food item based upon the food box identification
   *
   * @param foodBoxId represents the unique identifier for each for food box
   * @return collection of itemIds for the supplied food box id or null if id does not match with any food box
   */
  @Override
  public Collection<Integer> getItemIdsForFoodBox(int foodBoxId) {
    getFoodBoxNumber();
    for (FoodBox box : availableFoodBoxes) {
      if (foodBoxId == box.getId()) {
        ArrayList<Integer> itemIDs = new ArrayList<Integer>();
        for (FoodItem item : box.getContents()) {
          itemIDs.add(item.getId());
        }
        return itemIDs;
      }
    }
    return null;
  }

  /**
   * Method iterates through all food boxes and returns the item name upon the specific item id and food box id
   *
   * @param  itemId represents the unique identifier for item in the contents of food box
   * @param  foodBoxId represents the unique identifier for each food box
   * @return Name of the item requested or an appropriate message if it does not exist or cannot be found
   */
  @Override
  public String getItemNameForFoodBox(int itemId, int foodBoxId) {
    getFoodBoxNumber();
    for (FoodBox box : availableFoodBoxes) {
      if (foodBoxId == box.getId()) {
        for (FoodItem item : box.getContents()) {
          if (itemId == item.getId()) {
            return item.getName();
          }
        }
      }
    }
    return "Foodbox or item id not found";
  }

  /**
   * Method iterates through all food boxes and returns the quantity for item upon specific id for food box and item
   *
   * @param  itemId represents the unique identifier for item in the contents of the food box
   * @param  foodBoxId represents the unique identifier for each food box
   * @return quantity of the item that was requested or -1 if it does not exist
   */
  @Override
  public int getItemQuantityForFoodBox(int itemId, int foodBoxId) {
    getFoodBoxNumber();
    for (FoodBox box : availableFoodBoxes) {
      if (foodBoxId == box.getId()) {
        for (FoodItem item : box.getContents()) {
          if (itemId == item.getId()) {
            return item.getQuantity();
          }
        }
      }
    }
    return -1;
  }

  /**
   * Method to pick food box based on the current boxes
   *
   * @param foodBoxId represents the unique identifier for each food box
   * @return True if food box exists and False otherwise
   */
  @Override
  public boolean pickFoodBox(int foodBoxId) {
    getFoodBoxNumber();
    for (FoodBox box : availableFoodBoxes) {
      if (foodBoxId == box.getId()) {
        this.markedForOrder = box;
        return true;
      }
    }
    return false;
  }

  /**
   * method supports the edit order use case.
   * It uses the editContents method in the FoodBox class to change the quantity for the item
   *
   * @param itemId represents the unique identifier for item in the contents of food box
   * @param quantity represents the amount of items ordered by the unique id
   * @return True if quantity is decreased and unique id exists in order and False otherwise
   */
  @Override
  public boolean changeItemQuantityForPickedFoodBox(int itemId, int quantity) {
    return this.markedForOrder.editContents(itemId, quantity);
  }

  /**
   * method to return all the values of the currently placed orders in the system
   * @return collection of integers of all order Ids created
   */
  @Override
  public Collection<Integer> getOrderNumbers() {
    return this.orderNumbers;
  }

  /**
   * Method to GET HTTP request method to return the order status based on the order number
   *
   * @param orderNumber represents shielding individual's unique order identifier
   * @return string of order status corresponding to current point in delivery food box is in and null otherwise
   */
  @Override
  public String getStatusForOrder(int orderNumber) {
    assert this.orderNumbers.contains(orderNumber);
    String request = "/requestStatus?order_id=" + orderNumber;
    try {
      var response = ClientIO.doGETRequest(this.endpoint + request);
      return response;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Method that iterates through current placed orders to find order based on unique order id
   * and returns a collection of all the item ID's corresponding to that order
   *
   * @param orderNumber represents the shielding individual's unique order identifier
   * @return collection of all IDs as integers
   */
  @Override
  public Collection<Integer> getItemIdsForOrder(int orderNumber) {
    for (Order order : orders) {
      if (orderNumber == order.getOrderId()) {
        ArrayList<Integer> itemIDs = new ArrayList<Integer>();
        for (FoodItem item : order.getContents()) {
          itemIDs.add(item.getId());
        }
        return itemIDs;
      }
    }
    return null;
  }

  /**
   * Method that iterates over unique placed order to find corresponding item name based
   * on the unique item ID
   *
   * @param itemId represents the unique identifier for item in contents of food box
   * @param orderNumber represents the shielding individual's unique order identifier
   * @return item name corresponding to that unique item id and null if that can't be found
   */
  @Override
  public String getItemNameForOrder(int itemId, int orderNumber) {
    for (Order order : orders) {
      if (orderNumber == order.getOrderId()) {
        for (FoodItem item : order.getContents()) {
          if (itemId == item.getId()) {
            return item.getName();
          }
        }
      }
    }
    return null;
  }

  /**
   * Method iterates over current placed orders to get the quantities on the placed order based on
   * unique id and unique item id
   * @param itemId represents the unique identifier for item in contents of food box
   * @param orderNumber represents the shielding individual's unique order identifier
   * @return integer of quantity of unique item type requested and -1 otherwise
   */
  @Override
  public int getItemQuantityForOrder(int itemId, int orderNumber) {
    for (Order order : orders) {
      if (orderNumber == order.getOrderId()) {
        for (FoodItem item : order.getContents()) {
          if (itemId == item.getId()) {
            return item.getQuantity();
          }
        }
      }
    }
    return -1;
  }

  /**
   * method that sets the new quantity of an order upon editing order given a unique order number, item id and quantity
   *
   * @return boolean value depending on if the new quantity was set or not
   */
  @Override
  public boolean setItemQuantityForOrder(int itemId, int orderNumber, int quantity) {
    for (Order order : orders) {
      if (orderNumber == order.getOrderId()) {
        boolean success = order.editContents(itemId, quantity);
        return editOrder(orderNumber);
      }
    }
    return false;
  }

  // **UPDATE2** REMOVED METHOD getDeliveryTimeForOrder

  /**
   * method that obtains the closest catering company in currently registered caterers by calculating the smallest distance metric for all current catering companies
   * @return closest catering company found
   */
  // **UPDATE**
  @Override
  public String getClosestCateringCompany() {
    getCateringCompanies();
    float minDistance = -1;
    String closestCompany = "";

    for (CateringCompany company : this.cateringCompanies) {
      float dist = getDistance(this.postcode, company.getPostcode());
      if (dist < minDistance || minDistance < 0) {
        closestCompany = company.getName();
        minDistance = dist;
      }
    }
    return closestCompany;
  }

  /**
   * Method obtains a list of orders
   *
   * @return List of orders
   */
  public ArrayList<Order> getOrders() {
    return this.orders;
  }

  /**
   * Method that formats the postcode
   *
   * @param postcode represents the postcode
   * @return the formatted string
   */
  private String formatPostcode(String postcode) {
    // assert postcode.length() == 7 || postcode.length() == 6;
    String firstPart = "";
    String lastPart = "";
    if (postcode.length() == 8) {
      firstPart = postcode.substring(0, 4).toUpperCase().replace(" ", "").replace("_", "");
      lastPart = postcode.substring(4).toUpperCase().replace(" ", "").replace("_", "");
    } else {
      firstPart = postcode.substring(0, 3).toUpperCase().replace(" ", "").replace("_", "");
      lastPart = postcode.substring(3).toUpperCase().replace(" ", "").replace("_", "");;
    }
    return firstPart + "_" + lastPart;
  }


  private List<String> stringArrToArray(String stringArr) {
    stringArr = stringArr.replace("[", "");
    stringArr = stringArr.replace("]", "");
    stringArr = stringArr.replace("\"", "");
    String[] array = stringArr.split(",");
    return Arrays.asList(array);
  }

}
