package steps;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

// Hooks = el equivalente a @BeforeMethod / @AfterMethod del BaseTest de la semana 5.
// Cucumber los corre antes y después de CADA escenario.
public class Hooks {

    // Por ahora el driver es estático para compartirlo con las step definitions.
    // En la semana 10 lo mejoramos (inyección de dependencias / ejecución en paralelo).
    private static WebDriver driver;

    public static WebDriver getDriver() {
        return driver;
    }

    @Before
    public void abrirNavegador() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @After
    public void cerrarNavegador(Scenario scenario) {
        // Si el escenario falló, adjuntamos la captura al reporte de Cucumber
        if (scenario.isFailed() && driver != null) {
            byte[] captura = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            scenario.attach(captura, "image/png", scenario.getName());
        }
        if (driver != null) {
            driver.quit();
        }
    }
}
