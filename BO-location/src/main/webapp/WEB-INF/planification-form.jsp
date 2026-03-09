<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Planification - Sélection de date</title>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            margin: 0;
            padding: 20px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
        }
        .container {
            background-color: white;
            border-radius: 10px;
            padding: 40px;
            box-shadow: 0 10px 25px rgba(0,0,0,0.2);
            max-width: 500px;
            width: 100%;
        }
        h1 {
            color: #333;
            margin-bottom: 30px;
            text-align: center;
            font-size: 28px;
        }
        .form-group {
            margin-bottom: 25px;
        }
        label {
            display: block;
            margin-bottom: 8px;
            color: #555;
            font-weight: 600;
            font-size: 14px;
        }
        input[type="date"] {
            width: 100%;
            padding: 12px;
            border: 2px solid #e0e0e0;
            border-radius: 5px;
            font-size: 16px;
            transition: border-color 0.3s;
            box-sizing: border-box;
        }
        input[type="date"]:focus {
            outline: none;
            border-color: #667eea;
        }
        .button-group {
            display: flex;
            gap: 10px;
            margin-top: 30px;
        }
        button {
            flex: 1;
            padding: 14px;
            background-color: #667eea;
            color: white;
            border: none;
            border-radius: 5px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: background-color 0.3s, transform 0.1s;
        }
        button:hover {
            background-color: #5568d3;
            transform: translateY(-2px);
        }
        button:active {
            transform: translateY(0);
        }
        .back-link {
            display: inline-block;
            margin-bottom: 20px;
            color: #667eea;
            text-decoration: none;
            font-weight: 600;
            transition: color 0.3s;
        }
        .back-link:hover {
            color: #5568d3;
        }
        .info-box {
            background-color: #f0f4ff;
            border-left: 4px solid #667eea;
            padding: 15px;
            margin-bottom: 25px;
            border-radius: 4px;
        }
        .info-box p {
            margin: 0;
            color: #555;
            font-size: 14px;
        }
    </style>
</head>
<body>
    <div class="container">
        <a href="<%= request.getContextPath() %>/" class="back-link">← Retour à l'accueil</a>
        
        <h1>📅 Planification des Véhicules</h1>
        
        <div class="info-box">
            <p>Sélectionnez une date pour voir la planification des affectations véhicule-réservation.</p>
        </div>
        
        <form action="<%= request.getContextPath() %>/planification" method="get">
            <div class="form-group">
                <label for="date">Date de planification :</label>
                <input type="date" 
                       id="date" 
                       name="date" 
                       value="<%= request.getAttribute("dateAujourdhui") != null ? request.getAttribute("dateAujourdhui") : "" %>"
                       required>
            </div>
            
            <div class="button-group">
                <button type="submit">Voir la planification</button>
            </div>
        </form>
    </div>
</body>
</html>
