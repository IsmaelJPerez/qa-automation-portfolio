package listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.util.Arrays;

public class ReporteListener implements ITestListener {

    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> testActual = new ThreadLocal<>();

    // Se ejecuta al arrancar: crea el reporte (una sola vez)
    @Override
    public void onStart(ITestContext context) {
        if (extent == null) {
            ExtentSparkReporter spark = new ExtentSparkReporter("reportes/reporte.html");
            spark.config().setDocumentTitle("Reporte QA Automation");
            spark.config().setReportName("Suite SauceDemo");

            extent = new ExtentReports();
            extent.attachReporter(spark);
            extent.setSystemInfo("Tester", "Ismael Pérez");
            extent.setSystemInfo("Navegador", "Chrome");
        }
    }

    // Cada test que arranca = una entrada nueva en el reporte
    @Override
    public void onTestStart(ITestResult result) {
        String nombre = result.getMethod().getMethodName();
        if (result.getParameters().length > 0) {
            nombre += " " + Arrays.toString(result.getParameters());
        }
        testActual.set(extent.createTest(nombre));
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        testActual.get().pass("Test aprobado");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        testActual.get().fail(result.getThrowable());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        testActual.get().skip("Test omitido");
    }

    // Al terminar: escribe el HTML en disco
    @Override
    public void onFinish(ITestContext context) {
        extent.flush();
    }
    // Permite que otras clases (BaseTest) agreguen cosas al test actual
    public static ExtentTest getTestActual() {
        return testActual.get();
    }
}