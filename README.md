# Album API

## How to run locally

### With Maven CLI
 Type in Terminal
```
  mvn spring-boot:run
```
### With IntelliJ
- You can run the project locally with IntelliJ

## CRUD Operations
#### GET
- use GET */api/v1/album* to list all albums in stock
- use GET */api/v1/album/{id}* to get specific album by id
- use GET */api/v1/album?artist={artist}* to list albums by specific artist
- use GET */api/v1/album?releasedYear={year}* to list albums in specific year
- use GET */api/v1/album?genre={genre}* to list albums by specific genre
- use GET */api/v1/album?inStock=true* to list albums in stock
- use GET */api/v1/album?genre={genre}* to get album information of a specific album name
#### POST
- use POST */api/v1/album* to add an album
#### PUT
- use PUT */api/v1/album/{id}* to update specific album's details
#### DELETE
- use DELETE */api/v1/album/{id}* to delete specific album



## Running test
todo

## About this API
This API is a CRUD API for Albums, using Postgresql as the Database.
