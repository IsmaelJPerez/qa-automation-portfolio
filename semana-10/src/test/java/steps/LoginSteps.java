package steps;

import config.Config;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import org.testng.Assert;
import pages.InventoryPage;
import pages.LoginPage;

public class LoginSteps {

    private LoginPage loginPage;

    @Dado("que estoy en la página de login")
    public void queEstoyEnLaPaginaDeLogin() {
        loginPage = new LoginPage(Hooks.getDriver());
        loginPage.abrir(Config.get("web.url"));
    }

    // Usuario válido: el nombre real y la clave salen de la configuración del ambiente
    @Cuando("inicio sesión como el usuario {string}")
    public void inicioSesionComo(String alias) {
        loginPage.loginCon(Config.get("usuario." + alias), Config.get("web.clave"));
    }

    // Datos explícitos: para los casos negativos, donde el dato ES lo que se prueba
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