# README

Simple Spring Boot application with a few http rest endpoints secured by basic http authentication.

This app manages documents (files) and collections of documents called protocols.

There are a couple endpoints to manage documents (files)
- upload a file
- download a file
- edit file metadata such as name, user who created file, created time and document type (jpg)

There are endpoints to group documents into protocols. 
Each protocol must contain at least one document. 
And each document can belong to any number of protocols.

- create protocol - protocol must contain at least one document
- edit protocol - for example edit documents belonging to this protocol
- change only protocol's state. There are three states to chose from.


SQLite is used to persist both documents and protocols.

HTTP basic authentication with InMemory static user management.

# Install & Run
Manually running inside Tomcat application server:

1. Configure sqlite jdbc string inside application.properties.
`spring.datasource.url=jdbc:sqlite:<ABSOLUTE PATH TO sqlite3 database file>`
2. Generate `war` file using `maven`. For example inside root folder execute `./mvnw package`. (requires Maven and Java 11)
3. Move generated `war` inside Tomcat container `/webapps` folder.
4. Start tomcat
5. Api is accessible using `user` and `password` credentials.



