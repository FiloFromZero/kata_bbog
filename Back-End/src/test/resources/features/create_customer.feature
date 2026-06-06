Feature: Creación de clientes
  Como usuario autenticado
  Quiero registrar nuevos clientes
  Para mantener la base de datos actualizada

  Background:
    Given el usuario está autenticado con un token JWT válido

  Scenario: Crear un cliente exitosamente
    When envío una petición POST a "/api/customers" con:
      | name  | email            |
      | Juan  | juan@email.com   |
    Then la respuesta debe tener código 201
    And la respuesta debe contener el nombre "Juan"
    And la respuesta debe contener el email "juan@email.com"

  Scenario: Rechazar cliente con email duplicado
    Given existe un cliente con email "juan@email.com"
    When envío una petición POST a "/api/customers" con:
      | name  | email            |
      | Pedro | juan@email.com   |
    Then la respuesta debe tener código 409

  Scenario: Rechazar cliente con datos inválidos
    When envío una petición POST a "/api/customers" con:
      | name | email       |
      |      | no-es-email |
    Then la respuesta debe tener código 400
