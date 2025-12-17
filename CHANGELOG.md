# CHANGELOG
Todas las modificaciones importantes de este proyecto se documentarán en este archivo.

## [Unreleased]

## [0.0.1] - 26/05/2025

### Added
- Configuración inicial del proyecto con LibGDX Liftoff.
- Creación del README, el cual contiene **nombre** y **descripción** del proyecto, sus **integrantes**, las **tecnologías** empleadas, instrucciones de **compilación y ejecución** del programa, y el **estado actual** del mismo.
- Creación del archivo CHANGELOG, el cual se espera que sirva como un registro evolutivo de las modificaciones del proyecto.
- Creación de la Wiki.
- Creación del .gitignore.

## [0.0.2] - 02/06/2025
### Added
- Link directo desde el Home de la Wiki a la página de Propuesta del Proyecto.
### Changed
- Cambio de nombre del apartado "Tecnologías" del README.md a "Tecnologías y plataformas objetivo".
### Fixed
- Corregidas las instrucciones del README.md para la compilación y ejecución del proyecto.

## [0.1.0] - 03/08/2025
### Added
- Primera versión jugable (vertical slice).
- Sistema de movimiento lateral básico (izquierda/derecha).
- Sistema de movimiento vertical básico (salto).
- Colisiones físicas con plataformas del mapa.
- Manejo de entradas por teclado (WASD) para control de personaje.
- Estructura base de clases `Personaje`, `TipoPersonaje` y `Estadistica`.
- Sistema de dibujado desde clase `Personaje`.
- Animaciones de `idle`, `correr` y `saltar`.
- Soporte para spritesheet con `TextureAtlas`.
- Carga de sprites recortados desde atlas `.atlas` exportado con TexturePacker.
- Manejo de pantallas `introducción`, `menú`, `juego`.
- HUD con información básica durante la pantalla de juego.
- Opción de pausado del juego (Esc).
- Implementación de cámaras para las distintas capas de la pantalla de juego.
- Uso de viewports para poder ajustar el tamaño de la ventana.

### Changed
- Se actualizó la sección de "Estado Actual del Proyecto" en el README.md.
