# language: es
Requisito: Eliminación de clientes
  Como usuario autenticado
  Quiero eliminar clientes registrados
  Para mantener la base de datos limpia

  Antecedentes:
    Dado el usuario está autenticado con un token JWT válido

  Escenario: Eliminar un cliente exitosamente
    Dado existe un cliente con email "eliminar@email.com"
    Cuando envío una petición DELETE a "/api/customers" para el cliente con email "eliminar@email.com"
    Entonces la respuesta debe tener código 204

  Escenario: Intentar eliminar un cliente que no existe
    Cuando envío una petición DELETE a "/api/customers/00000000-0000-0000-0000-000000000000"
    Entonces la respuesta debe tener código 404
