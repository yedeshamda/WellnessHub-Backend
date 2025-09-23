# WellnessHub-Backend
## 🚀 Project Setup Guide

Bienvenue dans votre environnement de développement ! Suivez ces étapes pour configurer vos outils et commencer à créer des projets exceptionnels. Ce guide est optimisé pour les utilisateurs de IntelliJ IDEA et s’adresse à ceux qui travaillent sur un projet backend moderne.


---

## 🧰 Prérequis

Avant de commencer, assurez-vous d’avoir :

- ✅ Un système d’exploitation compatible (Windows, macOS, Linux)
- ✅ Droits d’administrateur pour les installations
- ✅ IntelliJ IDEA installé ([Télécharger ici](https://www.jetbrains.com/idea/download/))

---

## 📦 Étapes d'installation

### 1. 🐳 Installer Docker Desktop + Docker Compose  
*Lien de tâche : [WEHUB-7](#)*  
- Téléchargez Docker Desktop : [docker.com](https://www.docker.com/products/docker-desktop)  
- Vérifiez l’installation avec docker --version et docker-compose --version

### 2. 🐘 Installer PostgreSQL local et tester la connexion  
*Lien de tâche : [WEHUB-6](#)*  
- Téléchargez PostgreSQL : [postgresql.org](https://www.postgresql.org/download/)  
- Créez une base de test et connectez-vous via IntelliJ ou pgAdmin  
- Vérifiez avec psql -U postgres

### 3. 🛠 Installer les IDEs  
*Lien de tâche : [WEHUB-5](#)*  
- Installez [IntelliJ IDEA](https://www.jetbrains.com/idea/)  
- (Optionnel) VS Code pour édition rapide : [code.visualstudio.com](https://code.visualstudio.com/)

### 4. 🔐 Configurer GitHub Desktop et accès SSH  
*Lien de tâche : [WEHUB-4](#)*  
- Téléchargez GitHub Desktop : [desktop.github.com](https://desktop.github.com/)  
- Générez une clé SSH : ssh-keygen -t ed25519 -C "votre_email@example.com"  
- Ajoutez la clé à GitHub : [github.com/settings/keys](https://github.com/settings/keys)

### 5. 🌐 Créer comptes GitHub et configurer projet Git  
*Lien de tâche : [WEHUB-3](#)*  
- Créez un compte GitHub : [github.com/join](https://github.com/join)  
- Clonez votre projet : git clone git@github.com:username/repo.git  
- Configurez le remote et les branches dans IntelliJ

---

## 🧪 Vérification

Une fois tout installé :

- ✅ Docker fonctionne et peut lancer des containers
- ✅ PostgreSQL est accessible localement
- ✅ IntelliJ est prêt avec Git intégré
- ✅ Connexion SSH à GitHub réussie

---

## 💡 Astuces IntelliJ

- Utilisez le *Terminal intégré* pour lancer vos commandes
- Activez *Git Integration* dans les paramètres pour un suivi visuel
- Installez les plugins utiles : Docker, Database Navigator, GitHub Copilot

---

## 📞 Support

En cas de problème, contactez votre référent technique ou ouvrez une issue sur le repo GitHub.

---

> 🧠 Ce guide est conçu pour vous faire gagner du temps et éviter les pièges classiques. Bonne configuration !
