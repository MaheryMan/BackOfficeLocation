<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.LinkedHashMap" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="model.Planification" %>
<%@ page import="model.Reservation" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Planification du <%= request.getAttribute("date") %></title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@300;400;500;600;700;800&family=Space+Grotesk:wght@400;500;600;700&display=swap" rel="stylesheet">
    <style>
        :root {
            --bg:          #06111e;
            --surface:     #0b1a2b;
            --surface2:    #0f2238;
            --surface3:    #132940;
            --border:      rgba(99,179,255,0.10);
            --border2:     rgba(99,179,255,0.20);
            --accent:      #4e9eff;
            --accent-dim:  rgba(78,158,255,0.10);
            --gold:        #f0b429;
            --gold-dim:    rgba(240,180,41,0.10);
            --danger:      #f56565;
            --danger-dim:  rgba(245,101,101,0.10);
            --success:     #48bb78;
            --success-dim: rgba(72,187,120,0.10);
            --warning:     #ed8936;
            --warning-dim: rgba(237,137,54,0.10);
            --txt:         #dde8f4;
            --txt2:        #6e90b2;
            --txt3:        #304d69;
            --r:           12px;
            --rs:          8px;
            --fh: 'Space Grotesk', sans-serif;
            --fb: 'Plus Jakarta Sans', sans-serif;
        }
        *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }
        body { font-family: var(--fb); background: var(--bg); color: var(--txt); min-height: 100vh; padding: 40px 20px 80px; }

        .bg-gradient { position:fixed; inset:0; z-index:0; pointer-events:none; background: radial-gradient(ellipse 65% 55% at 10% 0%, rgba(18,55,100,.50) 0%, transparent 65%), radial-gradient(ellipse 40% 35% at 88% 95%, rgba(25,70,130,.25) 0%, transparent 60%), var(--bg); }
        .bg-lines    { position:fixed; inset:0; z-index:0; pointer-events:none; background-image: linear-gradient(rgba(99,179,255,.025) 1px,transparent 1px), linear-gradient(90deg,rgba(99,179,255,.025) 1px,transparent 1px); background-size:48px 48px; mask-image:radial-gradient(ellipse 80% 80% at center,black 25%,transparent 100%); }
        .page { position:relative; z-index:1; max-width:980px; margin:0 auto; }

        /* ── Nav ── */
        .nav-top { margin-bottom:28px; animation:fadeDown .4s ease both; }
        .back-link { display:inline-flex; align-items:center; gap:7px; color:var(--txt3); text-decoration:none; font-family:var(--fh); font-size:12px; font-weight:500; letter-spacing:.03em; transition:color .2s; }
        .back-link:hover { color:var(--accent); }
        .ico { display:inline-block; width:14px; height:14px; vertical-align:middle; flex-shrink:0; }

        /* ── Header ── */
        .page-header { margin-bottom:24px; animation:fadeDown .45s ease both; }
        .eyebrow { display:inline-flex; align-items:center; gap:6px; background:var(--gold-dim); border:1px solid rgba(240,180,41,.20); color:var(--gold); font-family:var(--fh); font-size:9px; font-weight:700; letter-spacing:.25em; text-transform:uppercase; padding:4px 12px 4px 10px; border-radius:100px; margin-bottom:14px; }
        h1 { font-family:var(--fh); font-size:clamp(22px,3.5vw,32px); font-weight:700; letter-spacing:-.02em; color:var(--txt); margin-bottom:5px; }
        .subline { font-size:13px; color:var(--txt2); }
        .subline strong { color:var(--accent); font-weight:600; }

        /* ── Stats ── */
        .stats-row { display:grid; grid-template-columns:repeat(3,1fr); gap:10px; margin-bottom:20px; animation:fadeUp .45s .07s ease both; }
        @media(max-width:480px){ .stats-row { grid-template-columns:1fr 1fr; } }
        .stat { background:var(--surface); border:1px solid var(--border); border-radius:var(--r); padding:16px 16px 14px; display:flex; flex-direction:column; gap:8px; }
        .stat-top { display:flex; align-items:center; justify-content:space-between; }
        .stat-icon { width:34px; height:34px; border-radius:var(--rs); display:flex; align-items:center; justify-content:center; }
        .si-blue  { background:var(--accent-dim);  color:var(--accent);  }
        .si-green { background:var(--success-dim); color:var(--success); }
        .si-red   { background:var(--danger-dim);  color:var(--danger);  }
        .stat-num { font-family:var(--fh); font-size:28px; font-weight:700; color:var(--txt); line-height:1; }
        .stat-lbl { font-size:11px; color:var(--txt3); text-transform:uppercase; letter-spacing:.09em; font-weight:600; }

        /* ── Alert ── */
        .alert { display:flex; align-items:center; gap:10px; background:var(--warning-dim); border:1px solid rgba(237,137,54,.22); border-left:3px solid var(--warning); border-radius:var(--rs); padding:11px 16px; margin-bottom:20px; font-size:13px; color:#f6ad55; animation:fadeUp .45s .1s ease both; }
        .alert strong { font-weight:700; color:#fbd38d; }

        /* ── Section label ── */
        .sec-label { display:flex; align-items:center; gap:9px; margin-bottom:12px; animation:fadeUp .45s .12s ease both; }
        .sec-label h2 { font-family:var(--fh); font-size:11px; font-weight:600; color:var(--txt3); text-transform:uppercase; letter-spacing:.12em; }
        .sec-label .pill { font-family:var(--fh); font-size:10px; font-weight:600; color:var(--txt3); background:rgba(255,255,255,.04); border:1px solid var(--border); border-radius:100px; padding:2px 8px; }
        .sec-rule { height:1px; background:var(--border); margin-bottom:16px; }

        /* ══ Carte véhicule ══ */
        .car-group { margin-bottom:12px; border:1px solid var(--border); border-radius:var(--r); background:var(--surface); overflow:hidden; animation:fadeUp .4s ease both; transition:border-color .2s; }
        .car-group:hover { border-color:var(--border2); }
        .car-group:nth-child(2){animation-delay:.04s}.car-group:nth-child(3){animation-delay:.08s}.car-group:nth-child(n+4){animation-delay:.12s}

        .car-header { display:flex; align-items:center; gap:12px; padding:14px 18px; background:var(--surface2); border-bottom:1px solid var(--border); cursor:pointer; user-select:none; transition:background .15s; }
        .car-header:hover { background:var(--surface3); }

        .car-avatar { width:42px; height:42px; border-radius:10px; flex-shrink:0; display:flex; align-items:center; justify-content:center; }
        .av-diesel  { background:var(--success-dim); color:var(--success); }
        .av-essence { background:var(--warning-dim); color:var(--warning); }
        .av-autre   { background:var(--accent-dim);  color:var(--accent);  }
        .car-avatar .ico { width:22px; height:22px; }

        .car-info { flex:1; min-width:0; }
        .car-numero { font-family:var(--fh); font-size:16px; font-weight:700; color:var(--txt); }
        .car-meta { display:flex; align-items:center; gap:6px; margin-top:4px; flex-wrap:wrap; }
        .car-meta-item { display:inline-flex; align-items:center; gap:4px; font-size:12px; color:var(--txt2); }
        .car-meta-item .ico { width:12px; height:12px; opacity:.65; }
        .meta-dot { width:3px; height:3px; background:var(--txt3); border-radius:50%; }

        .car-badges { display:flex; align-items:center; gap:7px; margin-left:auto; flex-shrink:0; flex-wrap:wrap; justify-content:flex-end; }
        .badge { display:inline-flex; align-items:center; gap:4px; padding:3px 9px; border-radius:100px; font-family:var(--fh); font-size:10px; font-weight:600; letter-spacing:.04em; white-space:nowrap; border:1px solid transparent; }
        .badge .ico { width:10px; height:10px; }
        .b-diesel  { background:var(--success-dim); color:var(--success); border-color:rgba(72,187,120,.18); }
        .b-essence { background:var(--warning-dim); color:var(--warning); border-color:rgba(237,137,54,.18); }
        .b-autre   { background:var(--accent-dim);  color:var(--accent);  border-color:rgba(78,158,255,.18); }
        .b-neutral { background:rgba(255,255,255,.04); color:var(--txt2); border-color:var(--border); }
        .b-accent  { background:var(--accent-dim); color:var(--accent); border-color:rgba(78,158,255,.18); }
        .b-resa    { background:rgba(255,255,255,.06); color:var(--txt2); border-color:var(--border); font-size:11px; padding:3px 10px; }

        .car-toggle { width:28px; height:28px; border-radius:7px; display:flex; align-items:center; justify-content:center; background:rgba(255,255,255,.04); border:1px solid var(--border); color:var(--txt3); flex-shrink:0; }
        .car-toggle .ico { width:14px; height:14px; transition:transform .25s; }
        .car-group.open .car-toggle .ico { transform:rotate(180deg); }
        .car-body { display:none; }
        .car-group.open .car-body { display:block; }

        /* ── Groupe heure de départ ── */
        .dep-group { border-bottom:1px solid var(--border); }
        .dep-group:last-child { border-bottom:none; }
        .dep-header { display:flex; align-items:center; gap:9px; padding:9px 18px; background:rgba(78,158,255,.04); border-bottom:1px solid var(--border); }
        .dep-header .ico { width:13px; height:13px; color:var(--accent); }
        .dep-time { font-family:var(--fh); font-size:13px; font-weight:700; color:var(--accent); }
        .dep-sep  { width:1px; height:12px; background:var(--border2); }
        .dep-lbl  { font-size:10px; color:var(--txt3); font-weight:600; letter-spacing:.1em; text-transform:uppercase; }
        .dep-count { margin-left:auto; font-size:10px; font-weight:600; color:var(--txt3); background:rgba(255,255,255,.04); border:1px solid var(--border); border-radius:100px; padding:2px 8px; }

        /* ══ Carte réservation (layout vertical lisible) ══ */
        .resa-list { list-style:none; }

        .resa-card {
            display:grid;
            grid-template-columns: auto 1fr auto;
            grid-template-rows: auto auto auto;
            gap:0;
            padding:16px 18px;
            border-bottom:1px solid rgba(99,179,255,.05);
            transition:background .12s;
        }
        .resa-card:last-child { border-bottom:none; }
        .resa-card:hover { background:rgba(78,158,255,.04); }

        /* Colonne gauche : badge ID */
        .rc-id { grid-column:1; grid-row:1/4; padding-right:16px; padding-top:2px; }
        .resa-num {
            display:flex; flex-direction:column; align-items:center; gap:3px;
            background:rgba(255,255,255,.05); border:1px solid var(--border);
            border-radius:var(--rs); padding:8px 10px; min-width:52px; text-align:center;
        }
        .resa-num .ico { width:13px; height:13px; color:var(--txt3); }
        .resa-num-label { font-size:9px; font-weight:700; color:var(--txt3); text-transform:uppercase; letter-spacing:.08em; }
        .resa-num-val   { font-family:var(--fh); font-size:14px; font-weight:700; color:var(--txt); }

        /* Colonne centrale : infos */
        .rc-main { grid-column:2; grid-row:1; padding-bottom:10px; }
        .rc-route { grid-column:2; grid-row:2; padding-bottom:10px; }
        .rc-footer{ grid-column:2; grid-row:3; }

        /* Colonne droite : horaires */
        .rc-times { grid-column:3; grid-row:1/4; display:flex; flex-direction:column; align-items:flex-end; justify-content:center; gap:8px; padding-left:16px; border-left:1px solid var(--border); }

        /* Responsive */
        @media(max-width:640px) {
            .resa-card { grid-template-columns:auto 1fr; grid-template-rows:auto auto auto auto; }
            .rc-id    { grid-column:1; grid-row:1; padding-right:12px; padding-bottom:12px; }
            .rc-main  { grid-column:2; grid-row:1; }
            .rc-route { grid-column:1/-1; grid-row:2; padding-bottom:8px; }
            .rc-footer{ grid-column:1/-1; grid-row:3; padding-bottom:8px; }
            .rc-times { grid-column:1/-1; grid-row:4; flex-direction:row; align-items:center; justify-content:flex-start; border-left:none; border-top:1px solid var(--border); padding-left:0; padding-top:10px; gap:16px; }
        }

        /* Nom client */
        .client-name { font-size:15px; font-weight:700; color:var(--txt); display:flex; align-items:center; gap:7px; margin-bottom:4px; }
        .client-name .ico { width:14px; height:14px; color:var(--txt3); }

        /* Ligne de route : prise en charge → destination */
        .route-line {
            display:flex; align-items:stretch; gap:0;
            background:var(--surface2); border:1px solid var(--border);
            border-radius:var(--rs); overflow:hidden;
            font-size:12px;
        }
        .route-origin, .route-dest {
            display:flex; align-items:center; gap:6px;
            padding:8px 12px; flex:1; min-width:0;
        }
        .route-origin { border-right:1px solid var(--border); }
        .route-origin .ico { width:12px; height:12px; color:var(--warning); flex-shrink:0; }
        .route-dest   .ico { width:12px; height:12px; color:var(--success); flex-shrink:0; }
        .route-block { display:flex; flex-direction:column; gap:1px; min-width:0; }
        .route-lbl  { font-size:9px; font-weight:700; color:var(--txt3); text-transform:uppercase; letter-spacing:.09em; }
        .route-name { font-size:12px; font-weight:600; color:var(--txt); white-space:nowrap; overflow:hidden; text-overflow:ellipsis; }
        .route-dist { font-size:10px; color:var(--txt3); display:flex; align-items:center; gap:3px; margin-top:1px; }
        .route-dist .ico { width:9px; height:9px; opacity:.5; }
        .route-arrow {
            display:flex; align-items:center; padding:0 8px;
            color:var(--txt3); background:var(--surface3); flex-shrink:0;
        }
        .route-arrow .ico { width:12px; height:12px; }

        /* Footer : passagers */
        .pax-info {
            display:inline-flex; align-items:center; gap:8px;
            background:var(--surface2); border:1px solid var(--border);
            border-radius:var(--rs); padding:6px 12px;
            font-size:12px;
        }
        .pax-info .ico { width:13px; height:13px; flex-shrink:0; }
        .pax-count { font-family:var(--fh); font-weight:700; font-size:13px; }
        .pax-sep  { width:1px; height:12px; background:var(--border2); }
        .pax-detail { font-size:11px; color:var(--txt2); }
        .pax-ok   { color:var(--accent); }
        .pax-part { color:var(--warning); }
        .pax-over { color:var(--danger); }
        .pax-tag  {
            display:inline-flex; align-items:center; gap:4px;
            font-size:10px; font-weight:700; padding:2px 7px;
            border-radius:100px; border:1px solid;
        }
        .pax-tag.ok   { background:var(--accent-dim);  color:var(--accent);  border-color:rgba(78,158,255,.2); }
        .pax-tag.part { background:var(--warning-dim); color:var(--warning); border-color:rgba(237,137,54,.2); }
        .pax-tag.over { background:var(--danger-dim);  color:var(--danger);  border-color:rgba(245,101,101,.2); }

        /* Bloc horaires (colonne droite) */
        .time-block {
            display:flex; flex-direction:column; align-items:flex-end; gap:1px;
        }
        .time-block-lbl { font-size:9px; font-weight:700; color:var(--txt3); text-transform:uppercase; letter-spacing:.09em; }
        .time-block-val { font-family:var(--fh); font-size:15px; font-weight:700; color:var(--txt); }
        .time-block-sub { font-size:10px; color:var(--txt3); }

        /* ══ Sans voiture ══ */
        .no-car-section { margin-top:26px; animation:fadeUp .45s .2s ease both; }
        .no-car-card { display:grid; grid-template-columns:auto 1fr auto; align-items:start; gap:0; padding:14px 18px; background:var(--surface); border:1px solid rgba(245,101,101,.15); border-left:3px solid var(--danger); border-radius:var(--rs); margin-bottom:8px; transition:background .15s; }
        .no-car-card:hover { background:var(--danger-dim); }
        .nc-icon { width:38px; height:38px; border-radius:var(--rs); background:var(--danger-dim); color:var(--danger); display:flex; align-items:center; justify-content:center; margin-right:14px; flex-shrink:0; }
        .nc-icon .ico { width:18px; height:18px; }
        .nc-lbl  { font-size:9px; font-weight:700; color:var(--danger); text-transform:uppercase; letter-spacing:.09em; margin-bottom:3px; }
        .nc-client { font-size:14px; font-weight:700; color:var(--txt); margin-bottom:4px; }
        .nc-hotel  { display:flex; align-items:center; gap:5px; font-size:12px; color:var(--txt2); }
        .nc-hotel .ico { width:11px; height:11px; opacity:.6; }
        .nc-right { display:flex; flex-direction:column; align-items:flex-end; gap:5px; padding-left:14px; border-left:1px solid var(--border); }
        .nc-time  { display:flex; flex-direction:column; align-items:flex-end; }
        .nc-time-lbl { font-size:9px; font-weight:700; color:var(--txt3); text-transform:uppercase; letter-spacing:.09em; }
        .nc-time-val { font-family:var(--fh); font-size:16px; font-weight:700; color:var(--danger); }
        .nc-pax   { display:flex; align-items:center; gap:4px; font-size:12px; font-weight:600; color:var(--txt3); }
        .nc-pax .ico { width:11px; height:11px; opacity:.6; }

        /* ── Empty / Actions ── */
        .empty { text-align:center; padding:50px 20px; color:var(--txt3); font-size:14px; display:flex; flex-direction:column; align-items:center; gap:12px; }
        .empty .ico { width:32px; height:32px; opacity:.4; }
        .actions { display:flex; gap:10px; justify-content:center; margin-top:34px; flex-wrap:wrap; animation:fadeUp .45s .28s ease both; }
        .btn { display:inline-flex; align-items:center; gap:7px; padding:10px 22px; border-radius:var(--rs); font-family:var(--fh); font-size:12px; font-weight:600; letter-spacing:.04em; text-decoration:none; transition:transform .2s, box-shadow .2s, opacity .2s; }
        .btn .ico { width:14px; height:14px; }
        .btn-primary { background:linear-gradient(135deg,#0d3060 0%,#1a5fc8 100%); color:#fff; border:1px solid rgba(78,158,255,.25); }
        .btn-primary:hover { opacity:.88; transform:translateY(-2px); box-shadow:0 8px 22px rgba(78,158,255,.2); }
        .btn-ghost  { background:transparent; color:var(--txt2); border:1px solid var(--border); }
        .btn-ghost:hover { color:var(--txt); border-color:var(--border2); transform:translateY(-1px); }

        @keyframes fadeDown { from{opacity:0;transform:translateY(-10px)} to{opacity:1;transform:translateY(0)} }
        @keyframes fadeUp   { from{opacity:0;transform:translateY(12px)}  to{opacity:1;transform:translateY(0)} }
    </style>
</head>
<body>

<!-- ═══ SPRITE SVG ═══ -->
<svg xmlns="http://www.w3.org/2000/svg" style="display:none">
  <symbol id="i-back"   viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="15 18 9 12 15 6"/></symbol>
  <symbol id="i-cal"    viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="4" width="18" height="18" rx="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></symbol>
  <symbol id="i-car"    viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M5 17H3a2 2 0 0 1-2-2V9l2-4h14l2 4v6a2 2 0 0 1-2 2h-2"/><circle cx="8.5" cy="17" r="2"/><circle cx="15.5" cy="17" r="2"/><polyline points="3 9 21 9"/></symbol>
  <symbol id="i-bus"    viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M8 6v6"/><path d="M16 6v6"/><path d="M2 12h19.6"/><path d="M18 18h3s.5-1.7.8-2.8c.1-.4.2-.8.2-1.2 0-.4-.1-.8-.2-1.2L20 6c-.2-.9-1-1-2-1H4c-1 0-1.8.1-2 1L.2 15.8c-.1.4-.2.8-.2 1.2s.1.8.2 1.2C.5 16.3 1 18 1 18h3"/><circle cx="7" cy="18" r="2"/><circle cx="15" cy="18" r="2"/></symbol>
  <symbol id="i-users"  viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></symbol>
  <symbol id="i-user"   viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></symbol>
  <symbol id="i-alert"  viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/></symbol>
  <symbol id="i-clock"  viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></symbol>
  <symbol id="i-pin"    viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/><circle cx="12" cy="10" r="3"/></symbol>
  <symbol id="i-ruler"  viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21.3 8.7l-9-9a1 1 0 0 0-1.4 0L2.7 8.1a1 1 0 0 0 0 1.4l9 9a1 1 0 0 0 1.4 0l8.2-8.2a1 1 0 0 0 0-1.6z"/><path d="M7.5 12.5l4-4"/></symbol>
  <symbol id="i-hotel"  viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="2" y="7" width="20" height="14" rx="2"/><path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16"/></symbol>
  <symbol id="i-tag"    viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20.59 13.41l-7.17 7.17a2 2 0 0 1-2.83 0L2 12V2h10l8.59 8.59a2 2 0 0 1 0 2.82z"/><line x1="7" y1="7" x2="7.01" y2="7"/></symbol>
  <symbol id="i-chev"   viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="6 9 12 15 18 9"/></symbol>
  <symbol id="i-leaf"   viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M11 20A7 7 0 0 1 9.8 6.1C15.5 5 17 4.48 19 2c1 2 2 4.18 2 8 0 5.5-4.78 10-10 10z"/><path d="M2 21c0-3 1.85-5.36 5.08-6C9.5 14.52 12 13 13 12"/></symbol>
  <symbol id="i-flame"  viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M8.5 14.5A2.5 2.5 0 0 0 11 12c0-1.38-.5-2-1-3-1.072-2.143-.224-4.054 2-6 .5 2.5 2 4.9 4 6.5 2 1.6 3 3.5 3 5.5a7 7 0 1 1-14 0c0-1.153.433-2.294 1-3a2.5 2.5 0 0 0 2.5 2.5z"/></symbol>
  <symbol id="i-repeat" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="17 1 21 5 17 9"/><path d="M3 11V9a4 4 0 0 1 4-4h14"/><polyline points="7 23 3 19 7 15"/><path d="M21 13v2a4 4 0 0 1-4 4H3"/></symbol>
  <symbol id="i-nocar"  viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="1" y1="1" x2="23" y2="23"/><path d="M6.7 6.7H4a2 2 0 0 0-2 2v3a2 2 0 0 0 2 2h1"/><path d="M16.7 6.7 17 6h1l3 6"/><path d="M18 12h2a2 2 0 0 1 2 2v1a2 2 0 0 1-2 2h-1"/><circle cx="7" cy="17" r="2"/><circle cx="15" cy="17" r="2"/><path d="M9 17h4"/></symbol>
  <symbol id="i-home"   viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></symbol>
  <symbol id="i-arrow"  viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="5" y1="12" x2="19" y2="12"/><polyline points="12 5 19 12 12 19"/></symbol>
  <symbol id="i-plane"  viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17.8 19.2 16 11l3.5-3.5C21 6 21 4 19 4s-2 1-3.5 2.5L4 10.2l1.5.7L14 7l-2 5.5 2 2.3-2 4.8z"/></symbol>
  <symbol id="i-seat"   viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20.2 18a2 2 0 0 0 1.8-2v-2a2 2 0 0 0-2-2H6a2 2 0 0 0-2 2v2a2 2 0 0 0 1.8 2"/><path d="M2 9V7a2 2 0 0 1 2-2h16a2 2 0 0 1 2 2v2"/><path d="M4 18v2"/><path d="M20 18v2"/><path d="M12 4v5"/></symbol>
  <symbol id="i-ret"    viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="9 14 4 19 9 24"/><path d="M20 4v7a4 4 0 0 1-4 4H4"/></symbol>
</svg>

<div class="bg-gradient"></div>
<div class="bg-lines"></div>

<main class="page">

<%
    Object planificationsObj = request.getAttribute("planifications");
    List<?> planifications = (planificationsObj instanceof List) ? (List<?>) planificationsObj : null;
    Object reservationsSansVoitureObj = request.getAttribute("reservationsSansVoiture");
    List<?> reservationsSansVoiture = (reservationsSansVoitureObj instanceof List) ? (List<?>) reservationsSansVoitureObj : null;

    int totalPlanifications = planifications != null ? planifications.size() : 0;
    int totalSansVoiture    = reservationsSansVoiture != null ? reservationsSansVoiture.size() : 0;
    int totalPassagers = 0;

    Map<String, Map<String, List<Planification>>> grouped = new LinkedHashMap<>();
    Map<String, Planification> voitureRef = new LinkedHashMap<>();

    if (planifications != null) {
        for (Object item : planifications) {
            if (item instanceof Planification) {
                Planification p = (Planification) item;
                if (p.getVoiture() == null || p.getReservation() == null) continue;
                totalPassagers += p.getPassagersAffectes() != null ? p.getPassagersAffectes() : p.getReservation().getNombrePassager();
                String num  = p.getVoiture().getNumero();
                String hDep = p.getDateHeureDepart() != null ? p.getDateHeureDepart().substring(11,16) : "—";
                if (!grouped.containsKey(num))  { grouped.put(num, new LinkedHashMap<>()); voitureRef.put(num, p); }
                if (!grouped.get(num).containsKey(hDep)) grouped.get(num).put(hDep, new ArrayList<>());
                grouped.get(num).get(hDep).add(p);
            }
        }
    }
%>

    <!-- Nav -->
    <div class="nav-top">
        <a href="<%= request.getContextPath() %>/planification/form" class="back-link">
            <svg class="ico"><use href="#i-back"/></svg>Changer de date
        </a>
    </div>

    <!-- Header -->
    <div class="page-header">
        <div class="eyebrow">
            <svg class="ico"><use href="#i-cal"/></svg>Planification du jour
        </div>
        <h1>Affectations véhicules</h1>
        <p class="subline">Date&nbsp;: <strong><%= request.getAttribute("date") %></strong></p>
    </div>

    <!-- Stats -->
    <div class="stats-row">
        <div class="stat">
            <div class="stat-top"><div class="stat-icon si-blue"><svg class="ico" style="width:17px;height:17px"><use href="#i-car"/></svg></div></div>
            <div class="stat-num"><%= grouped.size() %></div>
            <div class="stat-lbl">Véhicules affectés</div>
        </div>
        <div class="stat">
            <div class="stat-top"><div class="stat-icon si-green"><svg class="ico" style="width:17px;height:17px"><use href="#i-users"/></svg></div></div>
            <div class="stat-num"><%= totalPassagers %></div>
            <div class="stat-lbl">Passagers pris en charge</div>
        </div>
        <div class="stat">
            <div class="stat-top"><div class="stat-icon <%= totalSansVoiture > 0 ? "si-red" : "si-green" %>"><svg class="ico" style="width:17px;height:17px"><use href="#i-alert"/></svg></div></div>
            <div class="stat-num" style="color:<%= totalSansVoiture > 0 ? "var(--danger)" : "var(--success)" %>"><%= totalSansVoiture %></div>
            <div class="stat-lbl">Réservations sans véhicule</div>
        </div>
    </div>

    <% if (totalSansVoiture > 0) { %>
    <div class="alert">
        <svg class="ico"><use href="#i-alert"/></svg>
        <span><strong><%= totalSansVoiture %> réservation<%= totalSansVoiture > 1 ? "s" : "" %></strong> n'ont pas pu être affectées à un véhicule faute de capacité disponible.</span>
    </div>
    <% } %>

    <!-- Titre section -->
    <div class="sec-label">
        <h2>Missions du jour — par véhicule &amp; heure de départ</h2>
        <span class="pill"><%= totalPlanifications %> affectation<%= totalPlanifications > 1 ? "s" : "" %></span>
    </div>
    <div class="sec-rule"></div>

    <!-- Véhicules -->
    <% if (grouped.isEmpty()) { %>
    <div class="empty">
        <svg class="ico"><use href="#i-car"/></svg>
        Aucune planification pour cette date.
    </div>
    <% } else {
       int ci = 0;
       for (Map.Entry<String, Map<String, List<Planification>>> carE : grouped.entrySet()) {
           String num = carE.getKey();
           Planification ref = voitureRef.get(num);
           boolean isDiesel  = ref.getVoiture().estDiesel();
           String typeE      = ref.getVoiture().getTypeEnergie() != null ? ref.getVoiture().getTypeEnergie().getLibelle() : "N/A";
           int capa          = ref.getVoiture().getCapacite();
           int paxTot = 0;
           for (List<Planification> ls : carE.getValue().values())
               for (Planification pp : ls)
                   paxTot += pp.getPassagersAffectes() != null ? pp.getPassagersAffectes() : pp.getReservation().getNombrePassager();
           int nbMissions = carE.getValue().size();
           int nbArretsTotal = carE.getValue().values().stream().mapToInt(List::size).sum();
           String aCls  = isDiesel ? "av-diesel"  : (typeE.equalsIgnoreCase("essence") ? "av-essence" : "av-autre");
           String bCls  = isDiesel ? "b-diesel"   : (typeE.equalsIgnoreCase("essence") ? "b-essence"  : "b-autre");
           String iCar  = isDiesel ? "i-bus"  : "i-car";
           String iFuel = isDiesel ? "i-leaf" : "i-flame";
    %>
    <div class="car-group open" id="car-<%= ci %>">
        <div class="car-header" onclick="toggleCar(<%= ci %>)">
            <div class="car-avatar <%= aCls %>"><svg class="ico" style="width:22px;height:22px"><use href="#<%= iCar %>"/></svg></div>
            <div class="car-info">
                <div class="car-numero">Véhicule&nbsp;<%= num %></div>
                <div class="car-meta">
                    <span class="car-meta-item"><svg class="ico"><use href="#i-repeat"/></svg><%= nbMissions %> mission<%= nbMissions > 1 ? "s" : "" %> à effectuer</span>
                    <span class="meta-dot"></span>
                    <span class="car-meta-item"><svg class="ico"><use href="#i-clock"/></svg><%= nbArretsTotal %> arrêt<%= nbArretsTotal > 1 ? "s" : "" %> prévu<%= nbArretsTotal > 1 ? "s" : "" %></span>
                </div>
            </div>
            <div class="car-badges">
                <span class="badge <%= bCls %>"><svg class="ico"><use href="#<%= iFuel %>"/></svg><%= typeE %></span>
                <span class="badge b-neutral"><svg class="ico"><use href="#i-seat"/></svg>Capacité&nbsp;<%= capa %>&nbsp;places</span>
                <span class="badge b-accent"><svg class="ico"><use href="#i-users"/></svg><%= paxTot %>&nbsp;passagers au total</span>
            </div>
            <div class="car-toggle"><svg class="ico"><use href="#i-chev"/></svg></div>
        </div>

        <div class="car-body">
        <%
           for (Map.Entry<String, List<Planification>> depE : carE.getValue().entrySet()) {
               String hDep = depE.getKey();
               List<Planification> ts = depE.getValue();
               int paxDep = ts.stream().mapToInt(pp -> pp.getPassagersAffectes() != null ? pp.getPassagersAffectes() : pp.getReservation().getNombrePassager()).sum();
        %>
            <div class="dep-group">
                <div class="dep-header">
                    <svg class="ico" style="color:var(--accent)"><use href="#i-clock"/></svg>
                    <span class="dep-time"><%= hDep %></span>
                    <div class="dep-sep"></div>
                    <span class="dep-lbl">Heure de départ du véhicule</span>
                    <span class="dep-count"><%= ts.size() %> arrêt<%= ts.size()>1?"s":"" %> · <%= paxDep %> passager<%= paxDep>1?"s":"" %></span>
                </div>
                <ul class="resa-list">
                <%
                   for (Planification p : ts) {
                       String cNom  = p.getReservation().getClient() != null ? p.getReservation().getClient().getNom() : "N/A";
                       String hNom  = p.getReservation().getHotel()  != null ? p.getReservation().getHotel().getNom()  : "N/A";
                       String hArr  = p.getDateHeure()       != null ? p.getDateHeure().substring(11,16)       : "—";
                       String hRet  = p.getDateHeureRetour() != null ? p.getDateHeureRetour().substring(11,16)  : "—";
                       int pxAff    = p.getPassagersAffectes() != null ? p.getPassagersAffectes() : p.getReservation().getNombrePassager();
                       int pxDem    = p.getPassagersDemandes() != null ? p.getPassagersDemandes() : p.getReservation().getNombrePassager();
                       String dstAH = p.getDistanceAeroportHotel() != null ? String.format("%.1f km", p.getDistanceAeroportHotel()) : null;
                       String dstP  = p.getDistance() != null ? String.format("%.1f km", p.getDistance()) : null;
                       String hPrec = p.getHotelPrecedent() != null ? p.getHotelPrecedent() : null;
                       boolean isFromAirport = (hPrec == null);
                       String originLabel = isFromAirport ? "Aéroport" : hPrec;
                       String pxStatus = pxAff < pxDem ? "part" : (pxAff > capa ? "over" : "ok");
                       String pxMsg    = pxAff < pxDem
                           ? pxAff + " placés sur " + pxDem + " demandés (partiel)"
                           : (pxAff > capa ? pxAff + " passagers — dépasse la capacité !" : pxAff + " passager" + (pxAff>1?"s":"") + " pris en charge");
                       String pxTagMsg = pxAff < pxDem ? "Partiel" : (pxAff > capa ? "Surcharge" : "Complet");
                %>
                <li class="resa-card">

                    <!-- ID réservation -->
                    <div class="rc-id">
                        <div class="resa-num">
                            <svg class="ico"><use href="#i-tag"/></svg>
                            <span class="resa-num-label">Résa.</span>
                            <span class="resa-num-val">#<%= p.getResaId() %></span>
                        </div>
                    </div>

                    <!-- Ligne 1 : nom client -->
                    <div class="rc-main">
                        <div class="client-name">
                            <svg class="ico"><use href="#i-user"/></svg>
                            <%= cNom %>
                        </div>
                    </div>

                    <!-- Ligne 2 : route prise en charge → hôtel -->
                    <div class="rc-route">
                        <div class="route-line">
                            <div class="route-origin">
                                <svg class="ico"><use href="#<%= isFromAirport ? "i-plane" : "i-pin" %>"/></svg>
                                <div class="route-block">
                                    <span class="route-lbl">Prise en charge</span>
                                    <span class="route-name"><%= originLabel %></span>
                                    <% if (dstP != null) { %>
                                    <span class="route-dist"><svg class="ico"><use href="#i-ruler"/></svg>Trajet : <%= dstP %></span>
                                    <% } %>
                                </div>
                            </div>
                            <div class="route-arrow"><svg class="ico"><use href="#i-arrow"/></svg></div>
                            <div class="route-dest">
                                <svg class="ico"><use href="#i-hotel"/></svg>
                                <div class="route-block">
                                    <span class="route-lbl">Destination</span>
                                    <span class="route-name"><%= hNom %></span>
                                    <% if (dstAH != null) { %>
                                    <span class="route-dist"><svg class="ico"><use href="#i-pin"/></svg><%= dstAH %> depuis l'aéroport</span>
                                    <% } %>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Ligne 3 : passagers -->
                    <div class="rc-footer">
                        <div class="pax-info">
                            <svg class="ico" style="color:var(--<%= pxStatus.equals("ok") ? "accent" : pxStatus.equals("part") ? "warning" : "danger" %>)"><use href="#i-users"/></svg>
                            <span class="pax-count pax-<%= pxStatus %>"><%= pxAff %></span>
                            <span class="pax-sep"></span>
                            <span class="pax-detail"><%= pxMsg %></span>
                            <span class="pax-tag <%= pxStatus %>"><%= pxTagMsg %></span>
                        </div>
                    </div>

                    <!-- Horaires (colonne droite) -->
                    <div class="rc-times">
                        <div class="time-block" style="text-align:right">
                            <span class="time-block-lbl">Arrivée vol</span>
                            <span class="time-block-val"><%= hArr %></span>
                            <span class="time-block-sub">heure d'arrivée client</span>
                        </div>
                        <div class="time-block" style="text-align:right">
                            <span class="time-block-lbl">Retour véhicule</span>
                            <span class="time-block-val"><%= hRet %></span>
                            <span class="time-block-sub">fin de mission</span>
                        </div>
                    </div>

                </li>
                <% } %>
                </ul>
            </div>
        <% } %>
        </div>
    </div>
    <% ci++; } } %>

    <!-- Réservations sans voiture -->
    <% if (reservationsSansVoiture != null && !reservationsSansVoiture.isEmpty()) { %>
    <div class="no-car-section">
        <div class="sec-label">
            <h2>Réservations sans véhicule assigné</h2>
            <span class="pill" style="color:var(--danger);border-color:rgba(245,101,101,.18)"><%= totalSansVoiture %></span>
        </div>
        <div class="sec-rule"></div>
        <%
        for (Object item : reservationsSansVoiture) {
            if (item instanceof Reservation) {
                Reservation r = (Reservation) item;
                String rC = r.getClient() != null ? r.getClient().getNom() : "N/A";
                String rH = r.getHotel()  != null ? r.getHotel().getNom()  : "N/A";
                String rT = r.getDateHeureArrivee() != null ? r.getDateHeureArrivee().substring(11,16) : "—";
        %>
        <div class="no-car-card">
            <div class="nc-icon"><svg class="ico" style="width:18px;height:18px"><use href="#i-nocar"/></svg></div>
            <div class="nc-info">
                <div class="nc-lbl">Aucun véhicule disponible</div>
                <div class="nc-client"><%= rC %></div>
                <div class="nc-hotel"><svg class="ico"><use href="#i-hotel"/></svg>Destination : <%= rH %></div>
            </div>
            <div class="nc-right">
                <div class="nc-time">
                    <span class="nc-time-lbl">Arrivée vol</span>
                    <span class="nc-time-val"><%= rT %></span>
                </div>
                <div class="nc-pax"><svg class="ico"><use href="#i-users"/></svg><%= r.getNombrePassager() %> passager<%= r.getNombrePassager() > 1 ? "s" : "" %> à prendre en charge</div>
            </div>
        </div>
        <% } } %>
    </div>
    <% } %>

    <!-- Actions -->
    <div class="actions">
        <a href="<%= request.getContextPath() %>/planification/form" class="btn btn-primary">
            <svg class="ico"><use href="#i-cal"/></svg>Voir une autre date
        </a>
        <a href="<%= request.getContextPath() %>/" class="btn btn-ghost">
            <svg class="ico"><use href="#i-home"/></svg>Accueil
        </a>
    </div>

</main>
<script>
function toggleCar(i) { document.getElementById('car-'+i).classList.toggle('open'); }
</script>
</body>
</html>
