<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Planification — Sélection de date</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Syne:wght@600;700;800&family=DM+Sans:wght@300;400;500&display=swap" rel="stylesheet">
    <style>
        :root {
            --navy-950:  #060d1a;
            --navy-900:  #0b1628;
            --navy-800:  #0f2040;
            --navy-700:  #163058;
            --accent:    #3b82f6;
            --accent-dim: rgba(59,130,246,0.18);
            --gold:      #c9a84c;
            --gold-soft: rgba(201,168,76,0.1);
            --text-primary:   #e8edf5;
            --text-secondary: #8fa3be;
            --text-muted:     #4a6080;
            --border:    rgba(59,130,246,0.15);
            --glass:     rgba(11,22,40,0.75);
        }

        *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }

        body {
            font-family: 'DM Sans', sans-serif;
            background-color: var(--navy-950);
            color: var(--text-primary);
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 40px 20px;
        }

        .bg-layer {
            position: fixed;
            inset: 0;
            z-index: 0;
            background:
                radial-gradient(ellipse 70% 60% at 15% 25%, rgba(30,68,120,0.4) 0%, transparent 60%),
                radial-gradient(ellipse 50% 50% at 80% 70%, rgba(38,93,156,0.25) 0%, transparent 55%),
                var(--navy-950);
        }
        .bg-grid {
            position: fixed;
            inset: 0;
            z-index: 0;
            background-image:
                linear-gradient(rgba(59,130,246,0.04) 1px, transparent 1px),
                linear-gradient(90deg, rgba(59,130,246,0.04) 1px, transparent 1px);
            background-size: 60px 60px;
            mask-image: radial-gradient(ellipse 90% 90% at center, black 40%, transparent 100%);
        }

        .wrapper {
            position: relative;
            z-index: 1;
            width: 100%;
            max-width: 480px;
            animation: fadeUp 0.6s ease both;
        }

        .back-link {
            display: inline-flex;
            align-items: center;
            gap: 8px;
            color: var(--text-muted);
            text-decoration: none;
            font-size: 13px;
            font-weight: 500;
            letter-spacing: 0.02em;
            margin-bottom: 28px;
            transition: color 0.2s;
        }
        .back-link:hover { color: var(--accent); }
        .back-link::before {
            content: '←';
            font-size: 14px;
        }

        .card {
            background: var(--glass);
            border: 1px solid var(--border);
            border-radius: 20px;
            padding: 40px 36px;
            backdrop-filter: blur(14px);
            -webkit-backdrop-filter: blur(14px);
        }

        .card-header {
            margin-bottom: 32px;
        }
        .card-label {
            display: inline-flex;
            align-items: center;
            gap: 8px;
            background: var(--gold-soft);
            border: 1px solid rgba(201,168,76,0.25);
            color: var(--gold);
            font-family: 'Syne', sans-serif;
            font-size: 10px;
            font-weight: 700;
            letter-spacing: 0.2em;
            text-transform: uppercase;
            padding: 5px 14px;
            border-radius: 100px;
            margin-bottom: 18px;
        }
        .card-label::before {
            content: '';
            width: 5px; height: 5px;
            background: var(--gold);
            border-radius: 50%;
            animation: pulse 2s infinite;
        }
        h1 {
            font-family: 'Syne', sans-serif;
            font-size: 26px;
            font-weight: 800;
            letter-spacing: -0.03em;
            color: var(--text-primary);
            margin-bottom: 8px;
        }
        .card-subtitle {
            font-size: 13px;
            font-weight: 300;
            color: var(--text-secondary);
            line-height: 1.5;
        }

        .info-box {
            background: rgba(59,130,246,0.07);
            border: 1px solid rgba(59,130,246,0.18);
            border-radius: 10px;
            padding: 14px 16px;
            margin-bottom: 28px;
        }
        .info-box p {
            font-size: 13px;
            font-weight: 300;
            color: var(--text-secondary);
            line-height: 1.55;
        }

        .form-group {
            margin-bottom: 28px;
        }
        label {
            display: block;
            font-size: 11px;
            font-weight: 600;
            font-family: 'Syne', sans-serif;
            letter-spacing: 0.12em;
            text-transform: uppercase;
            color: var(--text-muted);
            margin-bottom: 10px;
        }

        input[type="date"] {
            width: 100%;
            padding: 13px 16px;
            background: rgba(6,13,26,0.6);
            border: 1px solid var(--border);
            border-radius: 10px;
            color: var(--text-primary);
            font-family: 'DM Sans', sans-serif;
            font-size: 15px;
            font-weight: 400;
            transition: border-color 0.25s, box-shadow 0.25s;
            appearance: none;
            -webkit-appearance: none;
            cursor: pointer;
        }
        input[type="date"]:focus {
            outline: none;
            border-color: rgba(59,130,246,0.5);
            box-shadow: 0 0 0 3px rgba(59,130,246,0.12);
        }
        input[type="date"]::-webkit-calendar-picker-indicator {
            filter: invert(0.5) sepia(1) saturate(3) hue-rotate(180deg);
            cursor: pointer;
            opacity: 0.7;
        }
        input[type="date"]::-webkit-calendar-picker-indicator:hover {
            opacity: 1;
        }

        .btn-submit {
            width: 100%;
            padding: 14px 20px;
            background: linear-gradient(135deg, var(--navy-700) 0%, var(--accent) 100%);
            color: white;
            border: none;
            border-radius: 10px;
            font-family: 'Syne', sans-serif;
            font-size: 14px;
            font-weight: 700;
            letter-spacing: 0.04em;
            cursor: pointer;
            transition: opacity 0.2s, transform 0.2s, box-shadow 0.2s;
            position: relative;
            overflow: hidden;
        }
        .btn-submit::after {
            content: '';
            position: absolute;
            inset: 0;
            background: linear-gradient(135deg, rgba(255,255,255,0.06) 0%, transparent 60%);
        }
        .btn-submit:hover {
            opacity: 0.9;
            transform: translateY(-2px);
            box-shadow: 0 8px 24px rgba(59,130,246,0.3);
        }
        .btn-submit:active {
            transform: translateY(0);
        }

        @keyframes fadeUp {
            from { opacity: 0; transform: translateY(18px); }
            to   { opacity: 1; transform: translateY(0); }
        }
        @keyframes pulse {
            0%, 100% { opacity: 1; }
            50%       { opacity: 0.35; }
        }
    </style>
</head>
<body>

    <div class="bg-layer"></div>
    <div class="bg-grid"></div>

    <div class="wrapper">

        <a href="<%= request.getContextPath() %>/" class="back-link">Retour à l'accueil</a>

        <div class="card">

            <div class="card-header">
                <div class="card-label">Planification</div>
                <h1>Sélection de date</h1>
                <p class="card-subtitle">Choisissez une date pour afficher les affectations véhicule-réservation.</p>
            </div>

            <div class="info-box">
                <p>Sélectionnez une date pour voir la planification des affectations véhicule-réservation.</p>
            </div>

            <form action="<%= request.getContextPath() %>/planification" method="get">
                <div class="form-group">
                    <label for="date">Date de planification</label>
                    <input type="date"
                           id="date"
                           name="date"
                           value="<%= request.getAttribute("dateAujourdhui") != null ? request.getAttribute("dateAujourdhui") : "" %>"
                           required>
                </div>
                <button type="submit" class="btn-submit">Voir la planification</button>
            </form>

        </div>

    </div>
</body>
</html>
