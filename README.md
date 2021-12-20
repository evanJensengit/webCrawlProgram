# CSS436
# Logic Platform
A rules based engine for managing interactions between platforms

## Setup
Install and setup your go environment. This project uses go 1.16.x so make sure to install the most recent version.
https://golang.org/doc/install

Make sure the project is cloned under your $GOPATH
```sh
git clone git@github.com:sirrus7/Logic-Platform.git $GOPATH/src/github.com/sirrus7/
```
## Running Locally
Run postgres:13.3 in a sperate terminal before starting the application as this will initialize a connection on start.
```sh
# Specific a mounting path where the postgres data will be stored, this will make it so the pg data does not have
# to be reseeded if the container dies or is restarted
docker run \
    --name logic-postgres \
    --rm \
    -p 5432:5432 \
    -e POSTGRES_USER=logic \
    -e POSTGRES_PASSWORD=logic \
    -v $HOME/logic/postgres:/var/lib/postgresql/data \
    -v $GOPATH/src/github.com/sirrus7/Logic-Platform/init-database.sh:/docker-entrypoint-initdb.d/init-database.sh \
    postgres:13.3
```

```sh
go run cmd/service/main.go --debug
```
After the service is running you can test connectivity on port 45231
```sh
curl -XGET http://localhost:45231/status
```
Utilize the UI function by cd to app directory then run "npm install"  
```sh
npm install #if you haven't already
npm start #starts up localhost:3000 which should open automatically
```
## Getting gitlab running locally
Follow the instructions here to get a gitlab instance running locally https://docs.gitlab.com/ee/install/docker.html

The APIs served
