package tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.InventoryPage;
import pages.LoginPage;

public class CarritoTest extends BaseTest {

    @Test
    public void agregarDosProductosAlCarrito() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.abrir();
        loginPage.loginCon("standard_user", "secret_sauce");

        InventoryPage inventory = new InventoryPage(driver);
        Assert.assertEquals(inventory.obtenerTitulo(), "Products");

        inventory.agregarAlCarrito("sauce-labs-backpack");
        inventory.agregarAlCarrito("sauce-labs-bike-light");

        Assert.assertEquals(inventory.cantidadEnCarrito(), 2,
                "El carrito no muestra 2 productos");
    }
}