# language: es
Característica: Carrito de compras
  Como usuario logueado
  Quiero agregar productos al carrito
  Para después poder comprarlos

  Antecedentes:
    Dado que inicié sesión como "standard_user"

  @smoke
  Escenario: Agregar un producto al carrito
    Cuando agrego al carrito el producto "sauce-labs-backpack"
    Entonces el carrito muestra 1 producto

  @regression
  Escenario: Agregar varios productos al carrito
    Cuando agrego al carrito los productos:
      | sauce-labs-backpack   |
      | sauce-labs-bike-light |
      | sauce-labs-onesie     |
    Entonces el carrito muestra 3 productos