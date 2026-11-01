# ITMO University — Academic & Engineering Portfolio

<p align="center">
  <img src="https://img.shields.io/badge/ITMO-University-ec174f?style=for-the-badge&logo=itmo&logoColor=white" alt="ITMO University" />
  <img src="https://img.shields.io/badge/Degree-CS_%26_Software_Engineering-007acc?style=for-the-badge" alt="Degree" />
  <img src="https://img.shields.io/badge/Author-Georgy_Nosov-181717?style=for-the-badge&logo=github&logoColor=white" alt="Author" />
  <img src="https://img.shields.io/badge/License-MIT-green?style=for-the-badge" alt="License" />
</p>

Curated academic portfolio, laboratory implementations, system designs, and engineering artifacts developed during studies at the **Faculty of Software Engineering and Computer Systems (FSECS / ФИТиП)**, **ITMO University** (Saint Petersburg, Russia).

The repository reflects an engineering progression from foundational algorithms and hardware-software interfaces up to distributed high-load enterprise backends and deep neural network architectures.

---

## Curriculum Navigation

```
ITMO/
├── 1-course/        # Java Core, OOP, Networking NIO, Sockets, Software Engineering
├── 2-course/        # Web Systems (Servlets, JSF, Spring Boot REST & JWT)
├── 3-course/        # Testing & QA, Linux POSIX OS, Computer Networks, CV & Simulation
└── 4-course/        # High-Load Systems, Embedded STM32, InfoSec, ML & Deep Learning
```

---

### 1-st Course

| Discipline | Technology Stack | Scope & Key Topics |
| :--- | :--- | :--- |
| [**Programming (Java)**](./1-course/Programming) | `Java 17+`, `NIO Channels`, `PostgreSQL`, `JDBC`, `Concurrency` | OOP paradigms, custom reflection frameworks, CLI command handlers, multi-threaded client-server sockets with non-blocking I/O (`java.nio.channels`), user authentication with salted SHA-384 hashing, transactional persistence in PostgreSQL. |
| [**Software Engineering**](./1-course/Software-Engineering) | `Java`, `Maven`, `Ant`, `Git` | Lifecycle management, automated build scripts, dependency graphs, source control workflows, and artifact packaging. |

---

### 2-nd Course

| Discipline | Technology Stack | Scope & Key Topics |
| :--- | :--- | :--- |
| [**Web Development**](./2-course/Web-Development) | `Java Servlets`, `JSP`, `JSF 2.x`, `Spring Boot`, `Hibernate/JPA`, `JWT`, `PostgreSQL` | Evolution of Java enterprise web services: low-level FastCGI protocol, Servlet-based MVC architecture with dynamic Canvas rendering, component-driven JSF with PrimeFaces & CDI, ending with a decoupled Single Page Application (SPA) driven by Spring Boot REST API and stateless JWT authentication. |

---

### 3-rd Course

| Discipline | Technology Stack | Scope & Key Topics |
| :--- | :--- | :--- |
| [**Software Testing**](./3-course/Software-Testing) | `JUnit 5`, `Mockito`, `AssertJ`, `Apache JMeter`, `Python` | Comprehensive software quality assurance: isolated unit testing with Taylor series expansions and open-addressing hash maps, integration function testing via bottom-up mocking, and stress/load profiling using Apache JMeter. |
| [**Operating Systems**](./3-course/Operating-Systems) | `C`, `C++`, `POSIX API`, `Linux` | Low-level systems programming: process lifecycles (`fork`, `exec`, `waitpid`), inter-process communication (pipes, FIFOs, POSIX shared memory, message queues), thread synchronization (`pthread_mutex`, condition variables), memory protection (`mmap`, `mprotect`). *(See also: [`Deviantedh/os-course`](https://github.com/Deviantedh/os-course))* |
| [**Computer Networks**](./3-course/Computer-Networks) | `NetEmul`, `Wireshark`, `TCP/IP` | Network topologies, CIDR IPv4 subnets, MAC switching tables, ARP resolution, static and dynamic RIP routing, DHCP address allocation, and deep packet inspection of TCP handshakes, windowing, and HTTP/DNS traffic in Wireshark. |
| [**Image Processing**](./3-course/Image-Processing) | `Python 3`, `OpenCV`, `YOLOv11`, `NumPy` | Digital image analysis: spatial domain filters, histogram equalization, morphological transformations, edge detection (Sobel, Canny), contour segmentation, and real-time object detection with modern YOLOv11. |
| [**Simulation Modeling**](./3-course/Simulation-Modeling) | `GPSS World`, `Queuing Theory` | Discrete-event stochastic simulation of multi-channel queuing systems (M/M/n), arrival stream modeling (Poisson, exponential distributions), queue length optimization, and server utilization estimation. |

---

### 4-th Course

| Discipline | Technology Stack | Scope & Key Topics |
| :--- | :--- | :--- |
| [**Highload Systems**](./4-course/Highload-Systems) | `Java 21 LTS`, `Spring Boot 3`, `PostgreSQL`, `Flyway`, `Docker Compose` | Enterprise workforce and shift scheduling platform. Strict ACID transaction isolation, optimistic locking against concurrency race conditions, automated change auditing via Spring AOP, migration management with Flyway, and containerized deployment with Docker Compose. |
| [**Embedded Systems**](./4-course/Embedded-Systems) | `C`, `STM32 (ARM Cortex-M)`, `UART`, `Python GUI` | Microcontroller firmware development and control software. Real-time serial protocol over UART/COM port, packet validation, checksums, interactive desktop Python GUI client, and automated measurement verification. |
| [**Information Security**](./4-course/Information-Security) | `Network Auditing`, `Cryptography`, `OWASP` | Vulnerability assessment and enterprise threat modeling: web application security audits (OWASP Top 10), penetration testing, cryptographic handshakes, and access control policies. |
| [**Artificial Intelligence**](./4-course/Artificial-Intelligence) | `Python`, `Scikit-Learn`, `Pandas`, `NumPy` | Classical machine learning and exploratory data analysis (EDA): feature engineering, Lasso ($L_1$) and Ridge ($L_2$) regularized regression, Decision Trees, Random Forests, Gradient Boosting, and classification metrics (ROC-AUC, F1). |
| [**Neural Networks**](./4-course/Neural-Networks) | `PyTorch`, `Deep Learning`, `CNN`, `ResNet`, `U-Net` | Deep learning computer vision architectures: Multi-Layer Perceptrons (MLP) with modern optimizers (AdamW), Convolutional Neural Networks (CNN) with regularization (BatchNorm, Dropout), and deep segmentation/detection networks (ResNet, U-Net). |

---

## Engineering Stack Matrix

<p align="center">
  <img src="https://skillicons.dev/icons?i=java,spring,postgres,c,cpp,linux,docker,maven,python,pytorch,git&perline=11" alt="Tech Stack" />
</p>

* **Backend & Systems:** Java 17/21 (Spring Boot 3, Spring Data JPA, Spring Security, Hibernate, Servlets), C/C++ (Linux POSIX, GNU toolchain).
* **Databases & Storage:** PostgreSQL, Flyway Migrations, JDBC, Redis/caching concepts.
* **Embedded & Hardware:** STM32 (ARM Cortex-M), UART, I2C, SPI, Virtual COM.
* **DevOps & QA:** Docker, Docker Compose, Git, Apache Maven, JUnit 5, Mockito, Apache JMeter, Wireshark.
* **Data Science & ML:** Python 3, PyTorch, Scikit-Learn, Pandas, NumPy, OpenCV, Ultralytics YOLOv11.

---

## Companion Repositories

Explore focused specialized repositories:

* [**`android-airplay-receiver`**](https://github.com/Deviantedh/android-airplay-receiver) — Low-level native AirPlay audio receiver engine for Android in pure C / NDK r14b, JNI, OpenSL ES audio output, and zero-dependency RFC 6762 mDNS advertisement.
* [**`os-course`**](https://github.com/Deviantedh/os-course) — Operating systems laboratory works and low-level Linux POSIX implementations.

---

## Hygiene & Structure Standards

* **Clean Codebase:** Zero compiled binaries, `.class` files, `.jar` bundles, or temporary build caches committed (`build/`, `target/`, `.gradle/`).
* **Reproducibility:** Projects contain build descriptor manifests (`pom.xml`, `requirements.txt`, Dockerfiles) ready for reproducible local execution.
* **Academic Integrity:** All collaborative work explicitly cites co-authors and lab partners in corresponding module documentations.

---

## Author

**Georgy Nosov**  
*ITMO University, Saint Petersburg, Russia*  
* Telegram: [@DeviantedH](https://t.me/DeviantedH)  
* Email: [DeviantedH@gmail.com](mailto:DeviantedH@gmail.com)  
* GitHub: [@Deviantedh](https://github.com/Deviantedh)
