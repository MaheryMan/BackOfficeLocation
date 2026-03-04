# Guide Rapide - Déploiement BackOffice sur Render

## 📋 Checklist avant déploiement

- [x] Base de données PostgreSQL créée sur Render
- [x] Tables créées et données insérées
- [x] Code modifié pour utiliser les variables d'environnement
- [x] Fichiers de configuration Docker créés
- [ ] Repository GitHub créé et code pushé
- [ ] Service Render configuré

## 🚀 Étapes à suivre

### 1. Créer le repository GitHub

```powershell
# Initialiser Git (si pas déjà fait)
git init

# Ajouter tous les fichiers
git add .

# Commit initial
git commit -m "Initial commit - BackOffice Location avec Docker"

# Créer un repo sur GitHub et le lier
git remote add origin https://github.com/VOTRE_USERNAME/BackOfficeLocation.git
git branch -M main
git push -u origin main
```

### 2. Déployer sur Render

#### Via l'interface web (le plus simple) :

1. Aller sur https://dashboard.render.com
2. Cliquer sur **"New +" → "Web Service"**
3. Connecter GitHub et sélectionner votre repo
4. Configurer :
   - **Name** : `backoffice-location`
   - **Runtime** : `Docker`
   - **Branch** : `main`
5. Ajouter les variables d'environnement :
   ```
   DATABASE_URL = jdbc:postgresql://dpg-d691dmsr85hc73d4f1ag-a.frankfurt-postgres.render.com:5432/location_s5
   DATABASE_USER = location
   DATABASE_PASSWORD = BfsmL3HGAhKbLHcGOzrdD7z4VNbCizds
   ```
6. Cliquer sur **"Create Web Service"**

### 3. Attendre le déploiement

- Premier déploiement : ~5-10 minutes
- Les logs s'affichent en temps réel
- Votre app sera accessible sur : `https://backoffice-location.onrender.com`

## 🧪 Tester en local avant de déployer

```powershell
# Build avec Maven
.\build-local.ps1

# Ou manuellement
cd BO-location
mvn clean package
```

## 🔧 Informations de connexion

- **Base de données** : `location_s5` sur Render
- **Hostname** : `dpg-d691dmsr85hc73d4f1ag-a.frankfurt-postgres.render.com`
- **Port** : `5432`
- **User** : `location`

## 📝 Fichiers importants créés

| Fichier | Description |
|---------|-------------|
| `Dockerfile` | Configuration Docker multi-stage |
| `BO-location/pom.xml` | Configuration Maven |
| `render.yaml` | Blueprint Render (optionnel) |
| `.dockerignore` | Fichiers à exclure du build Docker |
| `README.md` | Documentation complète |
| `build-local.ps1` | Script de build local |

## ⚠️ Important

- Ne commitez JAMAIS les mots de passe dans Git
- Les variables d'environnement sont gérées par Render
- Plan gratuit : app s'arrête après 15 min d'inactivité

## 🆘 En cas de problème

1. Vérifier les logs sur Render
2. Vérifier que les variables d'environnement sont correctes
3. Vérifier que la DB est accessible (External URL)
4. Consulter le README.md pour plus de détails

---
**Prêt à déployer !** 🎉
