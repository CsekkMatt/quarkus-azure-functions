ALTER TABLE note
ADD COLUMN creationdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP;


UPDATE note
SET creationdate = CURRENT_TIMESTAMP
WHERE creationdate IS NULL;