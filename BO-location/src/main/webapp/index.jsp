<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>BO-location - Back Office</title>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            margin: 0;
            padding: 0;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
        }
        .container {
            background-color: white;
            border-radius: 15px;
            padding: 50px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.3);
            max-width: 600px;
            width: 90%;
        }
        h1 {
            color: #333;
            margin-bottom: 10px;
            font-size: 36px;
            text-align: center;
        }
        .subtitle {
            color: #666;
            text-align: center;
            margin-bottom: 40px;
            font-size: 16px;
        }
        .menu {
            display: flex;
            flex-direction: column;
            gap: 15px;
        }
        .menu-item {
            display: flex;
            align-items: center;
            padding: 20px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            text-decoration: none;
            border-radius: 10px;
            transition: transform 0.3s, box-shadow 0.3s;
            font-size: 18px;
            font-weight: 600;
        }
        .menu-item:hover {
            transform: translateY(-3px);
            box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
        }
        .menu-item .icon {
            font-size: 24px;
            margin-right: 15px;
        }
        .menu-item .description {
            font-size: 14px;
            font-weight: normal;
            opacity: 0.9;
            margin-top: 5px;
        }
        .menu-item-content {
            display: flex;
            flex-direction: column;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1> BO-location</h1>
        <p class="subtitle">Back Office - Gestion des reservations et vehicules</p>
        
        <div class="menu">
            <a href="/reservations/form" class="menu-item">
                <span class="icon"></span>
                <div class="menu-item-content">
                    <span>Ajouter une reservation</span>
                    <span class="description">Créer une nouvelle reservation client</span>
                </div>
            </a>
            
            <a href="/reservations/liste" class="menu-item">
                <span class="icon"></span>
                <div class="menu-item-content">
                    <span>Liste des reservations</span>
                    <span class="description">Voir toutes les reservations</span>
                </div>
            </a>
            
            <a href="/planification/form" class="menu-item">
                <span class="icon"></span>
                <div class="menu-item-content">
                    <span>Planification des vehicules</span>
                    <span class="description">Affectation automatique des vehicules aux reservations</span>
                </div>
            </a>
            
            <a href="/voitures/form" class="menu-item">
                <span class="icon"></span>
                <div class="menu-item-content">
                    <span>Gestion des vehicules</span>
                    <span class="description">Ajouter ou gerer les vehicules</span>
                </div>
            </a>
        </div>
    </div>
</body>
</html>
