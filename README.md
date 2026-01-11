# DeepDame
![Flutter](https://img.shields.io/badge/Flutter-%2302569B.svg?style=for-the-badge&logo=Flutter&logoColor=white)
![spring-boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![postgresql](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![redis](https://img.shields.io/badge/redis-%23DD0031.svg?&style=for-the-badge&logo=redis&logoColor=white)
![docker](https://img.shields.io/badge/Docker-2CA5E0?style=for-the-badge&logo=docker&logoColor=white)
![Ollama](https://img.shields.io/badge/ollama-%23000000.svg?style=for-the-badge&logo=ollama&logoColor=white)

This is a checker game application made by FSTS students for JEE module. You can find the APK in the releases. This project was contributed to and maintained by

<p align="center">
  <a href="https://github.com/Ilyass-Bougati/SkyBooker/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=Ilyass-Bougati/DeepDame" />
</a>
</p>

### About This Application
This is a comprehensive checkers platform featuring **friend invites**, **real-time matches against friends**, **single-player AI modes**, and **random matchmaking**.

### Technical Architecture
This application leverages **WebSockets** over the **STOMP protocol** to handle real-time messaging and game state synchronization. We implemented the architecture below:

![img](/imgs/archi.png)

To ensure performance and scalability, we utilized a polyglot persistence strategy:

* **PostgreSQL:** Serves as the primary relational store for structured data like user profiles, relationships, and authentication.
* **MongoDB:** Handles unstructured data, specifically for archiving game history and match logs. This allows for flexible storage of game moves without rigid schema constraints.
* **Redis:** Employed for aggressive caching and session management. By offloading state from the application server's memory to Redis, we ensure the system is stateless, allowing for seamless **horizontal scaling** in the future. And we're using Redis pub/sub to handle events on multiple instances of the application.