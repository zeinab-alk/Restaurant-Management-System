package uk.ac.rhul.cs2810.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for the 'MenuItem' class.
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
}
