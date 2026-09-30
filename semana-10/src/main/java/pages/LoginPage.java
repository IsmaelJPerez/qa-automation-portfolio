package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage {

    // 1. El driver y la espera: los recibe de afuera
    private final WebDriver driver;
    private final WebDriverWait wait;

    // 2. Localizadores: SOLO acá, en un único lugar
    private final By campoUsuario  = By.id("user-name");
    private final By campoPassword = By.id("password");
    private final By botonLogin    = By.id("login-button");
    private final By mensajeError  = By.cssSelector("[data-test='error']");

    // 3. Constructor
    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // 4. Acciones que puede hacer un usuario en esta página
    public void abrir() {
        driver.get("https://www.saucedemo.com/");
    }

    public void loginCon(String usuario, String password) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(campoUsuario)).sendKeys(usuario);
        driver.findElement(campoPassword).sendKeys(password);
        driver.findElement(botonLogin).click();
    }

    public String obtenerMensajeError() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(mensajeError)).getText();
    }
}