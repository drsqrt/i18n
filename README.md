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
git clone https://github.com/drsqrt/i18n.git
cd i18n/bin
sudo chmod 755 run.sh
./run.sh
```

### 3. Access LibreTranslate
- Open [http://localhost:5051](http://localhost:5051) in your browser.

### 4. Test API with cURL
```sh
curl -X GET http://localhost:5051/languages

curl -X POST "http://localhost:5051/translate" -H "Content-Type: application/json" \
-d '{"q": "Hello", "source": "en", "target": "hi"}'
```

### 5. Safely stop docker 
```sh
docker stop $(docker ps -q)
```

### 6. Miscellaneous
```sh
docker pull libretranslate/libretranslate
#remove volume lt-local if cached the data
docker volume rm lt-local

docker run -ti --rm \
  -p 5051:5000 \
  -v lt-local:/home/libretranslate/.local \
  -e LT_LOAD_ONLY="en,hi,es" \
  libretranslate/libretranslate

#go inside docker container
docker exec -it <container_id> sh
```



