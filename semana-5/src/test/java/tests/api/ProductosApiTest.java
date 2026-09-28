package tests.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class ProductosApiTest {

    @BeforeClass
    public void setUp() {
        RestAssured.baseURI = "https://dummyjson.com";
    }

    // READ
    @Test
    public void obtenerProductoPorId() {
        given()
                .when()
                .get("/products/1")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("title", not(emptyOrNullString()))
                .body("price", greaterThan(0f));
    }

    // CREATE
    @Test
    public void crearProducto() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"title\":\"Producto QA Ismael\",\"price\":99.9}")
                .when()
                .post("/products/add")
                .then()
                .log().ifValidationFails()
                .statusCode(201)
                .body("id", notNullValue())
                .body("title", equalTo("Producto QA Ismael"));
    }

    // UPDATE
    @Test
    public void actualizarTituloDeProducto() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"title\":\"Titulo actualizado\"}")
                .when()
                .put("/products/1")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("title", equalTo("Titulo actualizado"));
    }

    // DELETE
    @Test
    public void borrarProducto() {
        given()
                .when()
                .delete("/products/1")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("isDeleted", equalTo(true))
                .body("deletedOn", notNullValue());
    }

    // Caso negativo: producto inexistente
    @Test
    public void productoInexistenteDevuelve404() {
        given()
                .when()
                .get("/products/99999")
                .then()
                .log().ifValidationFails()
                .statusCode(404);
    }
    // Contrato: la respuesta cumple la estructura definida en el esquema
    @Test
    public void productoCumpleElEsquema() {
        given()
                .when()
                .get("/products/1")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/producto-schema.json"));
    }
}