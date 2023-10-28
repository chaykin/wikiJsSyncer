BEGIN TRANSACTION;
CREATE TABLE IF NOT EXISTS "pages" (
	"id"                INTEGER NOT NULL UNIQUE,
	"title"             TEXT,
	"description"       TEXT,
	"locale"            TEXT NOT NULL,
	"remote_path"       TEXT NOT NULL UNIQUE,
	"local_path"        TEXT NOT NULL UNIQUE,
	"content_type"      TEXT NOT NULL,
	"remote_update_at"  INTEGER NOT NULL,
	"md5_hash"          TEXT NOT NULL,
	"tags"              TEXT,
	PRIMARY KEY("id")
);

CREATE TABLE IF NOT EXISTS "conflicts" (
	"id"	INTEGER NOT NULL UNIQUE,
	"local_path_mine"	TEXT NOT NULL UNIQUE,
	"local_path_theirs" TEXT NOT NULL UNIQUE,
	"remote_path"       TEXT NOT NULL UNIQUE,
	"remote_update_at"  INTEGER NOT NULL,
	"remote_md5_hash"   TEXT NOT NULL,
	"remote_tags"       TEXT,
	FOREIGN KEY("id") REFERENCES "pages"("id") ON DELETE CASCADE,
	PRIMARY KEY("id")
);
COMMIT;
