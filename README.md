# AJA Desktop

Aplicación de escritorio con JavaFX que incluye una pantalla de login.

## Requisitos

- Java 17 o superior
- Maven 3.6+

## Ejecutar la aplicación

```bash
mvn javafx:run
```

## Compilar

```bash
mvn clean compile
```

## Estructura del proyecto

```
src/main/java/com/aja/
├── App.java                    # Clase principal
└── controller/
    ├── LoginController.java    # Controlador del login
    └── MainDashboardController.java  # Controlador del panel principal

src/main/resources/
├── views/
│   ├── login.fxml              # Vista de login
│   └── MainDashboard.fxml      # Panel principal (Usuarios, Foro, Eventos, Mensajes)
└── styles/
    ├── login.css               # Estilos de la pantalla de login
    └── dashboard.css           # Estilos del panel principal
```

## Flujo de la aplicación

1. **Login** → Pantalla de inicio de sesión
2. **MainDashboard** → Panel principal con opciones laterales (Usuarios, Foro, Eventos, Mensajes)

## Personalización

- **LoginController**: Modifica `handleLogin()` para integrar tu lógica de autenticación (API, base de datos, etc.)
- **login.css**: Ajusta colores y estilos en las variables al inicio del archivo
