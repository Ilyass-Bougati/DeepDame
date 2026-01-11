# DeepDame

This project uses a Maven Multi-Module Architecture.

The logic is split into two parts:
1. `game-engine` - Core game logic
2. `DeepDame` (Backend) - Spring Boot server that uses the engine

## ⚠️ Important: Folder Structure

The folder `server` is the Root Maven Project. You must execute commands from here, not from inside the subfolder if you're working on the backend.

```
server/                  <-- RUN COMMANDS HERE
├── pom.xml              <-- Parent POM
├── game-engine/         <-- Module: Logic
└── DeepDame/            <-- Module: Spring Boot Backend
```

## How to Run the Project

### Option 1: Via Terminal (Recommended)

**Step 1: Build & Install Modules**

You must install the game engine first so the backend can find it. Run this from the `server` folder:

```bash
mvn clean install -DskipTests
```

**Step 2: Generate RSA Keys (Required)**

The backend requires RSA keys for security (JWT / encryption). the following commands must be executed from the `/server/DeepDame/src/main/resources` directory:
```bash
mkdir -p certs
cd certs
openssl genpkey -algorithm RSA -out private.pem -pkeyopt rsa_keygen_bits:2048
openssl rsa -pubout -in private.pem -out public.pem
```
Do NOT commit private_key.pem to version control.

**Step 3: Run the Spring Boot Server**

Now you can start the server:

```bash
mvn spring-boot:run
```

### Option 2: Via IntelliJ IDEA

If the project looks red or classes are missing, IntelliJ might not recognize the new structure.

1. **Open the Project**: Open the `server` directory in IntelliJ.
2. **Open Maven Plugin**: Open the Maven sidebar on the right side.
3. **Add Maven Project**: Click the (+) "Add Maven Project" button.
4. **Select Server**: Navigate to `server` and press OK.


## Documentations
For the **RESTful** APIs you can use Swagger to sift through the route, that's on `/swagger-ui/index.html`

# K8s
There are 2 method to deploy this backend, either mannualy or using the using the deployment script

### Deployment script
The deployment script was first meant to be using by the CD. However, I it can be used to deploy this application using minikube (it doesn't support any other tool currently)
```bash
# --observability : adds loki, grafana, and prometheus and integrates loki with the application
# --ai : deploys llama3.2 model
# --no-pull : make sure the application neither pulls nor clone the repo.
./deploy.k8s.sh --observability --ai
```
before deploying the application using k8s. You'll need to build the docker image, while on the `/server` directory run the ccommand
```bash
eval $(minikube docker-env)
docker build -t deepdame .
```
Then to run the project
```bash
kubectl apply -R -f k8s/
```