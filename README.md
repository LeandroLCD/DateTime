# DateTime Library

## 📌 Descripción
**DateTime** es una librería en Kotlin diseñada para simplificar la manipulación de fechas y horas en aplicaciones Android. Utiliza **ThreeTenABP** como base para proporcionar una API moderna y eficiente compatible con Android.

## 🚀 Características
- Fácil inicialización y uso.
- Soporte para zonas horarias.
- Conversión y formateo de fechas.
- Cálculo de diferencias entre fechas.
- Soporte para formatos fecha corta y larga con separador personalizado.
- Soporte para formato personalizado

## 📦 Instalación
### 1️⃣ Agregar la dependencia
Asegúrate de incluir la librería en tu `libs.versions.toml` (para Version Catalog):

```toml
[versions]
dateTime = "1.1.3"
threetenabp = "1.4.4"
[libraries]
datetime = { group = "com.blipblipcode.DateTime", name = "Library", version = "dateTime" }
threetenabp = { module = "com.jakewharton.threetenabp:threetenabp", version.ref = "threetenabp" }
```

Y en tu módulo de aplicación (`build.gradle.kts`):

```kotlin
dependencies {
    implementation(libs.datetime)
    implementation(libs.threetenabp)
}
```

### 2️⃣ Inicialización en la aplicación
Antes de usar la librería, inicialízala en la clase `Application`, o en tu actividad principal:

```kotlin
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        DateTime.init(this) // 🔥 Inicialización obligatoria y necesaria para capturar el uso horario del dispositivo
    }
}
```

## 📖 Uso
### ✅ Obtener la fecha y hora actual
```kotlin
val now = DateTime.now()
println("Fecha actual: \$now")
```

### 📅 Formatear una fecha
```kotlin
val formattedDate = now.format("yyyy-MM-dd HH:mm:ss")
println("Fecha formateada: \$formattedDate")
```
```kotlin
val formattedDate = now.format(FormatType.Short('/'))
println("Fecha formateada: \$formattedDate")
```

### ⏳ Calcular la diferencia entre dos fechas
```kotlin
val startDate = DateTime.fromString("2024-01-01")
val endDate = DateTime.fromString("2024-02-01")
val daysBetween = startDate.timeSpan(endDate).totalDays()
println("Días entre fechas: \$daysBetween")
```
## 🚀 Compatibilidad
Se puede usar desde el API 24 🔥

## 🤝 Contribuciones
Las contribuciones son bienvenidas. Si encuentras un error o deseas agregar una nueva funcionalidad, abre un **issue** o un **pull request**.

---
¡Gracias por usar **DateTime**! 🚀

