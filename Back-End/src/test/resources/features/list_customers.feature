Feature: Listado de clientes
  Como usuario autenticado
  Quiero ver todos los clientes registrados

  Background:
    Given el usuario está autenticado con un token JWT válido

  Scenario: Listar clientes cuando existen registros
    Given existen los siguientes clientes:
      | name   | email              |
      | Juan   | juan@email.com     |
      | María  | maria@email.com    |
    When envío una petición GET a "/api/customers"
    Then la respuesta debe tener código 200
    And la respuesta debe contener 2 clientes

  Scenario: Listar clientes cuando no existen registros
    When envío una petición GET a "/api/customers"
    Then la respuesta debe tener código 200
    And la respuesta debe contener 0 clientes
