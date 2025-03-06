package uk.ac.rhul.cs2810.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * MenuItem test class.
 */
public class MenuItemTest {

  // Declare a MenuItem field for testing
  MenuItem item;

  @BeforeEach
  void setUp() {
    // Initialise the MenuItem before each test
    item = new MenuItem();
  }

  @Test
  void testMenuItemGettersAndSetters() {
    // Set values using setters
    item.setItemId(101);
    item.setName("Taco Deluxe");
    item.setPrice(9.99);
    item.setCaloricInfo(750);

    List<String> testDietary = Arrays.asList("Vegetarian", "Gluten-Free");
    item.setDietaryRestrictions(testDietary);

    List<String> testAllergens = Arrays.asList("Nuts", "Soy");
    item.setAllergenInformation(testAllergens);

    item.setPopularity(5);

    // Verify values using getters
    assertEquals(101, item.getItemId());
    assertEquals("Taco Deluxe", item.getName());
    assertEquals(9.99, item.getPrice());
    assertEquals(750, item.getCaloricInfo());
    assertEquals(testDietary, item.getDietaryRestrictions());
    assertEquals(testAllergens, item.getAllergenInformation());
    assertEquals(5, item.getPopularity());
  }

  @Test
  void testSetAvailable() {
    // Set availability to true
    item.setAvailable(true);
    // Verify that the availability is true
    assertTrue(item.isAvailable(), "The availability should be true.");
  }

  @Test
  void testIsAvailable() {
    // Set availability to false
    item.setAvailable(false);
    // Verify that the availability is false
    assertFalse(item.isAvailable(), "The availability should be false.");
  }

  @Test
  void testSetImageUrl() {
    // Set a value for imageUrl
    String expectedImageUrl = "https://example.com/images/taco_deluxe.jpg";
    item.setImageUrl(expectedImageUrl);

    // Verify that the imageUrl has been correctly set
    assertEquals(expectedImageUrl, item.getImageUrl(), "The imageUrl should be correctly set.");
  }

  @Test
  void testGetImageUrl() {
    // Set a value for imageUrl
    String expectedImageUrl = "https://example.com/images/taco_deluxe.jpg";
    item.setImageUrl(expectedImageUrl);

    // Verify that getImageUrl() returns the correct value
    assertEquals(expectedImageUrl, item.getImageUrl(),
        "The getImageUrl method should return the correct image URL.");
  }

  @Test
  void testSetNullImageUrl() {
    // Set imageUrl to null
    item.setImageUrl(null);

    // Verify that the imageUrl is null
    assertEquals(null, item.getImageUrl(), "The imageUrl should be null.");
  }

  @Test
  void testSetEmptyImageUrl() {
    // Set imageUrl to an empty string
    item.setImageUrl("");

    // Verify that the imageUrl is an empty string
    assertEquals("", item.getImageUrl(), "The imageUrl should be an empty string.");
  }

  // New Test: Invalid image URL (Malformed URL)
  @Test
  void testSetInvalidImageUrl() {
    // Set an invalid URL (e.g., missing scheme)
    String invalidImageUrl = "invalid-url";

    // Verify that an IllegalArgumentException is thrown
    assertThrows(IllegalArgumentException.class, () -> item.setImageUrl(invalidImageUrl),
        "An IllegalArgumentException should be thrown for an invalid image URL.");
  }

  // New Test: Invalid image URL (malformed URL)
  @Test
  void testSetMalformedImageUrl() {
    // Set a malformed URL (e.g., missing domain)
    String malformedImageUrl = "http://";

    // Verify that an IllegalArgumentException is thrown
    assertThrows(IllegalArgumentException.class, () -> item.setImageUrl(malformedImageUrl),
        "An IllegalArgumentException should be thrown for a malformed image URL.");
  }
}
