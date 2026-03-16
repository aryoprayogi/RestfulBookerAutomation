package tests;

import api.endpoints.Routes;
import payloads.Auth;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;

public class BaseTest {
    public String token;

    @BeforeClass
    public void setup(){
        // Set Alamat Pusat dari buku alamat (Routes)
        RestAssured.baseURI = Routes.base_url;

        // Siapin data (Username & Password) pakai cetakan POJO Auth
        Auth authPayload = new Auth();
        authPayload.setUsername("admin");
        authPayload.setPassword("password123");

        // Tembak endpoint /auth untuk ambil token sekaligus simpan langsung balasannya (JSON Response nya) ke dalam variabel response
        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(authPayload)

                .when()
                .post(Routes.auth_url);

        // Buka JSON Response, ambil isinya, masukin ke token
        token = response.jsonPath().getString("token");

        // Print ke terminal
        System.out.println("=========================================");
        System.out.println("🔑 TOKEN berhasil diambil: " + token);
        System.out.println("=========================================");


    }

}
