![image](https://github.com/user-attachments/assets/2da5c144-f79e-45aa-a745-9eb193536a4c)


# OTP Service

Backend-приложение для генерации и проверки временных кодов (OTP) с поддержкой многоканальной рассылки

## Функциональность

- ✅ Регистрация и аутентификация пользователей (JWT)
- ✅ Генерация OTP-кодов с настраиваемыми параметрами:
  - Длина кода (6-10 символов)
  - Время жизни (1-60 минут)
- ✅ Проверка валидности кодов (ACTIVE/EXPIRED/USED)
- ✅ Рассылка кодов через:
  - SMS (через SMPP протокол)
  - Email (SMTP)
  - Telegram бота
  - Сохранение в файл
- ✅ Административный интерфейс:
  - Управление пользователями
  - Настройка параметров OTP
- ⚙️ Автоматическая пометка просроченных кодов
- 📊 Логирование всех операций

## Структура проекта
otp-service/

├── src/

│ ├── main/

│ │ ├── java/

│ │ │ └── example/

│ │ │ ├── config/ # Конфигурация БД

│ │ │ ├── controller/ # Обработчики HTTP-запросов

│ │ │ ├── dao/ # Data Access Object

│ │ │ ├── model/ # Сущности БД

│ │ │ ├── service/ # Бизнес-логика

│ │ │ └── util/ # Утилиты (JWT)

│ │ └── resources/ # Конфигурационные файлы

│ └── test/ # Тесты

├── pom.xml # Maven конфигурация

└── README.md

## Требования

- Java 11+
- Maven 3.8+
- PostgreSQL 14+
- Аккаунты в сервисах рассылки (опционально)

## Установка и запуск
Создать БД PostgreSQL:
CREATE DATABASE otp_service;
1. Клонировать репозиторий:
git clone https://github.com/Zhidkov-Aleksandr/otp-service.git 
2. Создать БД PostgreSQL: CREATE DATABASE otp_service;
3. Настроить подключение к БД в src/main/resources/database.properties
4. Собрать проект: mvn clean package

## Примеры API-запросов
Регистрация пользователя:
curl -X POST http://localhost:8080/register \
  -H "Content-Type: application/json" \
  -d '{"username":"user1", "password":"pass123"}'

Авторизация:
curl -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user1", "password":"pass123"}'

Генерация OTP:
curl -X POST http://localhost:8080/otp/generate \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"operationId":"transaction-123", "channel":"email", "destination":"user@example.com"}'

Проверка OTP:
curl -X POST http://localhost:8080/otp/validate \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"operationId":"transaction-123", "code":"123456"}'

Административные функции (только для admin):
# Получить список пользователей
curl -H "Authorization: Bearer <ADMIN_JWT>" http://localhost:8080/admin/users

# Изменить настройки OTP
curl -X PUT http://localhost:8080/admin/config \
  -H "Authorization: Bearer <ADMIN_JWT>" \
  -H "Content-Type: application/json" \
  -d '{"codeLength":8, "expirationMinutes":5}'

## Конфигурация
Файлы настроек в src/main/resources:
database.properties - настройки PostgreSQL
email.properties - параметры SMTP
sms.properties - данные SMPP-сервера
telegram.properties - настройки Telegram бота






