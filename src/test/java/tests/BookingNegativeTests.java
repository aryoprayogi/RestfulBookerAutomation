package tests;

// Internal project import
import api.endpoints.Routes;
import payloads.Booking;
import payloads.BookingDates;
import utils.DataGenerator;

//External Library Imports (Maven Library)
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.security.cert.CertificateRevokedException;

import static org.hamcrest.Matchers.*; //UNTUK ASSERTION


// Extends class BaseTest -> untuk ambil konfigurasi BaseURI dan dapetin TOKEN dari endpoint /auth
public class BookingNegativeTests extends BaseTest {

    //Helper Method untuk buat payload dasar
    private Booking getValidBookingPayload(){
        BookingDates dates = new BookingDates();
        dates.setCheckin(DataGenerator.getRandomCheckinDate());
        dates.setCheckout(DataGenerator.getRandomCheckoutDate());

        Booking payload = new Booking();
        payload.setFirstname(DataGenerator.getRandomFirstName());
        payload.setLastname(DataGenerator.getRandomLastName());
        payload.setTotalprice(DataGenerator.getRandomPrice());
        payload.setDepositpaid(DataGenerator.getRandomDepositPaid());
        payload.setBookingdates(dates);
        payload.setAdditionalneeds(DataGenerator.getRandomAdditionalNeeds());
        return payload;
    }

    @Test(priority = 1)
    public void testCreateBookingWithMissingField() {
        System.out.println("========== SCENARIO 2: POST MISSING MANDATORY FIELD ==========");

        Booking payload = getValidBookingPayload();

        //Negative test: Mengosongkan field wajib (firstname) untuk cek validasi error dari server.
        payload.setFirstname(null);

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Accept", "application/json")
                .body(payload)

                .when()
                .post(Routes.post_url);

        response.then().log().all();

        //Assertion
        response.then()
                .statusCode(500)
                .body(equalTo("Internal Server Error"))
                .time(lessThan(5000L));

        System.out.println("=====================");
        System.out.println("Validasi Sukses: Status code 500, Pesan Error tampil, dan time response di bawah 5 detik.");
        System.out.println("====================");
    }


    @Test(priority = 2)
    public void testCreateBookingWithExtremelyLongString(){
        System.out.println("========== SCENARIO 3: POST EXTREMELY LONG STRING ===========");

        Booking payload = getValidBookingPayload();

        //Negative test:  input String 3000 karakter untuk field additionalneeds.
        String longText = String.format("%03000d", 1);
        payload.setAdditionalneeds(longText);

        System.out.println(longText);

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Accept", "application/json")
                .body(payload)

                .when()
                .post(Routes.post_url);

        response.then().log().all();

        //Assertion
        response.then()
                .statusCode(200)
                .time(lessThan(5000L))
                .body("booking.additionalneeds", equalTo(longText));

        System.out.println("=====================");
        System.out.println("Validasi Sukses: API aman dari input string 3000 karakter");
        System.out.println("====================");
    }


    @Test(priority = 3)
    public void testGetBookingWithNonExistentId(){
        System.out.println("========== SCENARIO 5: GET NON-EXISTENT ID ==========");

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Accept", "application/json")
                .pathParam("id", 999999)

                .when()
                .get(Routes.get_url);

        response.then().log().all();

        //Assertion
        response.then()
                .statusCode(404)
                .time(lessThan(5000L))
                .body(equalTo("Not Found"));

        System.out.println("=====================");
        System.out.println("Validasi  Sukses: API Menampilkan pesan error 404 Not Found, dan tidak 500 Internal Server Error");
        System.out.println("=====================");
    }


    @Test(priority = 4)
    public void testGetBookingWithInvalidIdFormat(){
        System.out.println("========== SCENARIO 6: GET INVALID ID FORMAT ==========");

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Accept", "application/json")
                .pathParam("id", "abc")

                .when()
                .get(Routes.get_url);

        response.then().log().all();

        //Assertion
        response.then()
                .statusCode(404)
                .time(lessThan(5000L))
                .body(equalTo("Not Found"));

        System.out.println("=====================");
        System.out.println("Validasi Sukses: API Menampilkan pesan error 404 Not Found, dan tidak 500 Internal Server Error");
        System.out.println("=====================");
    }

    @Test(priority = 5)
    public void testUpdateBookingWithoutToken(){
        System.out.println("========== SCENARIO 8: PUT UPDATE WITHOUT TOKEN ==========");

        Booking payload = getValidBookingPayload();

        //untuk header, sengaja tidak disertakan token yang didapat dari hasil hit endpoint /auth pada Class BaseTest.
        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Accept", "application/json")
                .pathParam("id", 1)

                .when()
                .put(Routes.update_url);

        response.then().log().all();

        //Assertion
        response.then()
                .statusCode(403)
                .time(lessThan(5000L))
                .body(equalTo("Forbidden"));

        System.out.println("=====================");
        System.out.println("Validasi Sukses: API Memblokir Request, dan menampilkan pesan error 403 'Forbidden'");
        System.out.println("=====================");
    }


    @Test(priority = 6)
    public void testUpdateBookingWithInvalidToken(){
        System.out.println("========== SCENARIO 9: PUT UPDATE WITH INVALID TOKEN ==========");

        Booking payload = getValidBookingPayload();

        //Untuk Header, token disengaja diisi tidak sesuai (bukan hasil dari hit endpoint /Auth dari Class BaseTest).
        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Accept", "application/json")
                .header("Cookie", "token=FAKE_TOKEN_HACKED")
                .pathParam("id", 1)
                .body(payload)

                .when()
                .put(Routes.update_url);

        response.then().log().all();

        //Assertion
        response.then()
                .statusCode(403)
                .time(lessThan(5000L))
                .body(equalTo("Forbidden"));

        System.out.println("=====================");
        System.out.println("Validasi Sukses: API Memblokir Request dengan Token Palsu, dan menampilkan pesan error 403 'Forbidden'");
        System.out.println("=====================");
    }


    @Test(priority = 7)
    public void testDeleteBookingTwice(){
        System.out.println("========== SCENARIO 11: DOUBLE DELETE ==========\n");

        Booking payload = getValidBookingPayload();

        //Langkah 1: Membuat Booking
        System.out.println("========= 1. CREATE BOOKING =========");
        Response createRes = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Accept", "application/json")
                .body(payload)

                .when()
                .post(Routes.post_url);

        createRes.then().log().all();

        int idToDelete = createRes.jsonPath().getInt("bookingid");
        System.out.println("\nData Booking berhasil dibuat dengan ID: " + idToDelete);

        // ---------------------------------------------------------------------------------------------------------------
        //Langkah 2: Delete Booking Pertama
        System.out.println("\n========= 2. DELETE BOOKING PERTAMA =========");
        Response firstDeleteRes = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .pathParam("id", idToDelete)

                .when()
                .delete(Routes.delete_url);

        firstDeleteRes.then().log().all();

        //Assertion
        firstDeleteRes.then()
                .statusCode(201)
                .body(equalTo("Created"));

        System.out.println("\nDelete tahap pertama berhasil.");

        // ----------------------------------------------------------------------------------------------------------------
        //Langkah 3
        System.out.println("\n========== 3. DELETE BOOKING KEDUA ==========");
        Response doubleDeleteRes = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .pathParam("id", idToDelete)

                .when()
                .delete(Routes.delete_url);

        doubleDeleteRes.then().log().all();

        //Assertion
        doubleDeleteRes.then()
                .statusCode(405)
                .time(lessThan(5000L))
                .body(equalTo("Method Not Allowed"));

        System.out.println("\nValidasi Sukses: API menolak Double Delete (Method Not Allowed)");
    }
}


