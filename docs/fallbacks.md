## Fallbacks
443 порт удобен для nginx, поэтому научим дружить xray и npm на одном порту  
Сначала будет стоять xray-core, если это не его подключение, то трафик пойдет на nginx  
1. ```docker-compose.yml``` NPM:
```
services:
  app:
    image: 'jc21/nginx-proxy-manager:latest'
    restart: unless-stopped
    ports:
      - '80:80'         # HTTP (нужен для генерации Let's Encrypt сертификатов)
      - '81:81'         # Админ-панель NPM
      - '127.0.0.1:8443:443'      # HTTPS на нестандартном порту вместо 443
    networks:
        - npm_default
    volumes:
      - ./data:/data
      - ./letsencrypt:/etc/letsencrypt

networks:
    npm_default:
        external: true

```
2. Теперь сделаем fallback на на 127.0.0.1:8443 в ```config.json```:
```
...
"streamSettings": {
        "network": "tcp",
        "security": "reality",
        "realitySettings": {
          "show": false,
          "dest": "127.0.0.1:8443",
          "xver": 0,
          "serverNames": [
            "YOUR_DOMAIN"
          ],
          "privateKey": "oN6IGadDcqrxZ1cKbo7Uwg3DznPPlQDJtU75S4b8LVI",
          "shortIds": [
            "a5699637b319eb6d"
          ]
        }
      },
...
```
3. Готово

## Примечание
У вас должен быть свой домен, затем в npm нужно сделать ему запись и выдать сертификат  
Порядок действий такой: получили домен -> подняли npm и сделали запись на этот домен (например на какой-нибудь свой сайт) -> затем ставим xray перед nginx

## Примечание 2
Здесь fallback идет на локалхост, соотвественно это работает при ```network_mode: host```, если вы пользуетесь докер-сетью, то fallback должен быть на ```172.17.0.1```