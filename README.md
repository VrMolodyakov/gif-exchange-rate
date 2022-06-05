# gif-exchange-rate

## Описание

Создать сервис, который обращается к сервису курсов валют, и отображает gif:
* если курс по отношению к USD за сегодня стал выше вчерашнего, то отдаем рандомную отсюда https://giphy.com/search/rich
* если ниже - отсюда https://giphy.com/search/broke
### Ссылки
* REST API курсов валют - https://docs.openexchangerates.org/
* REST API гифок - https://developers.giphy.com/docs/api#quick-start-guide
### Must Have
* Сервис на Spring Boot 2 + Java / Kotlin
* Запросы приходят на HTTP endpoint (должен быть написан в соответствии с rest conventions), туда передается код валюты по отношению с которой сравнивается USD
* Для взаимодействия с внешними сервисами используется Feign
* Все параметры (валюта по отношению к которой смотрится курс, адреса внешних сервисов и т.д.) вынесены в настройки
* На сервис написаны тесты (для мока внешних сервисов можно использовать @mockbean или WireMock)
* Для сборки должен использоваться Gradle
* Результатом выполнения должен быть репо на GitHub с инструкцией по запуску
### Nice to Have
* Сборка и запуск Docker контейнера с этим сервисом

## Подготовка

Перед запустком в файле свойст можно задать код валюты, по отношению к которой проверяется курс (USD по умолчанию), но сначала нужно улучшить ключ доступа. https://openexchangerates.org/account/subscription

###### файл свойств : \src\main\resources\application.yaml 

## Endpoint
```
GET  /currencies/{ code }
```
##### Example :  http://localhost:8080/currencies/BOB
В зависимости от ответа будет показано gif-изображение с ключевым словом rich/broke или "i don't know" если значения равны. 
Ключевые слова для ответа также можно изменить в файле свойств.

Код валюты можно выбрать по ссылке : https://docs.openexchangerates.org/docs/supported-currencies

## Инструкция по запуску

### С помощью Gradle
```
git clone https://github.com/VrMolodyakov/gif-exchange-rate.git
cd gif-exchange-rate
gradle clean
gradlew bootRun
```

### С помощью Gradle и jar 
```
git clone https://github.com/VrMolodyakov/gif-exchange-rate.git
cd gif-exchange-rate
gradle clean
gradle build
cd build/libs
java -jar gifAndExchangeRate-0.0.1-SNAPSHOT.jar
```
### С помощью Docker

```
git clone https://github.com/VrMolodyakov/gif-exchange-rate.git
cd gif-exchange-rate
gradle clean
gradle build
docker build -t gif-exchange-image:1 .
docker run -p 8080:8080 gif-exchange-image:1
```
