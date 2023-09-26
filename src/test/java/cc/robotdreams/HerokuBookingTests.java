package cc.robotdreams;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.List;

public class HerokuBookingTests extends BaseTest{

    @Test(priority = 1)
    void createBooking() {
        // Create body using POJOs
        Bookingdates bookingdates = new Bookingdates("2020-03-25",
                "2020-03-27");
        Booking booking = new Booking("Nick",
                "Pruchkovskiy",
                150,
                false,
                bookingdates,
                "Baby crib");

        // Get response
        Response response = RestAssured.given(spec).contentType(ContentType.JSON).body(booking)
                .post("/booking");
        BookingId bookingId = response.as(BookingId.class);

        // Verifications
        // Verify response 200
        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200, but it's not");

        // Verify All fields
        Assert.assertEquals(bookingId.getBooking().toString(), booking.toString());
    }

    @Test(priority = 2)
    void getAllBookingIds() {
        // Get response with booking IDs
        Response response = RestAssured.given(spec).get("/booking");
        response.prettyPrint();

        // Verify response 200
        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200, but it is not");

        // Verify at least 1 booking id in the response
        List<Integer> bookingIds = response.jsonPath().getList("bookingid");
        Assert.assertFalse(bookingIds.isEmpty(), "List of booking IDs is empty but it shouldn't be");
    }

    @Test(priority = 3)
    void patchBookingPrice() {
        // Create booking
        Response responseCreate = createBookingManual();
        responseCreate.prettyPrint();

        // Get booking ID
        int bookingid = responseCreate.jsonPath().getInt("bookingid");

        // Create JSON body
        JSONObject body = new JSONObject();
        body.put("firstname", "Andrew");

        JSONObject bookingdates = new JSONObject();
        bookingdates.put("checkin", "2023-03-25");
        bookingdates.put("checkout", "2023-03-27");
        body.put("bookingdates", bookingdates);


        // Update response
        Response responseUpdate = RestAssured.given(spec).auth().preemptive().basic("admin",
                        "password123").contentType(ContentType.JSON).body(body.toString())
                .patch("/booking/" + bookingid);
        responseUpdate.prettyPrint();

        // Verifications
        // Verify response 200
        Assert.assertEquals(responseUpdate.getStatusCode(), 200, "Status code should be 200, but it's not");

        // Verify All fields
        SoftAssert softAssert = new SoftAssert();
        String actualFirstName = responseUpdate.jsonPath().getString("firstname");
        softAssert.assertEquals(actualFirstName, "Andrew", "firstname in response is not expected");

        String actualLastName = responseUpdate.jsonPath().getString("lastname");
        softAssert.assertEquals(actualLastName, "Pruchkovskiy", "lastname in response is not expected");

        int price = responseUpdate.jsonPath().getInt("totalprice");
        softAssert.assertEquals(price, 150, "totalprice in response is not expected");

        boolean depositpaid = responseUpdate.jsonPath().getBoolean("depositpaid");
        softAssert.assertFalse(depositpaid, "depositpaid should be true, but it's not");

        String actualCheckin = responseUpdate.jsonPath().getString("bookingdates.checkin");
        softAssert.assertEquals(actualCheckin, "2023-03-25", "checkin in response is not expected");

        String actualCheckout = responseUpdate.jsonPath().getString("bookingdates.checkout");
        softAssert.assertEquals(actualCheckout, "2023-03-27", "checkout in response is not expected");

        String actualAdditionalneeds = responseUpdate.jsonPath().getString("additionalneeds");
        softAssert.assertEquals(actualAdditionalneeds, "Baby crib", "additionalneeds in response is not expected");

        softAssert.assertAll();
    }



    @Test(priority = 4)
    void putBookingAdditionalNeeds() {
        Response responseCreate = createBookingManual();
        responseCreate.prettyPrint();

        // Get booking ID
        int bookingid = responseCreate.jsonPath().getInt("bookingid");

        // Create JSON body
        JSONObject body = new JSONObject();
        body.put("firstname", "Mila");
        body.put("lastname", "Pruchkovska");
        body.put("totalprice", 125);
        body.put("depositpaid", true);

        JSONObject bookingdates = new JSONObject();
        bookingdates.put("checkin", "2020-03-25");
        bookingdates.put("checkout", "2020-03-27");
        body.put("bookingdates", bookingdates);
        body.put("additionalneeds", "Baby crib");

        // Update response
        Response responseUpdate = RestAssured.given(spec).auth().preemptive().basic("admin",
                        "password123").contentType(ContentType.JSON).body(body.toString())
                .put("/booking/" + bookingid);
        responseUpdate.prettyPrint();

        // Verifications
        // Verify response 200
        Assert.assertEquals(responseUpdate.getStatusCode(), 200, "Status code should be 200, but it's not");

        // Verify All fields
        SoftAssert softAssert = new SoftAssert();
        String actualFirstName = responseUpdate.jsonPath().getString("firstname");
        softAssert.assertEquals(actualFirstName, "Mila", "firstname in response is not expected");

        String actualLastName = responseUpdate.jsonPath().getString("lastname");
        softAssert.assertEquals(actualLastName, "Pruchkovska", "lastname in response is not expected");

        int price = responseUpdate.jsonPath().getInt("totalprice");
        softAssert.assertEquals(price, 125, "totalprice in response is not expected");

        boolean depositpaid = responseUpdate.jsonPath().getBoolean("depositpaid");
        softAssert.assertTrue(depositpaid, "depositpaid should be true, but it's not");

        String actualCheckin = responseUpdate.jsonPath().getString("bookingdates.checkin");
        softAssert.assertEquals(actualCheckin, "2020-03-25", "checkin in response is not expected");

        String actualCheckout = responseUpdate.jsonPath().getString("bookingdates.checkout");
        softAssert.assertEquals(actualCheckout, "2020-03-27", "checkout in response is not expected");

        String actualAdditionalneeds = responseUpdate.jsonPath().getString("additionalneeds");
        softAssert.assertEquals(actualAdditionalneeds, "Baby crib", "additionalneeds in response is not expected");

        softAssert.assertAll();
    }

    @Test(priority = 5)
    void deleteBooking() {
        // Create booking
        Response responseCreate = createBookingManual();
        responseCreate.prettyPrint();

        // Get booking ID
        int bookingid = responseCreate.jsonPath().getInt("bookingid");

        // Delete response
        Response responseDelete = RestAssured.given(spec).auth().preemptive().basic("admin",
                        "password123").contentType(ContentType.JSON)
                .delete("/booking/" + bookingid);
        responseDelete.prettyPrint();

        // Verifications
        // Verify deletion response
        Assert.assertEquals(responseDelete.getStatusCode(), 201, "Status code should be 201, but it's not");
        Assert.assertEquals(responseDelete.getBody().asString(), "Created", "Message should be 'Created' but it is not");

        // Verify response 404
        Response responseGet = RestAssured.given(spec).get("/booking/" + bookingid);
        responseGet.prettyPrint();
        Assert.assertEquals(responseGet.getStatusCode(), 404, "Status code should be 404, but it's not");
        Assert.assertEquals(responseGet.getBody().asString(), "Not Found", "Message should be 'Not Found' but it is not");
    }
}
