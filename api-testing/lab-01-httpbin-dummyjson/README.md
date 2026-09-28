# Lab 01 — Autenticación de APIs con HTTPBin + DummyJSON

Laboratorio práctico de testing de APIs con **Postman**: envío de headers, login con token JWT, acceso a un endpoint protegido y casos negativos de autenticación.

- **Autor:** Ismael Pérez
- **Fecha de ejecución:** 17/09/2026 (actualizado el 28/09/2026)
- **Herramientas:** Postman (desktop, Windows), scripts de test en JavaScript (`pm.test`)
- **APIs bajo prueba:** [HTTPBin](https://httpbin.org) y [DummyJSON](https://dummyjson.com/docs/auth) (APIs públicas de práctica)

---

## Objetivo

1. Verificar que un header personalizado viaja correctamente del cliente al servidor.
2. Obtener un `accessToken` mediante login y reutilizarlo automáticamente.
3. Acceder a un endpoint protegido con un token válido.
4. Comparar el comportamiento del endpoint protegido sin token y con tokens inválidos.

---

## Cómo ejecutar

### Desde Postman

1. Importar `Lab-HTTPBin-DummyJSON.postman_collection.json` y `DummyJSON.postman_environment.json`.
2. Ejecutar los requests en orden (01 → 07), o usar **Run collection**.
3. El request `02` guarda el token en la variable de colección `accessToken`; los siguientes lo usan con `{{accessToken}}`.
4. **Importante:** antes de los casos 04, borrar las cookies de `dummyjson.com` (botón *Cookies* en Postman). Ver hallazgo H2.
5. El token dura **30 minutos** (`expiresInMins: 30` en el login). Si pasó más tiempo, volver a ejecutar `02` antes de los demás.
6. El test de `04c` **falla a propósito**: su aserción espera el comportamiento correcto (401) y el fallo es la evidencia de BUG-001. No se modificó para que pase.

El request `04a` limpia las cookies de `dummyjson.com` en su script pre-request, así que el caso "sin token" se prueba de verdad tanto en Postman como en Newman. Ver hallazgo H2.

### Desde la terminal con Newman

Requiere Node.js y `npm install -g newman newman-reporter-htmlextra`.

```bash
newman run Lab-HTTPBin-DummyJSON.postman_collection.json \
  -e DummyJSON.postman_environment.json \
  -r "cli,htmlextra"
```

En PowerShell las comillas alrededor de `cli,htmlextra` son necesarias: sin ellas la coma se interpreta como separador de lista y el reporter no se encuentra.

El reporte HTML se genera en `newman/` (carpeta excluida del repositorio por ser salida generada). En `evidencia/` se conserva una corrida concreta como respaldo.

**Newman termina con código de salida 1** por el fallo de `04c`. Es el comportamiento correcto: así le informa a un sistema de CI que la suite no pasó.

### Última corrida registrada (28/09/2026)

| Métrica | Total | Fallidos |
|---|---|---|
| Requests | 9 | 0 |
| Test scripts | 9 | 0 |
| Pre-request scripts | 2 | 0 |
| Aserciones | 18 | 1 |

El único fallo es la aserción de BUG-001. Evidencia: `evidencia/reporte-newman-2026-09-28.html`

---

## Casos de prueba

| ID | Request | Datos | Esperado | Obtenido | Resultado |
|---|---|---|---|---|---|
| 01 | `GET httpbin.org/headers` | Header `X-Demo-Trace: lab-ismael-001` | 200 y header reflejado en la respuesta | 200, header reflejado | ✅ Pass |
| 02 | `POST dummyjson.com/auth/login` | Usuario `emilys` | 200 y `accessToken` en la respuesta | 200, token recibido y guardado | ✅ Pass |
| 03 | `GET dummyjson.com/auth/me` | `Bearer {{accessToken}}` | 200 y datos del usuario `emilys` | 200, datos correctos | ✅ Pass |
| 04a | `GET dummyjson.com/auth/me` | Sin token, **con** cookie de login | 401 | **200** | ⚠️ Ver H2 |
| 04a | `GET dummyjson.com/auth/me` | Sin token, **sin** cookies | 401 | 401 `"Access Token is required"` | ✅ Pass |
| 04b | `GET dummyjson.com/auth/me` | `Bearer abc123` (token malformado) | 401 | 401 `"Invalid/Expired Token!"` | ✅ Pass |
| 04c | `GET dummyjson.com/auth/me` | `Bearer {{accessToken}}xyz` (firma alterada) | 401 | **500** `"invalid signature"` | ❌ Fail — ver BUG-001 |
| 04d | `GET dummyjson.com/auth/me` *(caso manual, fuera de la colección)* | `Bearer {{accessToken}}` vencido (login con `expiresInMins: 30`, enviado a los ~35 min) | 401 | 401 `"Token Expired!"` | ✅ Pass — ver H5 |
| 05 | `GET {{baseUrl}}/posts` | Environment `DummyJSON` | 200, lista de posts no vacía, URL resuelta desde `baseUrl` | 200, las 3 validaciones correctas | ✅ Pass |
| 06 | `POST httpbin.org/post` | Variables dinámicas: `$randomFullName`, `$randomEmail`, `$randomInt`, `$guid`, `$timestamp` | 200 y datos con formato válido | 200; `email` y `emailConfirmacion` distintos | ✅ Pass — ver H6 |
| 07 | `POST httpbin.org/post` | Email generado una sola vez en pre-request y guardado en `emailPrueba` | 200; `email` = `emailConfirmacion` = valor generado | 200, los tres valores coinciden | ✅ Pass |

Cada request tiene tests automáticos en **Scripts → After response** que validan el status code y el contenido relevante.

---

## Hallazgos

### BUG-001 — Token con firma alterada devuelve 500 en lugar de 401

| Campo | Detalle |
|---|---|
| **Endpoint** | `GET https://dummyjson.com/auth/me` |
| **Severidad** | Media |
| **Tipo** | Manejo de errores / Seguridad |
| **Entorno** | Postman desktop, Windows — 17/09/2026 |

**Precondiciones:** tener un `accessToken` válido obtenido desde `POST /auth/login`. Cookies de `dummyjson.com` borradas.

**Pasos para reproducir:**
1. Hacer login con `POST /auth/login` y copiar el `accessToken`.
2. Enviar `GET /auth/me` con el header `Authorization: Bearer <accessToken>xyz` (el token válido con caracteres agregados al final).

**Resultado esperado:** `401 Unauthorized` con un mensaje de token inválido, igual que ocurre con un token malformado.

**Resultado obtenido:** `500 Internal Server Error` con el body `{"message": "invalid signature"}`.

**Análisis:**
- La API detecta correctamente que la firma no es válida, pero no maneja la excepción y la devuelve como error interno del servidor.
- Con un token sin formato JWT (`abc123`) sí responde 401, por lo que el problema es específico de tokens con **estructura válida y firma adulterada**, que es el escenario típico de un intento de falsificación.
- Impacto: un 500 indica falla del servidor y no del cliente; confunde a quien consume la API, puede disparar alertas de monitoreo falsas y expone detalles internos de la librería de validación.

**Evidencia:** `capturas/04c-firma-alterada-500.png` (respuesta 500) y `capturas/04c-firma-alterada-500-test-failed.png` (el test en rojo: *expected 401 but got 500*)

---

### H2 — El endpoint acepta autenticación por cookie además del header Bearer

`POST /auth/login` devuelve el token en el body **y también lo guarda como cookie**. Postman conserva esa cookie, por lo que `GET /auth/me` **sin header Authorization** respondió **200 OK**. Tras borrar las cookies respondió **401 "Access Token is required"**.

**Conclusión:** para probar un caso "sin autenticación" hay que eliminar **todas** las vías de autenticación (header y cookies). De lo contrario el caso negativo da un falso resultado y se puede reportar un bug que no existe, o no detectar uno real.

**Evidencia:** `capturas/04a-sin-token-401.png` (resultado tras borrar las cookies). El 200 inicial se observó en vivo durante la ejecución y no quedó capturado.

---

### H3 — La respuesta de `/auth/me` expone la contraseña del usuario

La respuesta de `GET /auth/me` incluye el campo `"password"` en texto plano. DummyJSON lo hace a propósito por ser una API de datos ficticios, pero **en un sistema real sería un defecto de seguridad crítico**: ningún endpoint debería devolver contraseñas, ni siquiera hasheadas.

---

### H4 — Un body mal formado se rechaza antes de validar la autenticación

Durante la ejecución se envió por error un body de texto no JSON en un `GET /auth/me`. La API respondió **400 Bad Request** (`Unexpected token ... is not valid JSON`) sin llegar a evaluar el token. Esto muestra el orden de validación del servidor: primero el formato del request, después las credenciales.

**Evidencia:** `capturas/extra-h4-body-malformado-400.png`

---

### H5 — El vencimiento del token se respeta y tiene un mensaje propio

El login se hizo con `expiresInMins: 30`. Al volver a ejecutar `GET /auth/me` con el mismo token unos 35 minutos después, la API respondió **401 "Token Expired!"**. Comportamiento correcto, y con un mensaje distinto a los otros casos de 401:

| Situación | Mensaje |
|---|---|
| Sin token | `Access Token is required` |
| Token malformado | `Invalid/Expired Token!` |
| Token vencido | `Token Expired!` |
| Token con firma alterada | `invalid signature` (con status 500, BUG-001) |

Además confirma que el parámetro `expiresInMins` del login funciona como indica la documentación. Tras un nuevo login, el request 03 vuelve a responder 200.

**Evidencia:** `capturas/04d-token-vencido-401.png`

---

### H6 — Cada variable dinámica genera un valor nuevo en cada aparición

En el request 06, los campos `email` y `emailConfirmacion` usan cada uno `{{$randomEmail}}`. Postman genera **un email distinto por cada aparición**, así que los dos campos nunca coinciden. El test *"Cada {{$randomEmail}} genera un valor distinto"* documenta ese comportamiento.

**Solución (request 07):** generar el valor una sola vez en el script pre-request con `pm.variables.replaceIn("{{$randomEmail}}")`, guardarlo en la variable `emailPrueba` y usar `{{emailPrueba}}` en los dos campos.

**Por qué importa:** un formulario de registro con "confirmar email" fallaría siempre con datos generados de la primera forma, y se podría reportar como bug algo que en realidad es un error en los datos de prueba.

---

## Lecciones aprendidas

- Un status 401 en un caso negativo es un **resultado correcto**; las sugerencias automáticas de Postman que proponen "arreglar" la aserción no reemplazan el criterio del tester.
- **No hay que modificar un test para que pase cuando detectó un bug** (por ejemplo, cambiar la aserción a 500). El test en rojo es la evidencia.
- Antes de reportar, conviene **acotar el bug** con variantes (token malformado vs. firma alterada) para describir exactamente cuándo ocurre.
- Encadenar requests con variables (`pm.collectionVariables.set`) evita copiar tokens a mano y hace la colección reutilizable.
- No se deben subir tokens ni credenciales reales a un repositorio: la colección se exporta con la variable `accessToken` vacía.

---

## Estructura

```
lab-01-httpbin-dummyjson/
├── README.md
├── Lab-HTTPBin-DummyJSON.postman_collection.json
├── DummyJSON.postman_environment.json
├── evidencia/
│   └── reporte-newman-2026-09-28.html
└── capturas/
    ├── 01-headers-x-demo-trace-reflejado.png
    ├── 01-headers-script-y-response.png
    ├── 02-login-test-results.png
    ├── 03-me-token-valido.png
    ├── 04a-sin-token-401.png
    ├── 04b-token-malformado-401.png
    ├── 04b-token-malformado-401-abc123.png
    ├── 04c-firma-alterada-500.png
    ├── 04c-firma-alterada-500-test-failed.png
    ├── 04d-token-vencido-401.png
    └── extra-h4-body-malformado-400.png
```
