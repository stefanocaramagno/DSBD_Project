-- Seed iniziale degli aeroporti monitorabili (codici ICAO)

INSERT INTO airports (code, name, city, country, timezone) VALUES
  ('LICC', 'Catania Fontanarossa', 'Catania', 'Italy', 'Europe/Rome'),
  ('LIRF', 'Roma Fiumicino',       'Roma',    'Italy', 'Europe/Rome'),
  ('LIMC', 'Milano Malpensa',      'Milano',  'Italy', 'Europe/Rome'),
  ('LIML', 'Milano Linate',        'Milano',  'Italy', 'Europe/Rome');
