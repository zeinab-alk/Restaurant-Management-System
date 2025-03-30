package uk.ac.rhul.cs2810.model;

/**
 * DTO for processing payment requests.
 * 
 */
public class PayRequest {

  private int orderNumber;
  private String cardNumber;
  private String expiryDate;
  private String cardHolderName;
  private String cvv;

  public PayRequest(int orderNumber, String cardNumber, String expiryDate, String cardHolderName,
      String cvv) {
    this.orderNumber = orderNumber;
    this.cardNumber = cardNumber;
    this.expiryDate = expiryDate;
    this.cardHolderName = cardHolderName;
    this.cvv = cvv;
  }


  public int getOrderNumber() {
    return orderNumber;
  }

  public void setOrderNumber(int orderNumber) {
    this.orderNumber = orderNumber;
  }

  public String getCardNumber() {
    return cardNumber;
  }

  public void setCardNumber(String cardNumber) {
    this.cardNumber = cardNumber;
  }

  public String getExpiryDate() {
    return expiryDate;
  }

  public void setExpiryDate(String expiryDate) {
    this.expiryDate = expiryDate;
  }

  public String getCardHolderName() {
    return cardHolderName;
  }

  public void setCardHolderName(String cardHolderName) {
    this.cardHolderName = cardHolderName;
  }

  public String getCvv() {
    return cvv;
  }

  public void setCvv(String cvv) {
    this.cvv = cvv;
  }
}
