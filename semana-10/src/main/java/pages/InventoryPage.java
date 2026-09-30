package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class InventoryPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // Localizadores
    private final By titulo        = By.className("title");
    private final By badgeCarrito  = By.className("shopping_cart_badge");

    public InventoryPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public String obtenerTitulo() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(titulo)).getText();
    }

    // idProducto: ej. "sauce-labs-backpack" → botón "add-to-cart-sauce-labs-backpack"
    public void agregarAlCarrito(String idProducto) {
        By boton = By.id("add-to-cart-" + idProducto);
        wait.until(ExpectedConditions.elementToBeClickable(boton)).click();
    }

    public int cantidadEnCarrito() {
        String texto = wait.until(ExpectedConditions.visibilityOfElementLocated(badgeCarrito)).getText();
        return Integer.parseInt(texto);
    }
}