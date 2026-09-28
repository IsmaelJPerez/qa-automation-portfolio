package tests;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.LoginPage;

public class LoginTest extends BaseTest {

    // Caso positivo: login válido
    @Test
    public void loginExitoso() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.abrir();
        loginPage.loginCon("standard_user", "secret_sauce");

        Assert.assertTrue(driver.getCurrentUrl().contains("inventory.html"),
                "No redirigió al inventario después del login");
    }

    // Datos para los casos negativos: {usuario, password, mensaje esperado}
    @DataProvider(name = "loginsInvalidos")
    public Object[][] loginsInvalidos() {
        return new Object[][] {
                {"locked_out_user", "secret_sauce", "locked out"},
                {"standard_user",   "clave_mala",   "do not match"},
                {"",                "secret_sauce", "Username is required"},
                {"standard_user",   "",             "Password is required"}
        };
    }

    // Caso negativo: el mismo test corre 1 vez por cada fila del DataProvider
    @Test(dataProvider = "loginsInvalidos")
    public void loginFallido(String usuario, String password, String mensajeEsperado) {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.abrir();
        loginPage.loginCon(usuario, password);

        String mensaje = loginPage.obtenerMensajeError();
        Assert.assertTrue(mensaje.contains(mensajeEsperado),
                "Mensaje inesperado: " + mensaje);
    }
}