package steps;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Hooks de los escenarios web: abren y cierran Chrome.
// Los escenarios @api no pasan por acá (no necesitan navegador).
public class Hooks {

    // ThreadLocal: cada hilo (cada escenario en paralelo) tiene SU PROPIO driver.
    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    private static final Logger log = LoggerFactory.getLogger(Hooks.class);

    // Sin filtro de tags: corren para TODOS los escenarios (web y API)
    @Before(order = 0)
    public void logInicio(Scenario scenario) {
        log.info(">> INICIA: {} {}", scenario.getName(), scenario.getSourceTagNames());
    }

    @After(order = 0)
    public void logFin(Scenario scenario) {
        log.info("<< TERMINA: {} -> {}", scenario.getName(), scenario.getStatus());
    }
    public static WebDriver getDriver() {
        return driver.get();
    }

    @Before("not @api")
    public void abrirNavegador() {
        WebDriver nuevo = new ChromeDriver();
        nuevo.manage().window().maximize();
        driver.set(nuevo);
    }

    @After("not @api")
    public void cerrarNavegador(Scenario scenario) {
        WebDriver actual = driver.get();
        if (actual == null) {
            return;
        }
        // Si el escenario falló, adjuntamos la captura al reporte de Cucumber
        if (scenario.isFailed()) {
            byte[] captura = ((TakesScreenshot) actual).getScreenshotAs(OutputType.BYTES);
            scenario.attach(captura, "image/png", scenario.getName());
        }
        actual.quit();
        driver.remove();   // vaciamos la "caja" de este hilo
    }
}