# BackOffice Location - Déploiement sur Render

Application Java EE de gestion de réservations d'hôtels et de transport.

## 🚀 Déploiement sur Render

### Prérequis

1. Un compte GitHub
2. Un compte Render (gratuit sur https://render.com)
3. Votre base de données PostgreSQL déjà créée sur Render

### Étape 1 : Préparer le dépôt Git

```bash
# Initialiser Git si ce n'est pas déjà fait
git init

# Ajouter tous les fichiers
git add .

# Créer le premier commit
git commit -m "Initial commit - Application BackOffice Location"

# Créer un repository sur GitHub et le lier
git remote add origin https://github.com/VOTRE_USERNAME/BackOfficeLocation.git
git branch -M main
git push -u origin main
```

### Étape 2 : Déployer sur Render

#### Option A : Via l'interface Render (Recommandé)

1. **Connectez-vous à Render** : https://dashboard.render.com

2. **Créer un nouveau Web Service** :
   - Cliquez sur "New +" → "Web Service"
   - Connectez votre compte GitHub
   - Sélectionnez le repository `BackOfficeLocation`

3. **Configurer le service** :
   - **Name** : `backoffice-location` (ou votre choix)
   - **Runtime** : `Docker`
   - **Branch** : `main`
   - **Dockerfile Path** : `./Dockerfile`

4. **Configurer les variables d'environnement** :
   Cliquez sur "Advanced" puis ajoutez :
   
   | Key | Value |
   |-----|-------|
   | `DATABASE_URL` | `jdbc:postgresql://dpg-d691dmsr85hc73d4f1ag-a.frankfurt-postgres.render.com:5432/location_s5` |
   | `DATABASE_USER` | `location` |
   | `DATABASE_PASSWORD` | `BfsmL3HGAhKbLHcGOzrdD7z4VNbCizds` |

5. **Choisir le plan** :
   - **Free** : Pour tester (l'app s'arrête après 15 min d'inactivité)
   - **Starter ($7/mois)** : Toujours actif

6. **Cliquez sur "Create Web Service"**

7. **Attendez le déploiement** : 
   - Render va construire votre Docker image
   - Le déploiement prend environ 5-10 minutes
   - Vous verrez les logs en temps réel

#### Option B : Via render.yaml (Blueprint)

1. Modifier le fichier [render.yaml](render.yaml) :
   - Changez `repo:` par votre URL GitHub
   - Vérifiez la `branch`

2. Sur Render :
   - Cliquez sur "New +" → "Blueprint"
   - Sélectionnez votre repository
   - Render détectera automatiquement le `render.yaml`

### Étape 3 : Vérifier le déploiement

Une fois déployé, Render vous donnera une URL comme :
```
https://backoffice-location.onrender.com
```

Testez l'application en visitant cette URL.

### Étape 4 : Lier la base de données

Si ce n'est pas déjà fait, assurez-vous que :

1. Votre base de données `location_s5` est bien accessible depuis l'externe
2. Les tables sont créées (via les scripts SQL déjà exécutés)
3. Les données de test sont présentes

## 🔧 Configuration locale pour développement

Pour tester en local avant de déployer :

```bash
# Depuis le dossier BO-location
cd BO-location

# Build avec Maven
mvn clean package

# Déployer sur Tomcat local
# Copier le fichier target/backoffice-location.war dans le dossier webapps de Tomcat
```

## 📁 Structure du projet

```
BackOfficeLocation/
├── BO-location/
│   ├── src/main/
│   │   ├── java/          # Code Java
│   │   └── webapp/        # JSP, CSS, WEB-INF
│   ├── lib/               # JARs externes
│   └── pom.xml           # Configuration Maven
├── sql/                   # Scripts SQL
├── Dockerfile            # Configuration Docker
├── render.yaml           # Configuration Render
└── .dockerignore        # Fichiers à ignorer
```

## 🐛 Dépannage

### L'application ne démarre pas

1. Vérifiez les logs sur Render
2. Vérifiez que les variables d'environnement sont correctes
3. Vérifiez que la base de données est accessible

### Erreur de connexion à la base de données

1. Vérifiez l'URL de connexion
2. Vérifiez que les credentials sont corrects
3. Vérifiez que le hostname est le bon (External URL)

### Erreur 404

Si vous obtenez une erreur 404, vérifiez :
- Le fichier `web.xml` est bien présent dans `WEB-INF/`
- La classe `FrontServlet` est disponible dans `mini-framework.jar`

## 📝 Notes importantes

- **Plan gratuit** : L'application s'arrête après 15 minutes d'inactivité et redémarre au prochain accès (30-60 secondes)
- **Base de données** : Le plan gratuit PostgreSQL a une limite de 1GB
- **Logs** : Accessibles via le dashboard Render

## 🔐 Sécurité

⚠️ **Important** : Ne commitez jamais les mots de passe dans Git !

Pour protéger vos secrets :
1. Utilisez les Environment Variables de Render
2. Créez un fichier `.env.example` avec des exemples de variables
3. Ajoutez `.env` au `.gitignore`

## 📞 Support

Pour toute question :
- Documentation Render : https://render.com/docs
- Support Render : https://render.com/support

---

**Créé le** : 15 février 2026  
**Version** : 1.0.0
