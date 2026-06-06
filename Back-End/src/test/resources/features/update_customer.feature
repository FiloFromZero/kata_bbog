# language: es
Requisito: Actualización de clientes
  Como usuario autenticado
  Quiero actualizar los datos de clientes registrados
  Para mantener la información al día

  Antecedentes:
    Dado el usuario está autenticado con un token JWT válido

  Escenario: Actualizar un cliente exitosamente
    Dado existe un cliente con email "actualizar@email.com"
    Cuando envío una petición PUT a "/api/customers" para el cliente con email "actualizar@email.com" con:
      | name             | email                 |
      | Juan Actualizado | actualizado@email.com |
    Entonces la respuesta debe tener código 200
    Y la respuesta debe contener el nombre "Juan Actualizado"
    Y la respuesta debe contener el email "actualizado@email.com"

  Escenario: Rechazar actualización con email duplicado
    Dado existe un cliente con email "cliente1@email.com"
    Y existe un cliente con email "cliente2@email.com"
    Cuando envío una petición PUT a "/api/customers" para el cliente con email "cliente1@email.com" con:
      | name     | email              |
      | Cliente1 | cliente2@email.com |
    Entonces la respuesta debe tener código 409

  Escenario: Rechazar actualización con datos inválidos
    Dado existe un cliente con email "cliente3@email.com"
    Cuando envío una petición PUT a "/api/customers" para el cliente con email "cliente3@email.com" con:
      | name | email       |
      |      | no-es-email |
    Entonces la respuesta debe tener código 400

  Escenario: Intentar actualizar un cliente que no existe
    Cuando envío una petición PUT a "/api/customers/00000000-0000-0000-0000-000000000000" con:
      | name | email          |
      | Pepito | pepito@email.com |
    Entonces la respuesta debe tener código 404
