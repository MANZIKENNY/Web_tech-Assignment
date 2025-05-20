# Web_tech-Assignment

This repository contains assignments and projects related to web technologies. The structure is organized into three main subdirectories: `Main`, `SpringBoot`, and `TomCat`.

## Repository Structure

- [`Main`](https://github.com/MANZIKENNY/Web_tech-Assignment/tree/main/Main)  
  General resources or core assignment files.  
- [`SpringBoot`](https://github.com/MANZIKENNY/Web_tech-Assignment/tree/main/SpringBoot)  
  Contains a Java Spring Boot project, including Maven wrapper scripts and a typical Maven structure.
- [`TomCat`](https://github.com/MANZIKENNY/Web_tech-Assignment/tree/main/TomCat)  
  Contains a Tomcat-based Java project, including Maven configuration and supporting directories.

## Directory Details

### Main
- Might contain general project files or configuration (e.g., IDE settings in `.idea`).

### SpringBoot
- `.gitattributes` and `.gitignore`: Repo and environment configuration files.
- `.mvn/` and Maven wrapper scripts (`mvnw`, `mvnw.cmd`): Allow building and running the project without a local Maven installation.
- `pom.xml`: Maven build configuration for the Spring Boot application.
- `src/`: Source code for the Spring Boot application.

### TomCat
- `.gitignore`: Git ignore rules.
- `.idea/`: IDE configuration (likely for IntelliJ).
- `lib/`, `out/`, `src/`: Libraries, output, and source directories.
- `pom.xml`: Maven build configuration for the Tomcat project.

## Getting Started

### SpringBoot Project

To build and run the Spring Boot project:

```sh
cd SpringBoot
./mvnw spring-boot:run
```

### TomCat Project

To build the Tomcat Maven project:

```sh
cd TomCat
mvn package
```

(Adjust as needed for your development environment.)

## Requirements

- Java (JDK 8 or higher)
- [Maven](https://maven.apache.org/) (or use the provided Maven wrapper scripts)

## Contributing

1. Fork this repository.
2. Create your feature branch (`git checkout -b feature/your-feature`).
3. Commit your changes (`git commit -am 'Add new feature'`).
4. Push to the branch (`git push origin feature/your-feature`).
5. Create a new Pull Request.

## License

This project is for educational purposes. No explicit license is provided.

---
