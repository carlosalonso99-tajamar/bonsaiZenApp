# Bonsai Zen App

Bonsai Zen es una aplicación para gestionar tus bonsáis de manera sencilla y eficiente. Desarrollada en Kotlin para Android, la aplicación utiliza una arquitectura MVVM, Fragmentos, y una base de datos local para almacenar y editar la información de tus bonsáis.

## Características

- **Lista de bonsáis**: Visualiza una lista de todos tus bonsáis registrados.
- **Detalle y edición**: Abre un fragmento al seleccionar un bonsái para editar su información.
- **Base de datos local**: Los datos de tus bonsáis se almacenan localmente.
- **Interfaz intuitiva**: Navegación fluida y diseño minimalista.
- **Sincronización en la nube**: Integración con Firebase para sincronizar los datos en múltiples dispositivos.

## Tecnologías utilizadas

- **Lenguaje**: Kotlin
- **Arquitectura**: MVVM
- **UI Components**: RecyclerView, Fragments
- **Base de datos**: Room
- **Inyección de dependencias**: Hilt
- **Backend**: Firebase (Firestore y Authentication)

## Instalación y configuración

1. Clona este repositorio:

   ```bash
   git clone https://github.com/saulhervas/bonsaiZenApp.git
   ```

2. Abre el proyecto en Android Studio.

3. Asegúrate de tener instaladas las siguientes dependencias:

   ```kotlin
   implementation "androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1"
   implementation "androidx.room:room-runtime:2.5.2"
   kapt "androidx.room:room-compiler:2.5.2"
   implementation "com.google.dagger:hilt-android:2.48"
   kapt "com.google.dagger:hilt-compiler:2.48"
   implementation "com.google.firebase:firebase-firestore-ktx:24.7.1"
   implementation "com.google.firebase:firebase-auth-ktx:22.1.0"
   ```

4. Configura tu entorno para usar Hilt agregando el plugin en tu archivo `build.gradle`:

   ```groovy
   plugins {
       id 'dagger.hilt.android.plugin'
   }
   ```

5. Configura Firebase:
   - Crea un proyecto en [Firebase Console](https://console.firebase.google.com/).
   - Agrega el archivo `google-services.json` a la carpeta `app/` de tu proyecto.
   - Habilita Firestore y Authentication en tu proyecto de Firebase.

6. Sincroniza el proyecto y ejecuta la aplicación en tu emulador o dispositivo físico.

## Estructura del proyecto

```plaintext
bonsaiZenApp/
├── app/
│   ├── data/                # Clases relacionadas con la base de datos y modelos
│   ├── di/                  # Módulos de Hilt para inyección de dependencias
│   ├── ui/                  # Fragmentos y componentes de la interfaz de usuario
│   ├── viewmodel/           # ViewModels
│   ├── firebase/            # Clases relacionadas con Firebase
│   └── utils/               # Utilidades y extensiones
└── build.gradle             # Configuración del proyecto
```

## Capturas de pantalla

*(Incluye capturas de pantalla de la aplicación para mostrar su diseño y funcionalidades)*

## Contribución

¡Las contribuciones son bienvenidas! Si deseas mejorar esta aplicación:

1. Haz un fork del repositorio.
2. Crea una nueva rama para tu funcionalidad o corrección.
3. Envía un pull request detallando tus cambios.

## Licencia

Este proyecto está licenciado bajo la Licencia MIT. Consulta el archivo [LICENSE](LICENSE) para más detalles.

## Autor

Desarrollado por **Saúl Hervás**.
