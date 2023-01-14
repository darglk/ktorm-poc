CREATE OR REPLACE FUNCTION func_update_time()
    RETURNS TRIGGER AS
$$
BEGIN
    NEW.update_time := now();
    RETURN NEW;
END;
$$ LANGUAGE 'plpgsql';

ALTER TABLE users ADD COLUMN status VARCHAR(20) DEFAULT 'CREATED';
ALTER TABLE users ADD COLUMN created_at TIMESTAMP(3) WITH TIME ZONE NOT NULL DEFAULT current_timestamp(3);
ALTER TABLE users ADD COLUMN updated_at TIMESTAMP(3) WITH TIME ZONE NOT NULL DEFAULT current_timestamp(3);
UPDATE users SET status = 'CREATED';
ALTER TABLE users ALTER COLUMN status SET NOT NULL;

CREATE TRIGGER update_users_trigger
    BEFORE UPDATE
    ON "users"
    FOR EACH ROW
EXECUTE PROCEDURE func_update_time();