package tests;

// Internal Project Imports (Kelas dari dalam project)
import api.endpoints.Routes;
import org.testng.Assert;
import payloads.Booking;
import payloads.BookingDates;
import utils.DataGenerator;

// External Library Imports (Library bawaan Maven)
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;


// Extends BaseTest -> mewarisi setup Base URL dan Token otentikasi secara otomatis.
public class BookingE2ETests extends BaseTest {
    // Variabel global untuk menyimpan Booking ID agar dapat digunakan oleh skenario GET, PUT, dan DELETE.
    int bookingId;

    // Variabel global untuk menyimpan payload data agar mudah divalidasi dengan response dari server.
    Booking bookingPayload;

    @Test(priority = 1)
    public void testPostCreateBooking(){
        System.out.println("========== START SCENARIO 1: POST CREATE BOOKING ==========");

        // Menyiapkan data request (Payload) menggunakan POJO dan DataGenerator
        BookingDates dates = new BookingDates();
        dates.setCheckin(DataGenerator.getRandomCheckinDate());
        dates.setCheckout(DataGenerator.getRandomCheckoutDate());

        bookingPayload = new Booking();
        bookingPayload.setFirstname(DataGenerator.getRandomFirstName());
        bookingPayload.setLastname(DataGenerator.getRandomLastName());
        bookingPayload.setTotalprice(DataGenerator.getRandomPrice());
        bookingPayload.setDepositpaid(DataGenerator.getRandomDepositPaid());
        bookingPayload.setBookingdates(dates);
        bookingPayload.setAdditionalneeds(DataGenerator.getRandomAdditionalNeeds());


        // Mengirim HTTP POST Request untuk membuat pesanan baru
        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Accept", "application/json")
                .body(bookingPayload)

                .when()
                .post(Routes.post_url);

        // Menampilkan seluruh response dari server ke dalam terminal untuk kebutuhan log
        response.then().log().all();

        // Melakukan validasi (Assertion) terhadap response server, memastikan server mengembalikan status code 200 OK
        response.then().statusCode(200);

        // Mengambil dan menyimpan Booking ID dari response untuk skenario lainnya
        bookingId = response.jsonPath().getInt("bookingid");
        System.out.println("Booking ID didapat: " + bookingId);
        System.out.println("==============================================================");
    }


    @Test(priority = 2)
    public void testGetReadBooking(){
        System.out.println("========== START SCENARIO 4: GET READ BOOKING ==========");

        // Mengirim HTTP GET Request menggunakan bookingId dari skenario testPostCreateBooking
        Response response = RestAssured.given()
                .header("Accept", "application/json")
                .pathParam("id", bookingId)

                .when()
                .get(Routes.get_url);

        // Menampilkan response dari database
        response.then().log().all();

        // Memastikan data ditemukan (Status 200 OK)
        response.then().statusCode(200);

        // JSON SCHEMA VALIDATION -> memastikan struktur tipe data dari server sesuai dengan schema JSON restfulbooker API.
        response.then().assertThat().body(matchesJsonSchemaInClasspath("booking-schema.json"));

        // Memastikan nama di database SAMA PERSIS dengan nama yang tadi dibikin di payload POST.
        String responseFirstName = response.jsonPath().getString("firstname");
        String responseLastName = response.jsonPath().getString("lastname");

        Assert.assertEquals(responseFirstName, bookingPayload.getFirstname());
        Assert.assertEquals(responseLastName, bookingPayload.getLastname());

        System.out.println("VALIDASI SUKSES: Data di server 100% cocok");
        System.out.println("==============================================================");
    }


    @Test(priority = 3)
    public void testUpdateBooking(){
        System.out.println("========== START SCENARIO 7: PUT UPDATE BOOKING ==========");

        // Ubah data untuk update
        String namaBaru = DataGenerator.getRandomFirstName();
        bookingPayload.setFirstname(namaBaru);
        System.out.println("Mencoba mengubah nama depan menjadi: " + namaBaru);

        // Kirim HTTP PUT Request (Wajib dengan token yang didapat dari hasil tembak endpoint /auth di file BaseTest)
        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .pathParam("id", bookingId)
                .body(bookingPayload)

                .when()
                .put(Routes.update_url);

        // Menampilkan response dari server setelah update data
        response.then().log().all();

        // Assertion untuk memastikan update berhasil diterima (status 200 OK)
        response.then().statusCode(200);

        // Memastikan nama di database benar berubah menjadi nama baru
        String responseFirstName = response.jsonPath().getString("firstname");
        Assert.assertEquals(responseFirstName, bookingPayload.getFirstname());

        System.out.println("Update Sukses: Berhasil mengubah data dengan token " + token);
        System.out.println("==============================================================");
    }


    @Test(priority = 4)
    public void testDeleteBooking(){
        System.out.println("========== START SCENARIO 10: DELETE BOOKING ==========");

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .pathParam("id", bookingId)

                .when()
                .delete(Routes.delete_url);

        // Menampilkan response dari server
        response.then().log().all();

        //  Assertion status code
        response.then().statusCode(201);

        System.out.println("Delete berhasil, data atas nama " + bookingPayload.getFirstname() + " berhasil dihapus");
        System.out.println("==============================================================");
    }

    @Test(priority = 5)
    public void testGetBookingAfterDelete(){
        System.out.println("========== START SCENARIO 4.1: GET READ BOOKING AFTER DELETE DATA ==========");

        Response response = RestAssured.given()
                .header("Accept", "application/json")
                .pathParam("id", bookingId)

                .when()
                .get(Routes.get_url);

        // Menampilkan response dari database
        response.then().log().all();

        // Assertion status code harus 404
        response.then().statusCode(404);

        System.out.println("Validasi SUKSES: Data benar-benar terhapus dan tidak ditemukan lagi");
        System.out.println("==============================================================");
    }

}
