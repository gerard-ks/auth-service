# Auth Service

Système d'authentification et d'autorisation JWT stateless,
conçu pour être réutilisable et agnostique du domaine métier.

---

## Stack technique

| Technologie   | Rôle                                |
|---------------|-------------------------------------|
| Java 21       | Langage                             |
| Spring Boot 3 | Framework principal                 |
| PostgreSQL 15 | Base de données principale          |
| Redis 7       | Sessions + données temporaires      |
| Caffeine      | Cache permissions (local)           |
| RQueue        | File de jobs (emails asynchrones)   |
| Thymeleaf     | Templates emails HTML               |
| Flyway        | Migrations base de données          |

---

## Ce que fait ce service

- Inscription et vérification d'email (lien ou OTP)
- Connexion avec JWT (access token + refresh token)
- Réinitialisation de mot de passe
- Gestion des rôles et permissions (RBAC)
- Protection contre le brute force
- Anonymisation RGPD des comptes
- Administration des comptes, rôles et permissions

---

## Ce que ce service ne fait pas

- Il ne gère pas la logique métier de ton application
- Il ne connaît pas les concepts de ton domaine
  (médecin, patient, employé, etc.)
- Les rôles métier sont créés dynamiquement via l'API admin

---

## Prérequis

- Java 21
- Maven 3.9+
- PostgreSQL 15
- Redis 7
- Un serveur SMTP (ex: Mailgun, SendGrid, Gmail)

---

## Démarrage rapide

### 1. Cloner le projet

git clone https://github.com/gerard-ks/auth-service.git
cd auth-service

### 2. Configurer les variables d'environnement

Copier le fichier d'exemple :

cp .env.example .env

Remplir les valeurs dans `.env` :

# Base de données
DB_URL=jdbc:postgresql://localhost:5432/auth_db
DB_USERNAME=postgres
DB_PASSWORD=secret

# JWT
JWT_SECRET_KEY=une-clé-secrète-suffisamment-longue
JWT_ISSUER=auth-service
JWT_TTL_MINUTES=15

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379

# Email SMTP
MAIL_HOST=smtp.mailgun.org
MAIL_PORT=587
MAIL_USERNAME=no-reply@tonapp.com
MAIL_PASSWORD=secret
MAIL_SMTP_AUTH=true
MAIL_SMTP_STARTTLS=true
MAIL_SMTP_STARTTLS_REQUIRED=true

# Application
APP_BASE_URL=https://auth.tonapp.com
APP_VERIFICATION_ENABLED=true

# Premier administrateur (créé au démarrage)
APP_INITIAL_ADMIN_EMAIL=admin@tonapp.com
APP_INITIAL_ADMIN_PASSWORD=Admin123!
APP_INITIAL_ADMIN_FIRSTNAME=Admin
APP_INITIAL_ADMIN_LASTNAME=System

### 3. Lancer le service

mvn spring-boot:run

Le service démarre sur : http://localhost:8080

---

## Structure des tokens

Ce service utilise deux tokens :

### Access Token (JWT)
- Durée de vie : 15 minutes
- Contient : identifiant du compte, email, rôles
- Utilisé pour accéder aux ressources protégées

### Refresh Token
- Durée de vie : 7 jours
- Stocké dans Redis
- Rotation obligatoire à chaque appel
- Révoqué en cas de reset password ou désactivation

### Deux modes de transport

Tu choisis le mode via le header obligatoire `X-Token-Strategy` :

| Valeur | Access Token | Refresh Token                  |
|--------|--------------|--------------------------------|
| COOKIE | Body JSON    | Cookie HttpOnly Secure         |
| BEARER | Body JSON    | Body JSON                      |

> Header absent ou invalide → 400 INVALID_TOKEN_STRATEGY

---

## Rôles

### Rôles système (créés automatiquement)

| Rôle  | Description                             |
|-------|-----------------------------------------|
| USER  | Assigné automatiquement à l'inscription |
| ADMIN | Accès à l'administration du service     |

### Rôles métier
Créés librement via l'API admin selon ton domaine :

POST /api/admin/roles
{
"name": "DOCTOR",
"description": "Médecin de la plateforme"
}

---

## Endpoints principaux

### Authentification (public)

| Méthode | Endpoint                       | Description                   |
|---------|--------------------------------|-------------------------------|
| POST    | /api/auth/register             | Créer un compte               |
| POST    | /api/auth/verify-email/token   | Vérifier email par lien       |
| POST    | /api/auth/verify-email/otp     | Vérifier email par OTP        |
| POST    | /api/auth/resend-verification  | Renvoyer l'email de vérif.    |
| POST    | /api/auth/login                | Se connecter                  |
| POST    | /api/auth/refresh              | Rafraîchir les tokens         |
| POST    | /api/auth/forgot-password      | Demander un reset             |
| POST    | /api/auth/reset-password       | Réinitialiser le mot de passe |

### Mon compte (authentifié)

| Méthode | Endpoint                    | Description                  |
|---------|-----------------------------|------------------------------|
| POST    | /api/auth/logout            | Se déconnecter               |
| GET     | /api/accounts/me            | Consulter mon profil         |
| PUT     | /api/accounts/me            | Modifier mon profil          |
| PATCH   | /api/accounts/me/password   | Changer mon mot de passe     |
| PATCH   | /api/accounts/me/anonymize  | Anonymiser mon compte (RGPD) |

### Administration (rôle ADMIN requis)

| Méthode | Endpoint                                   | Description             |
|---------|--------------------------------------------|-------------------------|
| GET     | /api/admin/accounts                        | Lister les comptes      |
| GET     | /api/admin/accounts/{id}                   | Détail d'un compte      |
| PATCH   | /api/admin/accounts/{id}/disable           | Désactiver un compte    |
| PATCH   | /api/admin/accounts/{id}/enable            | Activer un compte       |
| POST    | /api/admin/accounts/{id}/roles/{roleId}    | Assigner un rôle        |
| DELETE  | /api/admin/accounts/{id}/roles/{roleId}    | Révoquer un rôle        |
| GET     | /api/admin/roles                           | Lister les rôles        |
| POST    | /api/admin/roles                           | Créer un rôle           |
| GET     | /api/admin/roles/{id}                      | Détail d'un rôle        |
| PUT     | /api/admin/roles/{id}                      | Modifier un rôle        |
| DELETE  | /api/admin/roles/{id}                      | Supprimer un rôle       |
| POST    | /api/admin/roles/{id}/permissions/{permId} | Ajouter une permission  |
| DELETE  | /api/admin/roles/{id}/permissions/{permId} | Retirer une permission  |
| GET     | /api/admin/permissions                     | Lister les permissions  |
| POST    | /api/admin/permissions                     | Créer une permission    |
| GET     | /api/admin/permissions/{id}                | Détail d'une permission |
| PUT     | /api/admin/permissions/{id}                | Modifier une permission |
| DELETE  | /api/admin/permissions/{id}                | Supprimer une permission|

---

## Format des erreurs

Toutes les erreurs suivent le standard RFC 7807 :

{
"title"         : "Email already exists",
"status"        : 409,
"detail"        : "An account with this email address already exists",
"errorCode"     : "EMAIL_ALREADY_EXISTS",
"correlationId" : "550e8400-e29b-41d4-a716-446655440000",
"timestamp"     : "2024-01-15T10:00:00Z"
}

### Codes HTTP utilisés

| Code | Signification         | Exemple                         |
|------|-----------------------|---------------------------------|
| 400  | Requête invalide      | Header manquant, champ invalide |
| 401  | Non authentifié       | Token expiré, mauvais password  |
| 403  | Accès refusé          | Permission insuffisante         |
| 404  | Ressource introuvable | Compte ou rôle inexistant       |
| 405  | Méthode non autorisée | GET sur un endpoint POST        |
| 409  | Conflit               | Email déjà utilisé              |
| 415  | Format non supporté   | Content-Type incorrect          |
| 422  | Règle métier violée   | Même mot de passe, auto-disable |
| 429  | Trop de requêtes      | Brute force, OTP bloqué         |
| 500  | Erreur serveur        | Erreur inattendue               |
| 503  | Service indisponible  | Base de données inaccessible    |

---

## Politique de mot de passe

Un mot de passe valide doit contenir :

- Au moins 8 caractères
- Au moins 1 lettre majuscule
- Au moins 1 lettre minuscule
- Au moins 1 chiffre
- Au moins 1 caractère spécial

---

## Sécurité

- Brute force : compte bloqué après 5 tentatives en 15 minutes
- Rotation obligatoire du refresh token à chaque appel
- Révocation totale des sessions sur reset ou désactivation
- Données sensibles jamais loggées ni retournées en HTTP
- Conformité RGPD avec anonymisation irréversible des comptes

---

## Documentation API

Disponible au démarrage sur :

http://localhost:8080/swagger-ui.html

---

## Migrations base de données

Gérées automatiquement par Flyway au démarrage.

| Migration | Contenu                         |
|-----------|---------------------------------|
| V1        | Table accounts                  |
| V2        | Tables roles et permissions     |
| V3        | Table account_roles             |
| V4        | Rôles système (USER, ADMIN)     |
| V5        | Permissions système             |
| V6        | Permissions assignées à ADMIN   |
| V7        | Index supplémentaires           |

---

## Variables d'environnement complètes

### Base de données — PostgreSQL

| Variable    | Description            | Défaut |
|-------------|------------------------|--------|
| DB_URL      | URL de connexion       | —      |
| DB_USERNAME | Utilisateur PostgreSQL | —      |
| DB_PASSWORD | Mot de passe           | —      |

---

### Redis

| Variable   | Description | Défaut |
|------------|-------------|--------|
| REDIS_HOST | Hôte Redis  | —      |
| REDIS_PORT | Port Redis  | 6379   |

---

### JWT

> Générer une clé secrète : `openssl rand -base64 32`

| Variable        | Description               | Défaut |
|-----------------|---------------------------|--------|
| JWT_SECRET_KEY  | Clé secrète de signature  | —      |
| JWT_ISSUER      | Émetteur du token         | —      |
| JWT_TTL_MINUTES | Durée de vie access token | 15     |

---

### Email — SMTP

| Variable                    | Description           | Défaut |
|-----------------------------|-----------------------|--------|
| MAIL_HOST                   | Hôte SMTP             | —      |
| MAIL_PORT                   | Port SMTP             | —      |
| MAIL_USERNAME               | Utilisateur SMTP      | —      |
| MAIL_PASSWORD               | Mot de passe SMTP     | —      |
| MAIL_SMTP_AUTH              | Authentification SMTP | true   |
| MAIL_SMTP_STARTTLS          | Activer STARTTLS      | true   |
| MAIL_SMTP_STARTTLS_REQUIRED | STARTTLS obligatoire  | true   |

---

### CORS

| Variable                      | Description                      | Défaut |
|-------------------------------|----------------------------------|--------|
| APP_CORS_ALLOWED_ORIGINS      | Origines autorisées              | —      |
| APP_CORS_ALLOWED_METHODS      | Méthodes HTTP autorisées         | —      |
| APP_CORS_ALLOWED_HEADERS      | Headers autorisés                | —      |
| APP_CORS_EXPOSED_HEADERS      | Headers exposés au client        | —      |
| APP_CORS_ALLOW_CREDENTIALS    | Autoriser les cookies            | true   |
| APP_CORS_MAX_AGE              | Durée cache preflight (secondes) | 3600   |

---

### Application

| Variable                              | Description                       | Défaut |
|---------------------------------------|-----------------------------------|--------|
| APP_BASE_URL                          | URL publique du service           | —      |
| APP_VERIFICATION_ENABLED              | Vérification email active         | true   |
| APP_VERIFICATION_TTL_MINUTES          | Durée validité token vérification | 15     |
| APP_VERIFICATION_RESEND_DELAY_MINUTES | Délai minimum avant renvoi        | 2      |
| APP_BRUTE_FORCE_MAX_ATTEMPTS          | Tentatives avant blocage login    | 5      |
| APP_BRUTE_FORCE_BLOCK_MINUTES         | Durée du blocage login            | 15     |
| APP_OTP_MAX_ATTEMPTS                  | Tentatives OTP avant blocage      | 5      |
| APP_OTP_BLOCK_MINUTES                 | Durée du blocage OTP              | 15     |
| APP_REFRESH_TOKEN_TTL_DAYS            | Durée de vie refresh token        | 7      |

---

### File d'emails — RQueue

| Variable                         | Description                 | Défaut |
|----------------------------------|-----------------------------|--------|
| APP_EMAIL_QUEUE_MAX_ATTEMPTS     | Tentatives max d'envoi      | 3      |
| APP_EMAIL_QUEUE_BACKOFF_DELAY_MS | Délai entre tentatives (ms) | 2000   |
| APP_EMAIL_QUEUE_JOB_DEADLINE_MS  | Timeout par tentative (ms)  | 10000  |

---

### Premier administrateur

> Créé automatiquement au premier démarrage si absent.
> Le mot de passe doit respecter la politique de sécurité.

| Variable                    | Description              | Défaut |
|-----------------------------|--------------------------|--------|
| APP_INITIAL_ADMIN_EMAIL     | Email de l'admin initial | —      |
| APP_INITIAL_ADMIN_PASSWORD  | Mot de passe             | —      |
| APP_INITIAL_ADMIN_FIRSTNAME | Prénom                   | —      |
| APP_INITIAL_ADMIN_LASTNAME  | Nom                      | —      |

---

### Actuator

| Variable                                  | Description             | Défaut          |
|-------------------------------------------|-------------------------|-----------------|
| MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE | Endpoints exposés       | health,info     |
| MANAGEMENT_ENDPOINT_HEALTH_SHOW_DETAILS   | Niveau de détail health | when-authorized |

---