package runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

// El runner reemplaza al testng.xml: le dice a Cucumber qué .feature correr
// y dónde están las step definitions. TestNG lo ejecuta como un test más.
@CucumberOptions(
        features = "src/test/resources/features",
        glue = "steps",
        plugin = {
                "pretty",
                "html:target/cucumber-report.html"
        },
        tags = "not @bug"
)
public class TestRunner extends AbstractTestNGCucumberTests {
}
