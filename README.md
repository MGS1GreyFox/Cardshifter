Cardshifter
===========

Master branch: [![Build Status](https://travis-ci.org/Cardshifter/Cardshifter.svg?branch=master)](https://travis-ci.org/Cardshifter/Cardshifter?branch=master)  
Develop branch: [![Build Status](https://travis-ci.org/Cardshifter/Cardshifter.svg?branch=develop)](https://travis-ci.org/Cardshifter/Cardshifter?branch=develop)

Cardshifter Game

Developer guidelines
--------------------

In order to ease development on this project, we ask you to follow the following guidelines:
 - If a ```.travis.yml``` file is present in your branch, then the Travis CI build system will attempt to build your branch and run tests on it and accordingly notify you of success or failure.
 - Please respect the coding standards of other people in their own submodules.
 - Open issues early and often, especially if you do not have time for the project for a few days, then other people can pick up the issue and attempt to implement it.
 - Commit early and often, you are hopefully doing this locally already, but please also share your updates remotely as fast as possible, in this way others will know what you are working on and what the status is.

Coding style
------------

We would like everyone to follow the following guidelines:
 - Use the most common coding standard for the language you are coding in.
 - Use tabs for indentation in Java.
 - Use two spaces for indentation in Lua.

Platform
--------

Currently we plan to support version Java 8u20 and above.

Playing the game
----------------

To play the game, [download the latest release](https://github.com/Cardshifter/Cardshifter/releases). Start the client jar using `java -jar cardshifter-fx-<version>.jar`.

There is a server running at `dwarftowers.com` port `4242` that you may connect to.

Otherwise, you may run a server locally in the console, and connect to `127.0.0.1` port `4242` to play.

Develop another client
----------------------

If you want to help develop another client, [download the latest server release](https://github.com/Cardshifter/Cardshifter/releases). Start the server using `java -jar cardshifter-server-<version>.jar`.

All messages sent between client and server are using JSON format. The available messages can be found [here](https://github.com/Cardshifter/Cardshifter/tree/client-server/cardshifter-api/src/main/java/com/cardshifter/api).

For example, to send a [`LoginMessage`](https://github.com/Cardshifter/Cardshifter/blob/client-server/cardshifter-api/src/main/java/com/cardshifter/api/incoming/LoginMessage.java) pass the following JSON: `{ "command": "login", "username": "example" }`.

To send a request for starting a game, use [`StartGameRequest`](https://github.com/Cardshifter/Cardshifter/blob/client-server/cardshifter-api/src/main/java/com/cardshifter/api/incoming/StartGameRequest.java) which can be sent as JSON like this: `{ "command": "startgame", "opponent": 1, "gameType": "VANILLA" }` (currently "VANILLA" is the only supported game type).

Running cardshifter-server from within the IDE
----------------------------------------------

In order to run the cardshifter-server module from within your IDE, you will need to run it with JVM arguments `-Djava.security.manager -Djava.security.policy==server.policy`. For Netbeans, open up the Project Properties for the cardshifter-server project.  Then go to Run, and add the above JVM arguments to the VM Options box.

Running cardshifter-server standalone
-------------------------------------

In order to run the cardshifter server standalone, you will need to use the supplied JAR and policy file, suppose we are on version 0.4-SNAPSHOT, then you can start the server with the following command:
`java -jar -Djava.security.manager -D.java.security.policy==cardshifter-server-0.4-SNAPSHOT.policy cardshifter-server-0.4-SNAPSHOT.jar`

For secure execution, you must use a double equals sign in the -Djava.security.policy==x declaration and you must not edit the policy file.
