BEGIN TRANSACTION;
CREATE TABLE IF NOT EXISTS "pages" (
	"id"	INTEGER NOT NULL UNIQUE,
	"title" TEXT,
	"description"   TEXT,
	"locale"    TEXT NOT NULL,
	"remote_path"	TEXT NOT NULL UNIQUE,
	"local_path"	TEXT NOT NULL UNIQUE,
	"content_type"	TEXT NOT NULL,
	"remote_update_at"	INTEGER NOT NULL,
	"md5_hash"	TEXT NOT NULL,
	"tags"	TEXT,
	PRIMARY KEY("id")
);
COMMIT;