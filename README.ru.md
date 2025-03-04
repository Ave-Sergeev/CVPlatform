# CVPlatform

Сервис-шаблон для тестирования технологий, и дальнейшего использования в MVP.
На данный момент основная логика заменена на простой CRUD (профиль пользователя).

В проекте используются:
1) Аутентификация с помощью KeyCloak.
2) Работа с PostgreSQL через Quill.
3) Миграции осуществляются через Liquibase.
4) Работа с конфигурацией (ZIO-Config).
5) Конечные точки HTTP и gPRC (ZIO-HTTP, ZIO-gRPC).
6) Ведение логов (ZIO-Logging + SLF4J2).
7) Работа с Redis (ZIO-Redis).
8) Обработчики для RPC и REST (с аннотацией трассировки и подсчетом метрик) (ZIO-Aspect).
9) Сбор метрик для Prometheus (ZIO-Metrics).
10) Использование ULID в качестве более продвинутого и совместимого аналога UUID.
11) И другие технологии...

### Локальное развертывание

Чтобы развернуть проект локально, вам нужно:
1) Перейти в директорию `/docker`.
2) Добавить свой KeyCloak в файл `docker-compose.yml` (по умолчанию он не настроен).
3) Запустить контейнеры зависимых сервисов с помощью команды `docker compose up`.
4) Переопределить переменные окружения для PostgreSQL, Keycloak и Redis.
5) Запустить проект из файла `Main.scala`.

### API
...

Уважаемый!
Если вам что-то приглянулось в данном проекте, сочли его полезным, или просто понравился код - не стесняйся поставить ⭐ звездочку в благодарность.
