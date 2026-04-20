# Список покупок — лабораторна (Android) студента ФІТ1-2м Ігнатова Ю.В

**Kotlin · Jetpack Compose · Room · Retrofit** (mock API через OkHttp Interceptor).  
Репозиторій — повний проєкт Android Studio (модуль `app`).

---

## 1. Який функціонал реалізовано?

| Область | Що зроблено |
|--------|-------------|
| **UI** | Compose + Material 3: список, введення назви/кількості, додавання, чіпи фільтра й сортування, синхронізація з App Bar |
| **Фільтр** | Усі / куплені / не куплені |
| **Сортування** | Назва (A–Я) · новіші за датою · куплені спочатку |
| **Пагінація** | Порції по 10 елементів + кнопка «Завантажити ще» (на відфільтрованому та відсортованому списку) |
| **Збереження** | **Room** (`shopping.db`), дані лишаються між запусками; `fallbackToDestructiveMigration()` при зміні схеми БД |
| **Мережа** | **Retrofit** + **mock JSON**; після sync — **upsert** у Room за `id` |
| **Дії з товарами** | Додати локально (UUID), позначити «куплено», **видалити** рядок (іконка кошика) |

**Лабораторна 3 (тестування):** таблиці тест-кейсів, шаблон ручного звіту та опис автотестів — у [`docs/TESTING.md`](docs/TESTING.md) та [`docs/MANUAL_TEST_REPORT.md`](docs/MANUAL_TEST_REPORT.md).

---

## 2. Додаткове доповнення

**Анімації**

- чекбокс «куплено» — плавна зміна масштабу;
- картка товару — `animateContentSize`;
- список — `AnimatedContent` (fade) при зміні фільтра або сортування.

---

## 3. З якими труднощами зіткнулися?

- **Порядок логіки:** фільтр → сортування → обрізка для пагінації; інакше змінюється видимий набір елементів.
- **Синхронізація:** однакові `id` з mock перезаписують запис у Room; локальні товари з власним UUID не конфліктують з `srv-*`.
- **Збірка / Gradle:** налаштування **Daemon JVM / toolchain** (плагін Foojay у `settings.gradle.kts`, auto-download JDK у `gradle.properties`).
- **Версії Compose:** API `animateItemPlacement` у поточній залежності було прибрано з коду; анімація списку залишена через `AnimatedContent`.

---

## Запуск

1. Android Studio → **Open** → корінь проєкту (де `settings.gradle.kts`).  
2. **Gradle Sync** → **Run** → модуль **`app`**.
З терміналу (Windows):

```bat
gradlew.bat assembleDebug
gradlew.bat assembleDebugAndroidTest
gradlew.bat connectedDebugAndroidTest
```

Остання команда потребує запущеного емулятора або пристрою (інструментовані тести).

---

## GitHub Actions (CI)

Workflow [`.github/workflows/test.yml`](.github/workflows/test.yml) запускається на **кожен `push` і `pull_request`**, а також **вручну** (`workflow_dispatch`): GitHub → **Actions** → **Android tests** → **Run workflow** → вибір гілки → **Run workflow**.

| Крок | Що робить |
|------|-----------|
| **Checkout** | Клонує репозиторій |
| **Python 3.12** | Налаштування середовища (вимога ЛР: Java + Python); версія виводиться в лог |
| **JDK 17** (Eclipse Temurin) | Збірка Kotlin/Android; кеш Gradle |
| **Android Emulator** | API 34, `x86_64`, Google APIs |
| **Gradle** | `./gradlew connectedDebugAndroidTest` — інструментовані тести ЛР3 |

**Де дивитися результат:** репозиторій на GitHub → **Actions** → останній запуск workflow **Android tests** → job **instrumented-tests** → розгорнути кроки; звіт про тести та помилки — у логах Gradle.

Якщо workflow червоний, перевірте лог кроку **Run instrumented tests on emulator** (там повний `--stacktrace` від Gradle).
