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
        }
        // tags = "@smoke"   // descomentá para correr solo los escenarios con ese tag
)
public class TestRunner extends AbstractTestNGCucumberTests {
}
