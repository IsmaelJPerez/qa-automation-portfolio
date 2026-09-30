package steps;

import config.Config;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import org.testng.Assert;
import pages.InventoryPage;
import pages.LoginPage;

import java.util.List;

public class CarritoSteps {

    private InventoryPage inventario;

    // Login completo en un solo paso: el foco de este feature es el carrito
    @Dado("que inicié sesión como el usuario {string}")
    public void queInicieSesionComo(String alias) {
        LoginPage loginPage = new LoginPage(Hooks.getDriver());
        loginPage.abrir(Config.get("web.url"));
        loginPage.loginCon(Config.get("usuario." + alias), Config.get("web.clave"));

        inventario = new InventoryPage(Hooks.getDriver());
        Assert.assertEquals(inventario.obtenerTitulo(), "Products");
    }

    @Cuando("agrego al carrito el producto {string}")
    public void agregoAlCarritoElProducto(String idProducto) {
        inventario.agregarAlCarrito(idProducto);
    }

    // La tabla del .feature llega como una lista de Strings
    @Cuando("agrego al carrito los productos:")
    public void agregoAlCarritoLosProductos(List<String> productos) {
        for (String producto : productos) {
            inventario.agregarAlCarrito(producto);
        }
    }

    // "producto(s)": la "s" es opcional, sirve para "1 producto" y "3 productos"
    @Entonces("el carrito muestra {int} producto(s)")
    public void elCarritoMuestra(int cantidadEsperada) {
        Assert.assertEquals(inventario.cantidadEnCarrito(), cantidadEsperada,
                "Cantidad incorrecta en el carrito");
    }
}