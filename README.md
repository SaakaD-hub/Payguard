╔═══════════════════════════════════════════════════════════════════════════╗
║                                                                           ║
║                           PAYGUARD 🛡️                                     ║
║                                                                           ║
║              Enterprise Fintech Payment Platform                          ║
║                   Technical Documentation                                 ║
║                                                                           ║
║                         Developed by                                      ║
║                       SAAKA DAVID                                         ║
║                 Senior Software Engineer                                  ║
║                                                                           ║
║              Version: 1.0.0  |  Date: March 2026                         ║
║                                                                           ║
╚═══════════════════════════════════════════════════════════════════════════╝

Secure, scalable microservices platform for payment processing, fraud 
detection, and transaction reconciliation.

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

📋 TABLE OF CONTENTS

   1. Overview
   2. Technology Stack
   3. System Architecture
   4. End-to-End Payment Processing Flow
   5. Microservices Overview
   6. Quick Start Guide
   7. API Endpoints
   8. Security Architecture
   9. Deployment
  10. CI/CD Pipeline
  11. Troubleshooting
  12. Support & Contact

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

📋 OVERVIEW

PayGuard is an enterprise-grade microservices platform built with Spring Boot 
for secure payment processing. The system features:

   ✓ Centralized JWT authentication at API Gateway
   ✓ Multi-tenant architecture with isolated databases
   ✓ Event-driven design with Kafka for async processing
   ✓ ML-based fraud detection with Redis caching
   ✓ Docker containerization and Kubernetes deployment

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

🛠️ TECHNOLOGY STACK

┌────────────────────┬─────────────────────────┬──────────┬─────────────┐
│ Category           │ Technology              │ Version  │ Purpose     │
├────────────────────┼─────────────────────────┼──────────┼─────────────┤
│ Language           │ Java                    │ 17+      │ Core        │
│ Framework          │ Spring Boot             │ 3.2.0    │ Microservcs │
│ API Gateway        │ Spring Cloud Gateway    │ 4.1.0    │ Routing     │
│ Security           │ Spring Security + JWT   │ 6.1.1    │ Auth        │
│ Payment            │ Stripe Java SDK         │ 24.x     │ Processing  │
│ ML Serving         │ ONNX Runtime            │ 1.16.x   │ Fraud       │
│ Messaging          │ Apache Kafka            │ 3.6.0    │ Events      │
│ Database           │ PostgreSQL              │ 15+      │ Storage     │
│ Cache              │ Redis                   │ 7.x      │ Caching     │
│ Migration          │ Flyway                  │ 10.4.1   │ DB Version  │
│ Container          │ Docker                  │ 20.x     │ Local Dev   │
│ Orchestration      │ Kubernetes              │ 1.24+    │ Production  │
└────────────────────┴─────────────────────────┴──────────┴─────────────┘

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

🏛️ SYSTEM ARCHITECTURE

PayGuard follows a microservices architecture with centralized authentication:

┌─────────────────────────────────────────────────────────────────────────┐
│                        ARCHITECTURE LAYERS                               │
└─────────────────────────────────────────────────────────────────────────┘

  ┌───────────────────────────────────────────────────────────────┐
  │                     CLIENT LAYER                               │
  │      Web Apps  •  Mobile Apps  •  Third-party APIs            │
  └───────────────────────────┬───────────────────────────────────┘
                              │
                              │ HTTPS/TLS
                              │
  ┌───────────────────────────▼───────────────────────────────────┐
  │                      API GATEWAY :8089                         │
  │   JWT Validation • Routing • Rate Limiting • CORS             │
  └───┬────────┬──────────┬───────────┬────────────┬──────────────┘
      │        │          │           │            │
      ▼        ▼          ▼           ▼            ▼
  ┌───────┐┌────────┐┌────────┐┌──────────┐┌─────────────┐
  │ USER  ││PAYMENT ││ FRAUD  ││  NOTIF   ││    RECON    │
  │  SVC  ││  SVC   ││ ENGINE ││   SVC    ││     SVC     │
  │ :8081 ││ :8082  ││ :8083  ││  :8084   ││    :8085    │
  └───┬───┘└────┬───┘└────┬───┘└─────┬────┘└──────┬──────┘
      │         │         │          │            │
      └────┬────┴─────┬───┴──────┬───┴────────┬───┴─────────┐
           │          │          │            │             │
           ▼          ▼          ▼            ▼             ▼
      ┌─────────┐┌────────┐┌───────┐┌──────────┐┌─────────────┐
      │PostgreSQL││ Redis  ││ Kafka ││Zookeeper ││   External  │
      │  5 DBs   ││ Cache  ││Streams││Coordinator││  Services   │
      │   15+    ││  7.x   ││ 3.6   ││   3.6    ││ Stripe/SMTP │
      └──────────┘└────────┘└───────┘└──────────┘└─────────────┘

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

💳 END-TO-END PAYMENT PROCESSING FLOW

Use Case: Merchant processes a $100 payment from a customer using Stripe

┌─────────────────────────────────────────────────────────────────────────┐
│                  COMPLETE TRANSACTION SEQUENCE                           │
└─────────────────────────────────────────────────────────────────────────┘

─── STEP 1: USER AUTHENTICATION ──────────────────────────────────────────

  Client → API Gateway: POST /api/v1/auth/login {email, password}
  API Gateway → User Service: Forward authentication request
  User Service → PostgreSQL: Query user by email
  User Service: BCrypt.compare(password, hash) → ✓ validated
  User Service: Generate JWT (userId, email, role, exp: 24h)
  User Service → API Gateway: Return {token, userId}
  API Gateway → Client: 200 OK + JWT token

─── STEP 2: PAYMENT REQUEST INITIATION ───────────────────────────────────

  Client → API Gateway: POST /api/v1/payments/charge
    Headers: Authorization: Bearer <JWT>
    Body: {amount: 10000, currency: 'USD', customerEmail, stripeToken}
  
  API Gateway: Validate JWT signature with JWT_SECRET
  API Gateway: Extract claims → userId, email, role
  API Gateway → Payment Service: Forward request with injected headers
    X-User-Id: merchant-uuid
    X-User-Email: merchant@example.com
    X-User-Role: MERCHANT

─── STEP 3: EXTERNAL PAYMENT PROCESSING ──────────────────────────────────

  Payment Service → Stripe API: POST /v1/charges
    {amount: 10000, currency: 'USD', source: stripeToken}
  
  Stripe API: Process payment via card network
  Stripe API → Payment Service: 200 OK {id: ch_xxx, status: 'succeeded'}
  
  Payment Service → PostgreSQL: INSERT payment record
    {id, merchantId, amount, currency, status: 'PENDING', stripeChargeId}

─── STEP 4: REAL-TIME FRAUD DETECTION ────────────────────────────────────

  Payment Service → Fraud Engine: GET /api/v1/fraud/score/{paymentId}
  Fraud Engine → Redis: GET fraud_score:{paymentId} → cache miss
  Fraud Engine: Load ONNX fraud detection model
  Fraud Engine: Extract features {amount, location, deviceId, timestamp}
  Fraud Engine: Run inference → ML score: 0.12
  Fraud Engine: Apply business rules → velocity check, amount threshold
  Fraud Engine: Final score: 0.15, Risk: LOW (<0.3)
  Fraud Engine → Redis: SET fraud_score:{paymentId} EX 900 (15 min TTL)
  Fraud Engine → PostgreSQL: INSERT fraud_audit_log
    {paymentId, score: 0.15, riskLevel: 'LOW', modelVersion, evaluatedAt}
  
  Fraud Engine → Payment Service: Return {score: 0.15, riskLevel: 'LOW'}
  Payment Service → PostgreSQL: UPDATE payment 
    SET status='COMPLETED', fraudScore=0.15

─── STEP 5: ASYNCHRONOUS EVENT PUBLISHING ────────────────────────────────

  Payment Service → Kafka: PUBLISH to 'payment.completed' topic
    Event payload:
    {
      paymentId: 'uuid', merchantId: 'uuid', amount: 10000,
      status: 'COMPLETED', fraudScore: 0.15, timestamp
    }
  
  Kafka: Store event in partition, replicate to brokers

─── STEP 6: NOTIFICATION SERVICE PROCESSING ──────────────────────────────

  Kafka → Notification Service: Consumer polls 'payment.completed' event
  Notification Service: Load email template 'payment_confirmation.html'
  Notification Service: Inject payment details (amount, date, merchant)
  Notification Service → SMTP Server: Send email to merchant@example.com
  SMTP Server → Notification Service: 250 OK (Email accepted)
  Notification Service → PostgreSQL: INSERT notification_log
    {paymentId, type: 'EMAIL', recipient, status: 'SENT', sentAt}

─── STEP 7: FINAL RESPONSE TO CLIENT ─────────────────────────────────────

  Payment Service → API Gateway: Return payment response
    Response payload:
    {
      id: 'payment-uuid', merchantId: 'merchant-uuid',
      amount: 10000, currency: 'USD', status: 'COMPLETED',
      fraudScore: 0.15, riskLevel: 'LOW',
      stripeChargeId: 'ch_xxx', createdAt: '2026-03-21T10:00:00Z'
    }
  
  API Gateway → Client: HTTP 200 OK + JSON response

┌─────────────────────────────────────────────────────────────────────────┐
│  ⏱ TOTAL END-TO-END PROCESSING TIME: ~850ms                             │
│                                                                          │
│  ✓ Payment: 350ms     ✓ Fraud Check: 200ms     ✓ Database: 150ms       │
│  ✓ Kafka: 50ms        ✓ Gateway Overhead: 100ms                         │
└─────────────────────────────────────────────────────────────────────────┘

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

📦 MICROSERVICES OVERVIEW

┌───────────────────────┬──────┬─────────────────┬─────────────────────────┐
│ Service               │ Port │ Database        │ Key Responsibilities    │
├───────────────────────┼──────┼─────────────────┼─────────────────────────┤
│ API Gateway           │ 8089 │ -               │ JWT validation,         │
│                       │      │                 │ routing, CORS, rate     │
│                       │      │                 │ limiting                │
├───────────────────────┼──────┼─────────────────┼─────────────────────────┤
│ User Service          │ 8081 │ users           │ Registration, login,    │
│                       │      │                 │ JWT generation,         │
│                       │      │                 │ profile mgmt            │
├───────────────────────┼──────┼─────────────────┼─────────────────────────┤
│ Payment Service       │ 8082 │ payments        │ Stripe integration,     │
│                       │      │                 │ charge/refund, history  │
├───────────────────────┼──────┼─────────────────┼─────────────────────────┤
│ Fraud Engine          │ 8083 │ fraud           │ ML scoring, business    │
│                       │      │ Redis cache     │ rules, risk assessment  │
├───────────────────────┼──────┼─────────────────┼─────────────────────────┤
│ Notification Service  │ 8084 │ notifications   │ Email/SMS, Kafka        │
│                       │      │                 │ consumer, templates     │
├───────────────────────┼──────┼─────────────────┼─────────────────────────┤
│ Reconciliation Svc    │ 8085 │ reconciliation  │ Settlement,             │
│                       │      │                 │ discrepancy detection,  │
│                       │      │                 │ reports                 │
└───────────────────────┴──────┴─────────────────┴─────────────────────────┘

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

🚀 QUICK START GUIDE

┌─────────────────────────────────────────────────────────────────────────┐
│ PREREQUISITES                                                            │
└─────────────────────────────────────────────────────────────────────────┘

  • Java 17+ (Eclipse Temurin recommended)
  • Maven 3.8+ for build management
  • Docker 20+ and Docker Compose 2.0+

┌─────────────────────────────────────────────────────────────────────────┐
│ INSTALLATION STEPS                                                       │
└─────────────────────────────────────────────────────────────────────────┘

  1. Clone repository and build services:
     
     mvn clean package -DskipTests

  2. Start all services:
     
     docker-compose up -d

  3. Create databases (one-time setup):
     
     Run database creation scripts for:
     users, payments, fraud, notifications, reconciliation

  4. Verify services:
     
     curl http://localhost:8089/actuator/health

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

📡 API ENDPOINTS

┌─────────────────────────────────────────────────────────────────────────┐
│ PUBLIC ENDPOINTS (No Authentication Required)                           │
└─────────────────────────────────────────────────────────────────────────┘

┌────────┬──────────────────────────────┬────────────────────────────────┐
│ Method │ Endpoint                     │ Description                    │
├────────┼──────────────────────────────┼────────────────────────────────┤
│ POST   │ /api/v1/auth/register        │ Register new merchant account  │
│ POST   │ /api/v1/auth/login           │ Login and receive JWT token    │
│ GET    │ /actuator/health             │ Service health check           │
└────────┴──────────────────────────────┴────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│ PROTECTED ENDPOINTS (JWT Required)                                      │
└─────────────────────────────────────────────────────────────────────────┘

┌────────┬─────────────────────────────────┬──────────────────────────────┐
│ Method │ Endpoint                        │ Description                  │
├────────┼─────────────────────────────────┼──────────────────────────────┤
│ GET    │ /api/v1/users/me                │ Get current user profile     │
│ POST   │ /api/v1/payments/charge         │ Create payment transaction   │
│ GET    │ /api/v1/payments/{id}           │ Get payment details          │
│ GET    │ /api/v1/fraud/score/{paymentId} │ Get fraud risk score         │
└────────┴─────────────────────────────────┴──────────────────────────────┘

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

🔐 SECURITY ARCHITECTURE

┌─────────────────────────────────────────────────────────────────────────┐
│ CENTRALIZED AUTHENTICATION FLOW                                          │
└─────────────────────────────────────────────────────────────────────────┘

  1. Client sends credentials to API Gateway
  2. Gateway validates JWT token (256-bit HMAC-SHA256)
  3. Extracts user claims (userId, email, role)
  4. Injects headers: X-User-Id, X-User-Email, X-User-Role
  5. Downstream services trust Gateway headers

┌─────────────────────────────────────────────────────────────────────────┐
│ SECURITY FEATURES                                                        │
└─────────────────────────────────────────────────────────────────────────┘

  ✓ Stateless JWT tokens (no server-side sessions)
  ✓ BCrypt password hashing (10 rounds of salting)
  ✓ CSRF disabled for REST APIs
  ✓ Role-based access control (MERCHANT, ADMIN)
  ✓ PostgreSQL SSL connections for data security

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

🐳 DEPLOYMENT

┌─────────────────────────────────────────────────────────────────────────┐
│ DOCKER CONFIGURATION                                                     │
└─────────────────────────────────────────────────────────────────────────┘

┌────────────────────────┬──────────────┬───────────┬─────────────────────┐
│ Service                │ Container    │ Host      │ Access URL          │
│                        │ Port         │ Port      │                     │
├────────────────────────┼──────────────┼───────────┼─────────────────────┤
│ API Gateway            │ 8080         │ 8089      │ localhost:8089      │
│ User Service           │ 8081         │ 8081      │ localhost:8081      │
│ Payment Service        │ 8082         │ 8082      │ localhost:8082      │
│ Fraud Engine           │ 8083         │ 8083      │ localhost:8083      │
│ Notification Service   │ 8084         │ 8084      │ localhost:8084      │
│ Reconciliation Service │ 8085         │ 8085      │ localhost:8085      │
│ PostgreSQL             │ 5432         │ 5433      │ localhost:5433      │
│ Redis                  │ 6379         │ 6379      │ localhost:6379      │
│ Kafka                  │ 9092         │ 9092      │ localhost:9092      │
└────────────────────────┴──────────────┴───────────┴─────────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│ KUBERNETES DEPLOYMENT                                                    │
└─────────────────────────────────────────────────────────────────────────┘

PayGuard includes complete Kubernetes manifests for production deployment:

  • Namespace isolation (payguard)
  • ConfigMaps for environment configuration
  • Secrets management for sensitive data
  • StatefulSets for PostgreSQL
  • Deployments with replicas (2x each service)
  • LoadBalancer service for API Gateway
  • Health probes (liveness & readiness)
  • Horizontal pod autoscaling

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

🔄 CI/CD PIPELINE

┌─────────────────────────────────────────────────────────────────────────┐
│ AUTOMATED PIPELINE WITH GITHUB ACTIONS                                   │
└─────────────────────────────────────────────────────────────────────────┘

  Stage 1: Build & Test (~3 min)
           Compile, unit tests, code coverage

  Stage 2: Quality Scan (~2 min)
           SonarQube, security scanning

  Stage 3: Package (~4 min)
           Build Docker images, push to registry

  Stage 4: Deploy Staging (~5 min)
           Auto-deploy, integration tests

  Stage 5: Deploy Production (~3 min)
           Manual approval, blue/green deployment

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

🐛 TROUBLESHOOTING

┌──────────────────────────┬─────────────────────┬──────────────────────────┐
│ Issue                    │ Cause               │ Solution                 │
├──────────────────────────┼─────────────────────┼──────────────────────────┤
│ 500 Error:               │ API Gateway using   │ Use user-service:8081    │
│ Connection Refused       │ localhost           │ in GatewayConfig         │
├──────────────────────────┼─────────────────────┼──────────────────────────┤
│ 403 Forbidden: CSRF      │ CSRF protection     │ Add .csrf(csrf ->        │
│                          │ enabled             │ csrf.disable())          │
├──────────────────────────┼─────────────────────┼──────────────────────────┤
│ Database Does Not Exist  │ Databases not       │ Run CREATE DATABASE      │
│                          │ created             │ commands                 │
├──────────────────────────┼─────────────────────┼──────────────────────────┤
│ Kafka Connection Error   │ Wrong listener      │ Use PLAINTEXT://         │
│                          │ config              │ kafka:9092               │
├──────────────────────────┼─────────────────────┼──────────────────────────┤
│ JWT Validation Failed    │ Mismatched secrets  │ Ensure JWT_SECRET        │
│                          │                     │ matches                  │
└──────────────────────────┴─────────────────────┴──────────────────────────┘

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

📞 SUPPORT & CONTACT

  📧 Email:          davidsaaka77@payguard.com
  💬 Slack:          #payguard-dev
  🐛 GitHub Issues:  https://github.com/saakadavid/payguard/issues
  📖 Documentation:  GitHub Wiki

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

                  
              Senior Software Engineer | March 2026
        As part of backend engineering mastery journey

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
