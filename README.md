# 📅 DateTime

[![](https://jitpack.io/v/LeandroLCD/DateTime.svg)](https://jitpack.io/#LeandroLCD/DateTime)
![GitHub release (latest by date)](https://img.shields.io/github/v/release/LeandroLCD/DateTime)
![GitHub last commit](https://img.shields.io/github/last-commit/LeandroLCD/DateTime)
![GitHub issues](https://img.shields.io/github/issues/LeandroLCD/DateTime)
[![Android CI](https://github.com/LeandroLCD/DateTime/actions/workflows/android-ci.yml/badge.svg)](https://github.com/LeandroLCD/DateTime/actions/workflows/android-ci.yml)



Una librería ligera para trabajar con fechas y tiempos en **Android**.  
Proporciona una API sencilla para manipular, formatear y comparar fechas de manera segura.

## 🚀 Instalación

### 1. Agregar JitPack en `settings.gradle.kts`

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}
```

### 2. Usar en tu `libs.versions.toml`

```toml
[versions]
dateTime = "1.0.0"

[libraries]
dateTime = { module = "com.github.LeandroLCD:DateTime", version.ref = "dateTime" }
```

### 3. Agregar dependencias en `build.gradle.kts`

```kotlin
dependencies {
    implementation(libs.dateTime)
}
```

### 4. Inicializar en tu `Application`

```kotlin
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        DateTime.init(this) //Deprecado en versiones recientes ya no se requiere inicialización.
    }
}
```

---

## ✨ Características

- Crear fechas desde **string**, **milisegundos** o la fecha/hora actual.
- Conversión entre **LocalDateTime**, **ZonedDateTime** y **millis**.
- Sumar o restar **días, meses, años, minutos, segundos**.
- Calcular diferencias entre fechas con `TimeSpan`.
- Formateo flexible mediante patrones o tipos predefinidos.
- Validaciones seguras para días y meses inválidos.
- Builder simple para inicializar fechas personalizadas.

---

## 📚 Uso

### Crear fechas

```kotlin
// Fecha actual con zona horaria del sistema
val now = DateTime.now()

// Fecha actual en una zona horaria específica
val nowInUtc = DateTime.now("UTC")

// Desde string (intenta con varios patrones comunes)
val fromString = DateTime.fromString("2023-12-25")

// Desde milisegundos
val fromMillis = DateTime.fromMillis(System.currentTimeMillis())
```

### Formatear fechas

```kotlin
val date = DateTime.now()

// Usando tipo Large
val formattedLarge = date.format(FormatType.Large(delimiter = "-"))
// -> 04-10-2025 14:30:00 Europe/Madrid

// Usando tipo Short
val formattedShort = date.format(FormatType.Short(delimiter = "/"))
// -> 04/10/2025

// Usando patrón personalizado
val custom = date.format("dd MMM yyyy HH:mm:ss")
// -> 04 Oct 2025 14:30:00
```

### Operaciones con fechas

```kotlin
val date = DateTime.now()

val nextWeek = date.addDays(7)
val nextMonth = date.addMonths(1)
val nextYear = date.addYears(1)

val firstDay = date.firstDayOfMonth()
val lastDay = date.lastDayOfMonth()
```

### Diferencias entre fechas

```kotlin
val start = DateTime.fromString("2020-01-01")
val end = DateTime.fromString("2023-05-15")

val diff = end.timeSpan(start)
// diff -> TimeSpan(years=3, months=4, days=14, hours=0, minutes=0, seconds=0)
```

### Conversión

```kotlin
val millis = date.toMillis()
val utcMillis = date.toMillisUTC()

val ldt = date.toLocalDateTime()
val zdt = date.toZonedDateTime()
```

### Usando el Builder

```kotlin
val date = DateTime.Builder()
    .setYear(2025)
    .setMonth(12)
    .setDay(31)
    .build()
```

---

## ⚠️ Excepciones

La librería lanza `InvalidFormatException` cuando el string o zona horaria no son válidos:

```kotlin
try {
    val date = DateTime.fromString("invalid-date")
} catch (e: InvalidFormatException) {
    // manejar error
}
```

---

## 🛠️ Dependencias

- [DateTime](https://github.com/LeandroLCD/DateTime)

---

