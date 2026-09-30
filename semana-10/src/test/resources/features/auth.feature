# language: es
@api
Característica: Autenticación en la API de DummyJSON
  Como cliente de la API
  Quiero autenticarme con usuario y clave
  Para acceder a mis datos protegidos

  @smoke
  Escenario: Login válido devuelve un token
    Cuando hago login en la API con el usuario "emilys" y la clave "emilyspass"
    Entonces la respuesta tiene código 200
    Y la respuesta incluye el campo "accessToken"
    Y el campo "username" es "emilys"

  @regression
  Escenario: Consultar mi perfil con un token válido
    Dado que tengo un token válido del usuario "emilys" con clave "emilyspass"
    Cuando consulto mi perfil con ese token
    Entonces la respuesta tiene código 200
    Y el campo "username" es "emilys"

  @regression
  Escenario: Consultar mi perfil sin token
    Cuando consulto mi perfil sin token
    Entonces la respuesta tiene código 401
    Y el campo "message" es "Access Token is required"

  @regression
  Escenario: Consultar mi perfil con un token malformado
    Cuando consulto mi perfil con el token "abc123"
    Entonces la respuesta tiene código 401
    Y el campo "message" es "Invalid/Expired Token!"

  @bug
  Escenario: BUG-001 - Token con firma alterada debería devolver 401
    Dado que tengo un token válido del usuario "emilys" con clave "emilyspass"
    Cuando consulto mi perfil con el token alterado
    Entonces la respuesta tiene código 401