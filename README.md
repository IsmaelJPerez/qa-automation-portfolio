# QA Automation Portfolio — Ismael Pérez

Portfolio de testing y automatización de pruebas, construido siguiendo un plan de estudio de 16 semanas: de QA manual a automatización con Java.

Cada carpeta corresponde a una etapa del plan. Todo el trabajo se integró mediante ramas y pull requests.

---

## Stack

| Área | Herramientas |
|---|---|
| Lenguaje | Java 17, Maven |
| Automatización web | Selenium WebDriver 4, Page Object Model |
| Framework de tests | TestNG (suites, DataProvider, listeners), JUnit 5 |
| Reportes | ExtentReports (HTML con capturas embebidas) |
| Testing de APIs | Postman (colecciones, environments, scripts), Newman, RestAssured, JSON Schema |
| Control de versiones | Git + GitHub (ramas, pull requests) |

---

## Proyecto destacado: framework web con Selenium (`semana-5/`)

Automatización de [saucedemo.com](https://www.saucedemo.com) con Page Object Model y TestNG.

- **Page Objects:** `LoginPage`, `InventoryPage`. Los localizadores viven en un único lugar y los tests no usan `findElement` directamente.
- **`BaseTest`:** abre y cierra Chrome antes y después de cada test (`@BeforeMethod` / `@AfterMethod`).
- **Tests parametrizados:** `LoginTest` cubre 1 caso positivo y 4 negativos (usuario bloqueado, clave incorrecta, campos vacíos) con un único método y `@DataProvider`.
- **Capturas automáticas:** cuando un test falla, se guarda una captura de pantalla con fecha y hora.
- **Reporte HTML:** un `ITestListener` genera un reporte con ExtentReports; los tests fallidos incluyen el error (esperado vs. obtenido) y la captura embebida.
- **Ejecución en paralelo:** la suite `testng.xml` corre los grupos *Login* y *Carrito* en hilos separados.
- **Tests de API con RestAssured:** casos de autenticación del Lab 01 automatizados en Java (incluido BUG-001), CRUD de productos y validación de contrato con JSON Schema, en la misma suite que los tests web.

**Cómo ejecutarlo:** abrir `semana-5` en IntelliJ IDEA → clic derecho en `testng.xml` → *Run*. El reporte se genera en `semana-5/reportes/reporte.html`.

---

## Testing de APIs (`api-testing/`)

### Lab 01 — Autenticación con HTTPBin + DummyJSON

Colección de Postman con login por token JWT, acceso a un endpoint protegido y casos negativos de autenticación, ejecutable también desde la terminal con Newman.

- Encadenamiento de requests con variables (el token del login se reutiliza automáticamente).
- Casos negativos: sin token, token malformado, token con firma alterada, token vencido.
- **BUG-001 reportado:** un token con firma alterada devuelve `500` en lugar de `401`.
- Hallazgos documentados (autenticación por cookie, exposición de datos sensibles, orden de validación del servidor).

Detalle completo, casos de prueba y evidencias en el [README del lab](api-testing/lab-01-httpbin-dummyjson/README.md).

---

## Recorrido del plan

| Carpeta | Contenido |
|---|---|
| `semana-1` | Fundamentos de Java: calculadora, validador de datos, FizzBuzz |
| `semana-2` | Programación orientada a objetos: clases relacionadas y herencia |
| `semana-3` | Excepciones personalizadas, archivos y primeros tests con JUnit 5 |
| `semana-4` | Primeros tests con Selenium WebDriver: locators, esperas explícitas, dropdowns |
| `semana-5` | Framework con POM + TestNG, reportes, capturas y paralelo (semanas 5 y 6) |
| `api-testing` | Laboratorios de testing de APIs con Postman y Newman |

---

## Próximos pasos

- BDD con Cucumber (Gherkin)
- Pipeline de integración continua con GitHub Actions

---

## Sobre mí

Vengo de más de 20 años de trabajo en carpintería, cerrajería y corte láser, donde la precisión y la atención al detalle son el oficio. Desde 2026 me estoy formando como QA Tester (testing manual, SQL, Jira, TestLink) y en automatización de pruebas.

📍 Uruguay · [GitHub](https://github.com/IsmaelJPerez)
