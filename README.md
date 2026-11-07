
# AviControl

## Sistema de gestión para un negocio de pollos de engorde

### Descripción

AviControl es una aplicación web desarrollada para facilitar la administración de un negocio dedicado a la crianza y comercialización de pollos de engorde.

Permite organizar la información de los lotes, registrar gastos, administrar clientes y controlar las ventas.

### Tecnologías utilizadas

- Java 21
- Spring Boot 4.1.1
- MySQL 8.0
- Spring Data JPA
- Maven
- HTML y CSS
- Visual Studio Code
- Postman

### Módulos principales

- **Lotes:** registro y consulta de los ciclos de crianza.
- **Gastos:** control de los costos de producción.
- **Clientes:** administración de los compradores.
- **Ventas:** registro de cantidad de pollos, peso total, precio por kilogramo y valor de la venta.

### Base de datos

El sistema utiliza MySQL y se conecta a una base de datos llamada `avicontrol`.

Las tablas de la base de datos son:

- lotes
- gastos
- clientes
- ventas
- mortalidad

### Arquitectura

El proyecto utiliza una estructura organizada en capas:

- Entity: representación de las entidades.
- Repository: acceso a la base de datos.
- Service: lógica del sistema.
- Controller: gestión de las solicitudes.
- Templates: vistas HTML del sistema.

### Ejecución del proyecto

1. Abrir la carpeta del proyecto.
2. Verificar que MySQL esté funcionando.
3. Configurar la conexión a MySQL en `application.properties`.
4. Ejecutar en la terminal:

```powershell
.\mvnw.cmd spring-boot:run
```

5. Esperar a que la aplicación inicie correctamente.
6. Abrir el navegador en:

http://localhost:8080

### Pruebas de funcionamiento

Se realizaron pruebas de las operaciones CRUD mediante Postman en los siguientes módulos:

- Lotes
- Gastos
- Clientes
- Ventas

En cada módulo se verificaron las siguientes operaciones:

- GET: consultar registros.
- POST: crear nuevos registros.
- PUT: actualizar registros existentes.
- DELETE: eliminar registros.

### Resultados de las pruebas CRUD

| Módulo | GET | POST | PUT | DELETE |
|---|---|---|---|---|
| Lotes | OK | OK | OK | OK |
| Gastos | OK | OK | OK | OK |
| Clientes | OK | OK | OK | OK |
| Ventas | OK | OK | OK | OK |

Las 16 pruebas CRUD se realizaron satisfactoriamente.

Los registros temporales utilizados durante las pruebas fueron eliminados sin afectar los datos reales del negocio.

### Funcionalidades futuras

Se contempla continuar el desarrollo del sistema incorporando:

- Registro y control de mortalidad.
- Consulta de gastos totales por lote.
- Consulta de ingresos por ventas.
- Cálculo de ganancias por ciclo de producción.
- Generación de reportes.

### Estado del proyecto

El proyecto se encuentra en desarrollo.

Actualmente se han implementado y probado las operaciones CRUD de los módulos de lotes, gastos, clientes y ventas.

### Autor

Proyecto académico de Lenguaje de Programación II.

Programa de Ingeniería de Sistemas.
