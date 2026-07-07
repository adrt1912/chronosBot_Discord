# ⏳ Chronos Bot

Chronos es un bot de Discord multifuncional, robusto y totalmente modular programado en **Java** utilizando la biblioteca **JDA (Java Discord API) 5** y **SQLite** como motor de persistencia de datos. El bot integra sistemas avanzados de gestión de eventos en vivo, economía virtual, juegos de casino, moderación y un sistema automatizado de niveles y experiencia (XP).

El proyecto ha sido diseñado bajo estándares profesionales de arquitectura de software, priorizando la modularidad, la separación de responsabilidades y la automatización mediante reflexión.

---

## 🚀 Características Principales

El bot se divide en módulos independientes bien definidos:

### 📅 1. Gestión de Eventos y Notificaciones (`/Notificaciones`)
*   **Creación Interactiva:** `/crear-notificacion` permite desplegar eventos con formato de **Lista de Asistencia** (votos Sí/No) o **Encuestas Personalizadas** multitarea.
*   **Actualizaciones en Vivo:** Las tarjetas dinámicas se editan en tiempo real mediante interacción con botones, procesadas de forma asíncrona sin recargar el chat.
*   **Agenda Personal y Global:** Los usuarios pueden consultar el `/calendario` del servidor de forma paginada o revisar su lista personal mediante `/mis-eventos`.
*   **Automatización:** Incluye un `PlanificadorAlarmas` en segundo plano que avisa al canal configurado mediante `/configuracion` cuando los eventos están por comenzar, además de un sistema de `/recordatorio` personales con tiempo relativo (`45m`, `2h`, `1d`).

### 💰 2. Economía y Tienda (`/Economia` & `/Tienda`)
*   **Monedero Virtual:** `/balance` permite consultar fondos y `/transferir` realiza transacciones seguras entre usuarios directos de la base de datos.
*   **Generación de Ingresos:** Comando `/trabajar` protegido por cooldowns temporales para evitar abusos inflacionarios.
*   **Comercio de Roles:** Una `/tienda` de roles configurados directamente desde la base de datos donde los usuarios usan `/comprar` para adquirir rangos cosméticos automáticos.

### 🎰 3. Casino y Apuestas (`/Casino`)
*   **Sistemas de Azar:** Minijuegos matemáticos integrados como `/dados`, `/tragaperras` con multiplicadores de símbolos y `/ruleta` con apuestas a colores dinámicos (🔴 Rojo, ⚫ Negro, 🟢 Verde).

### 📈 4. Sistema de Niveles y Experiencia (`/Experiencia`)
*   **Escucha Pasiva Inteligente:** `ManejadorXP` intercepta la actividad del chat calculando de 15 a 25 puntos de experiencia por mensaje, regulado por un cooldown silencioso de 60 segundos por usuario para mitigar el spam.
*   **Visualización de Progreso:** El comando `/rank` genera una tarjeta textual que calcula dinámicamente el progreso y renderiza una **barra de progreso animada con caracteres** (`▰▰▰▱▱`).
*   **Roles de Recompensa Automáticos:** Al alcanzar ciertos hitos de nivel (5, 10, 20), el bot inyecta de forma autónoma roles honoríficos en el perfil del usuario.
*   **Competición:** Comando `/topxp` que muestra de mayor a menor el Top 10 de usuarios más activos del servidor.

### 🛡️ 5. Moderación y Utilidades (`/Moderacion` & `/Utilidad`)
*   **Saneamiento de Chat:** Comando `/clean` para purgar de forma masiva hasta 100 mensajes de texto de un canal.
*   **Sistema de Advertencias:** `/warn` registra amonestaciones indexadas por servidor en la BD, contando los avisos acumulados por usuario.
*   **Panel de Ayuda Autogestionado:** El comando `/ayuda` es 100% dinámico. Utiliza **Reflexión de Java** para escanear el mapa central de comandos del bot, clasificándolos automáticamente por el paquete/carpeta al que pertenecen y extrayendo sus descripciones en tiempo real.

---

## 🗂️ Arquitectura del Proyecto

El código fuente sigue una estructura estricta de paquetes que independiza las características de la aplicación:

```text
src/main/java/aperez578/
│
├── Main.java                        # Punto de entrada y arranque del bot
├── ConexionBD.java                  # Inicializador del Core SQLite (Singleton)
├── Comando.java                     # Interfaz común para el Patrón Command
├── ContextoComando.java             # Wrapper de unificación (SlashEvents / Buttons)
├── LectorDeComandos.java            # Despachador de eventos y mapa central
├── ManejadorSlash.java              # Enrutador de eventos e interacciones de Discord
│
├── 💰 Economia/
│   ├── EconomiaBD.java              # Subcapa DAO para transacciones monetarias
│   └── Comandos/                    # /balance, /trabajar, /robar, /top, /transferir
│
├── 🎰 Casino/
│   └── Comandos/                    # /ruleta, /dados, /tragaperras
│
├── 🛒 Tienda/
│   └── Comandos/                    # /tienda, /comprar
│
├── 📈 Experiencia/
│   ├── ManejadorXP.java             # Listener de chat y asignador de roles por nivel
│   ├── Experiencia.java             # Subcapa DAO para control de XP y rankings
│   └── Comandos/                    # /rank, /topxp
│
├── 🛡️ Moderacion/
│   ├── UtilidadBD.java              # Subcapa DAO para registro de amonestaciones
│   └── Comandos/                    # /clean, /warn
│
└── 🔔 Notificaciones/
    ├── PlanificadorAlarmas.java     # Hilo temporizador de alertas de agenda
    ├── NotificacionesBD.java        # Subcapa DAO para eventos, asistencia y recordatorios
    └── Comandos/                    # /crear-notificacion, /calendario, BotonesEventos...