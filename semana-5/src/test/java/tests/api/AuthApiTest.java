package tests.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class AuthApiTest {

    private String accessToken;

    private static final String LOGIN_BODY =
            "{\"username\":\"emilys\",\"password\":\"emilyspass\",\"expiresInMins\":30}";

    // Se ejecuta UNA vez antes de todos los tests de la clase (como el request 02)
    @BeforeClass
    public void obtenerToken() {
        RestAssured.baseURI = "https://dummyjson.com";

        accessToken = given()
                .contentType(ContentType.JSON)
                .body(LOGIN_BODY)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .extract().path("accessToken");
    }

    // Caso 02
    @Test
    public void loginExitosoDevuelveToken() {
        given()
                .contentType(ContentType.JSON)
                .body(LOGIN_BODY)
                .when()
                .post("/auth/login")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .body("accessToken", notNullValue())
                .body("username", equalTo("emilys"));
    }

    // Caso 03
    @Test
    public void meConTokenValidoDevuelveUsuario() {
        given()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get("/auth/me")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .body("username", equalTo("emilys"));
    }

    // Caso 04a
    @Test
    public void meSinTokenDevuelve401() {
        given()
                .when()
                .get("/auth/me")
                .then()
                .log().ifValidationFails()
                .statusCode(401)
                .body("message", equalTo("Access Token is required"));
    }

    // Caso 04b
    @Test
    public void meConTokenMalformadoDevuelve401() {
        given()
                .header("Authorization", "Bearer abc123")
                .when()
                .get("/auth/me")
                .then()
                .log().ifValidationFails()
                .statusCode(401)
                .body("message", equalTo("Invalid/Expired Token!"));
    }

    // Caso 04c — BUG-001: este test FALLA a propósito, espera el comportamiento correcto
    @Test(description = "BUG-001: token con firma alterada deberia devolver 401, devuelve 500")
    public void meConFirmaAlteradaDevuelve401() {
        given()
                .header("Authorization", "Bearer " + accessToken + "xyz")
                .when()
                .get("/auth/me")
                .then()
                .log().ifValidationFails()
                .statusCode(401);
    }
}