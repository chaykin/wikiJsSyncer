BEGIN TRANSACTION;
CREATE TABLE IF NOT EXISTS "pages" (
	"id"	INTEGER NOT NULL UNIQUE,
	"title"	TEXT NOT NULL,
	"locale"	TEXT NOT NULL,
	"remote_path"	TEXT NOT NULL UNIQUE,
	"local_path"	TEXT NOT NULL UNIQUE,
	"content_type"	TEXT NOT NULL,
	"remote_update_at"	INTEGER NOT NULL,
	"local_update_at"	INTEGER NOT NULL,
	"tags"	TEXT,
	PRIMARY KEY("id")
);
COMMIT;