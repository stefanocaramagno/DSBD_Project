-- Aggiunta delle colonne per le soglie sugli interessi utente-aeroporto

ALTER TABLE user_airport_interest
    ADD COLUMN IF NOT EXISTS high_value INTEGER NULL,
    ADD COLUMN IF NOT EXISTS low_value  INTEGER NULL;
