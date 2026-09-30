package steps;

import config.Config;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class AuthApiSteps {

    private static final String BASE_URL = Config.get("api.url");

    // Cucumber crea una instancia nueva por escenario:
    // estos campos se comparten entre los pasos de UN escenario, no entre escenarios
    private Response respuesta;
    private String token;

    // Usuario de prueba: sale de la configuración del ambiente
    @Cuando("hago login en la API con el usuario de prueba")
    public void hagoLoginConUsuarioDePrueba() {
        hagoLogin(Config.get("api.usuario"), Config.get("api.clave"));
    }

    // Datos explícitos: disponible para casos negativos (clave incorrecta, etc.)
    @Cuando("hago login en la API con el usuario {string} y la clave {string}")
    public void hagoLogin(String usuario, String clave) {
        String body = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", usuario, clave);
        respuesta = given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/auth/login");
    }

    @Dado("que tengo un token válido del usuario de prueba")
    public void tengoUnTokenValido() {
        hagoLoginConUsuarioDePrueba();
        token = respuesta.then().statusCode(200).extract().path("accessToken");
    }

    @Cuando("consulto mi perfil con ese token")
    public void consultoPerfilConEseToken() {
        consultarPerfilCon("Bearer " + token);
    }

    @Cuando("consulto mi perfil con el token {string}")
    public void consultoPerfilConElToken(String tokenDado) {
        consultarPerfilCon("Bearer " + tokenDado);
    }

    @Cuando("consulto mi perfil con el token alterado")
    public void consultoPerfilConTokenAlterado() {
        consultarPerfilCon("Bearer " + token + "xyz");
    }

    @Cuando("consulto mi perfil sin token")
    public void consultoPerfilSinToken() {
        respuesta = given().baseUri(BASE_URL).when().get("/auth/me");
    }

    @Entonces("la respuesta tiene código {int}")
    public void laRespuestaTieneCodigo(int codigo) {
        respuesta.then().log().ifValidationFails().statusCode(codigo);
    }

    @Entonces("la respuesta incluye el campo {string}")
    public void laRespuestaIncluyeElCampo(String campo) {
        respuesta.then().body(campo, notNullValue());
    }

    @Entonces("el campo {string} es {string}")
    public void elCampoEs(String campo, String valorEsperado) {
        respuesta.then().body(campo, equalTo(valorEsperado));
    }

    @Entonces("el campo {string} es el del usuario de prueba")
    public void elCampoEsElDelUsuarioDePrueba(String campo) {
        elCampoEs(campo, Config.get("api.usuario"));
    }

    // Método privado: no es un step, solo evita repetir código
    private void consultarPerfilCon(String headerAuth) {
        respuesta = given()
                .baseUri(BASE_URL)
                .header("Authorization", headerAuth)
                .when()
                .get("/auth/me");
    }
}