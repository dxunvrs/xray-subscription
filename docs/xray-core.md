# Работа с конфигом xray-core
> P.S. Все будет через docker + docker compose
## 1. Генерим ключи
Private + Public keys
```
docker run --rm ghcr.io/xtls/xray-core:latest x25519
```
UUID для первого клиента
```
docker run --rm ghcr.io/xtls/xray-core:latest uuid 
```
Short ID для первого клиента
```
openssl rand -hex 8
```

## 2. Пример конфига config/config.json
[Пример](https://github.com/dxunvrs/xray-subscription/tree/master/config/simple_config_example.json) конфига, там уже расписано, что и куда подставлять

## 3. Docker compose файл для xray-core
``` 
services:
    xray:
        image: ghcr.io/xtls/xray-core:latest
        container_name: xray-core
        restart: always
        network_mode: "host"
        volumes:
            - ./config/config.json:/usr/local/etc/xray/config.json:ro
```

## 4. Поднимаем
``` 
docker compose up -d
```

## 5. VLESS-ключ
Формируется так:  
``` 
vless://CLIENT_UUID@SERVER_IP:PORT?type=tcp&security=reality&pbk=PUBLIC_KEY&fp=BROWSER&sni=SNI&sid=SHORT_ID&flow=xtls-rprx-vision#NAME
```
- CLIENT_UUID, PUBLIC_KEY, SHORT_ID - генерировали ранее
- SERVER_IP - IP вашего сервера
- PORT - указан в config_example.json - 443, можно изменить
- SNI - указан в config_example.json - apple.com, можно изменить
- NAME - любое имя, будет видно на клиенте