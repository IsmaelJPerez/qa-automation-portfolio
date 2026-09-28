package tests;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import listeners.ReporteListener;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BaseTest {

    protected WebDriver driver;

    @BeforeMethod
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        // Si el test falló, sacamos captura ANTES de cerrar el navegador
        if (result.getStatus() == ITestResult.FAILURE && driver != null) {
            tomarCaptura(result.getName());
            adjuntarCapturaAlReporte();
        }
        if (driver != null) {
            driver.quit();
        }
    }

    private void tomarCaptura(String nombreTest) {
        File origen = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        String fecha = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        Path destino = Paths.get("capturas", nombreTest + "_" + fecha + ".png");
        try {
            Files.createDirectories(destino.getParent());
            Files.copy(origen.toPath(), destino);
            System.out.println("Captura guardada: " + destino.toAbsolutePath());
        } catch (IOException e) {
            System.out.println("No se pudo guardar la captura: " + e.getMessage());
        }
    }
    private void adjuntarCapturaAlReporte() {
        ExtentTest test = ReporteListener.getTestActual();
        if (test != null) {
            String base64 = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
            test.fail("Captura al momento del fallo",
                    MediaEntityBuilder.createScreenCaptureFromBase64String(base64).build());
        }
    }
}