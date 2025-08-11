# PChat Container Build Guide

This guide explains the different container build options available for the PChat Quarkus application, with a focus on Red Hat UBI (Universal Base Image) containers.

## Container Options

### 1. UBI 9 JDK21 Runtime packaged as uber jar (Recommended)
**File:** `src/main/docker/Containerfile.ubi9-jre-uber`

- **Base Image:** `registry.access.redhat.com/ubi9/openjdk-21-runtime:latest`
- **Java Version:** OpenJDK 21 (headless)
- **Build Type:** Runtime-only (requires pre-built application as uber jar)
- **Image Size:** ~210MB (smallest)
- **Security:** Minimal attack surface with only essential packages

**Benefits:**
- Smallest image size and attack surface
- Latest UBI 9 base with security updates
- Optimized for production workloads
- FIPS compliance ready

**Usage:**
```bash
# Build application first
./mvnw package -Dquarkus.package.jar.type=uber-jar

# Build container
podman build -f src/main/docker/Containerfile.ubi9-jre-uber -t pchat:ubi9jre21uber-latest .
```

### 2. UBI 9 JDK21 Runtime (Slightly Larger)
**File:** `src/main/docker/Containerfile.ubi9-jre`

- **Base Image:** UBI 9 with OpenJDK 21 (runtime)
- **Java Version:** OpenJDK 21
- **Build Type:** Runtime-only (requires pre-built application)
- **Image Size:** ~220MB
- **Modularity:** Artifacts deployed as layers for incremental updates

**Benefits:**
- Latest UBI 9 base with security updates
- Optimized layering for better caching
- Build-time security scanning
- FIPS compliance ready

**Usage:**
```bash
# Build application first
./mvnw package

# Build container
podman build -f src/main/docker/Containerfile.ubi9-jre-uber -t pchat:ubi9jre21uber-latest .

```


## Runtime Configuration

### Environment Variables

All container variants support these runtime environment variables:

#### JVM Configuration
```bash
# Base JVM options (overrides defaults)
JAVA_OPTS="-Xmx512m -XX:+UseG1GC"

# Additional JVM options (appended)
JAVA_OPTS_APPEND="-Dmy.property=value"

# Memory management
JAVA_MAX_MEM_RATIO=75          # Percentage of container memory for heap
JAVA_INITIAL_MEM_RATIO=25      # Initial heap as percentage of max heap
```

#### Quarkus Configuration
```bash
# Application settings
QUARKUS_HTTP_HOST=0.0.0.0      # HTTP binding host
QUARKUS_HTTP_PORT=8080         # HTTP port
QUARKUS_LOG_LEVEL=INFO         # Log level
QUARKUS_PROFILE=prod           # Quarkus profile

# Database configuration
QUARKUS_DATASOURCE_JDBC_URL="jdbc:postgresql://db:5432/pchat"
QUARKUS_DATASOURCE_USERNAME="pchat_user"
QUARKUS_DATASOURCE_PASSWORD="password"
```

#### Debugging
```bash
# Enable remote debugging
JAVA_DEBUG=true
JAVA_DEBUG_PORT="*:5005"      # Debug port binding
```

### Example Runtime Commands

#### Development
```bash
# Development with debugging
podman run -it --rm \
  --network=host \
  -e JAVA_DEBUG=true \
  -e QUARKUS_LOG_LEVEL=DEBUG \
  pchat:ubi9jre21uber-latest
```

#### Production
```bash
# Production deployment
podman run -d \
  -p 8080:8080 \
  -e JAVA_OPTS="-Xmx1g -XX:+UseG1GC" \
  -e QUARKUS_PROFILE=prod \
  -v /app/config:/deployments/config:ro \
  --name pchat-prod \
  pchat:ubi9jre21uber-latest
```


## Security Considerations

### UBI Advantages
- **FIPS Compliance:** UBI images are FIPS 140-2 compliant
- **CVE Scanning:** Regular security updates from Red Hat
- **Support:** Enterprise support available with RHEL subscription
- **Compliance:** Meets enterprise security requirements

### Container Security Best Practices
1. **Non-root User:** All containers run as non-root user (UID 185)
2. **Read-only Filesystem:** Application files are read-only
3. **Minimal Packages:** Only essential packages installed
4. **Security Contexts:** Kubernetes security contexts configured
5. **Image Scanning:** Integrate with security scanning tools

### Vulnerability Management
```bash
# Scan with Podman/Skopeo
podman scan   pchat:ubi9jre21uber-latest

# Scan with Trivy
trivy image   pchat:ubi9jre21uber-latest

# Scan with Clair
clair-scanner   pchat:ubi9jre21uber-latest
```



## Troubleshooting

### Common Issues

#### Build Failures
```bash
# Check build dependencies
./mvnw dependency:tree

# Clean build
./mvnw clean package -DskipTests

# Verbose container build
podman build --log-level debug -f src/main/docker/Containerfile.ubi9-jre-uber -t test .
```

#### Runtime Issues
```bash
# Check container logs
podman logs pchat-container

# Debug inside container
podman exec -it pchat-container /bin/bash

# Check Java version
podman exec -it pchat-container java -version
```



## Performance Optimization

### JVM Tuning for Containers
```bash
# G1GC with low pause times
JAVA_OPTS="-XX:+UseG1GC -XX:MaxGCPauseMillis=100"

# Container-aware settings
JAVA_OPTS="-XX:+UseContainerSupport -XX:InitialRAMPercentage=50"

# Startup optimization
JAVA_OPTS="-XX:+TieredCompilation -XX:TieredStopAtLevel=1"
```


## Registry Configuration

### Container Registry Options
```bash
# Red Hat Quay
podman tag pchat:ubi9jre21uber-latest quay.io/yourorg/pchat:ubi9-latest
podman push quay.io/yourorg/pchat:ubi9-latest

```

This guide provides information for building and deploying PChat containers using Red Hat UBI images with optimal security and performance characteristics.
