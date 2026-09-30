# language: es
Característica: Login en SauceDemo
  Como usuario de la tienda
  Quiero iniciar sesión con mis credenciales
  Para poder ver y comprar productos

  Antecedentes:
    Dado que estoy en la página de login

  @smoke
  Escenario: Login exitoso con usuario estándar
    Cuando inicio sesión con el usuario "standard_user" y la clave "secret_sauce"
    Entonces veo la página de productos

  @regression
  Esquema del escenario: Login fallido muestra un mensaje de error
    Cuando inicio sesión con el usuario "<usuario>" y la clave "<clave>"
    Entonces veo un error que contiene "<mensaje>"

    Ejemplos:
      | usuario         | clave        | mensaje              |
      | locked_out_user | secret_sauce | locked out           |
      | standard_user   | clave_mala   | do not match         |
      |                 | secret_sauce | Username is required |
      | standard_user   |              | Password is required |
