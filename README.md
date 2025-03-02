# i18n using [LibreTranslate](https://github.com/LibreTranslate/LibreTranslate)

## LibreTranslate Setup with Colima & Docker

## Prerequisites
- Install [Colima](https://github.com/abiosoft/colima)
- Install [Docker](https://www.docker.com/)

## Setup & Run

### 1. Start Colima
```sh
colima start
```

### 2. Pull LibreTranslate Docker Image and Run it on $LT_PORT
```sh
docker pull libretranslate/libretranslate

#remove volume lt-local if cached the data
docker volume rm lt-local

#go inside docker container
docker exec -it <container_id> sh

or 

git clone https://github.com/drsqrt/i18n.git
cd i18n/bin
sudo chmod 755 run.sh
./run.sh
```

### 3. Access LibreTranslate
- Open [http://localhost:5050](http://localhost:5050) in your browser.

### 4. Test API with cURL
```sh
curl -X GET http://localhost:5050/languages

curl -X POST "http://localhost:5050/translate" -H "Content-Type: application/json" \
-d '{"q": "Hello", "source": "en", "target": "hi"}'
```



