# Xray Subscription
## Описание
API для работы с [xray-core](https://github.com/XTLS/Xray-core/tree/main). Запросы к ядру через gRPC.  
Небольшой [мануал](https://github.com/dxunvrs/xray-subscription/tree/master/docs/xray-core.md) по работе с конфигом xray-core.

## Быстрый старт
### 1. Необходимые ключи
Из [этого](https://github.com/dxunvrs/xray-subscription/tree/master/docs/xray-core.md) гайда нужны сгенерированные ```Public Key```, ```Private Key``` и ```Short ID```.

### 2. Переменные окружения
В корне проекта создайте ```.env``` файл и заполните его, как в ```env.example```
```
XRAY_SERVER_IP=127.0.0.1 - айпи сервера, который будет проксировать трафик
XRAY_SERVER_PORT=443 - порт (лучше всего 443)
XRAY_REALITY_PUBLIC_KEY=pub_key - сгенерированный публичный ключ
XRAY_REALITY_SHORT_ID=short_id - сгенерированный SHORT ID
XRAY_REALITY_SNI=apple.com - маскировка
XRAY_REALITY_FINGERPRINT=firefox - любой браузер
ADMIN_USERNAME=admin - имя админа
ADMIN_PASSWORD=1234 - пароль админа

XRAY_GRPC_HOST=xray-core - имя докер-контейнера с ядром xray
```

### 3. Конфиг
В папке config создайте ```config.json```, шаблон для конфига ```api_config_example.json```, там необходимо изменить ```PRIVATE_KEY```, ```SHORT_ID``` и ```SNI```

### 4. Запуск
```
docker compose up -d
```

### 5. Первый ключ
Добавьте пользователя через ```POST localhost:12258/api/admin/users```, тело запроса: 
```JSON
{
  "email": "test"
}
```
Получите ключ ```GET localhost:12258/sub/test```, либо вставьте эту ссылку в впн-клиент

## Админские эндпоинты (нужен пароль)
### 1. Добавление пользователя
- URL: ```/api/admin/users```
- Method: ```POST```
- Body:
```JSON
{
  "email": "user"
} 
```
- Response:
```JSON
{
  "id": 1,
  "email": "user",
  "uuid": "uuid"
}
```

### 2. Удаление пользователя
- URL: ```/api/admin/users/{email}```
- Method: ```DELETE```

### 3. Получение пользователя
- URL: ```/api/admin/users/{email}```
- Method: ```GET```
- Response:
```JSON
{
  "id": 1,
  "email": "user",
  "uuid": "uuid"
}
```

### 4. Получение всех пользователей
- URL: ```/api/admin/users```
- Method: ```GET```

### 5. Получение статистики пользователя
- URL: ```/api/admin/users/{email}/traffic```
- Method: ```GET```
- Response:
```JSON
{
  "email": "user",
  "uplinkBytes": 0,
  "downlinkBytes": 0,
  "totalBytes": 0
}
```

## Публичный эндпоинт
### Получение подписки
- URL: ```/sub/{email}```
- Method: ```GET```
- Response:
```
vless://.......
```

## Технологический стек
- Java 21
- Spring Boot 4
- Spring Security, Spring gRPC
- SQLite
- Xray Core
- Docker & Docker Compose 