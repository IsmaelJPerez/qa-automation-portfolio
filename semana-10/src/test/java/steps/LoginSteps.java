package steps;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import org.testng.Assert;
import pages.InventoryPage;
import pages.LoginPage;

// Step definitions: el "pegamento" entre cada frase del .feature y los Page Objects.
public class LoginSteps {

    private LoginPage loginPage;

    @Dado("que estoy en la página de login")
    public void queEstoyEnLaPaginaDeLogin() {
        loginPage = new LoginPage(Hooks.getDriver());
        loginPage.abrir();
    }

    // {string} captura el texto entre comillas del .feature y lo pasa como parámetro
    @Cuando("inicio sesión con el usuario {string} y la clave {string}")
    public void inicioSesionCon(String usuario, String clave) {
        loginPage.loginCon(usuario, clave);
    }

    @Entonces("veo la página de productos")
    public void veoLaPaginaDeProductos() {
        InventoryPage inventario = new InventoryPage(Hooks.getDriver());
        Assert.assertEquals(inventario.obtenerTitulo(), "Products");
    }

    @Entonces("veo un error que contiene {string}")
    public void veoUnErrorQueContiene(String mensajeEsperado) {
        String mensaje = loginPage.obtenerMensajeError();
        Assert.assertTrue(mensaje.contains(mensajeEsperado),
                "Mensaje inesperado: " + mensaje);
    }
}
