BEGIN TRANSACTION;
CREATE TABLE IF NOT EXISTS "pages" (
	"id"                INTEGER NOT NULL UNIQUE,
	"title"             TEXT,
	"description"       TEXT,
	"locale"            TEXT NOT NULL,
	"server_path"       TEXT NOT NULL UNIQUE,
	"local_path"        TEXT NOT NULL UNIQUE,
	"content_type"      TEXT NOT NULL,
	"server_update_at"  INTEGER NOT NULL,
	"md5_hash"          TEXT NOT NULL,
	"tags"              TEXT,
	PRIMARY KEY("id")
);

CREATE TABLE IF NOT EXISTS "assets" (
	"id"                INTEGER NOT NULL UNIQUE,
	"folderId"          INTEGER NOT NULL,
	"server_path"       TEXT NOT NULL UNIQUE,
	"local_path"        TEXT NOT NULL UNIQUE,
	"content_type"      TEXT NOT NULL,
	"server_update_at"  INTEGER NOT NULL,
	"md5_hash"          TEXT NOT NULL,
	PRIMARY KEY("id")
);

CREATE TABLE IF NOT EXISTS "conflicts" (
	"id"	INTEGER NOT NULL UNIQUE,
	"local_path_mine"	TEXT NOT NULL UNIQUE,
	"local_path_theirs" TEXT NOT NULL UNIQUE,
	"server_path"       TEXT NOT NULL UNIQUE,
	"server_update_at"  INTEGER NOT NULL,
	"server_md5_hash"   TEXT NOT NULL,
	"server_tags"       TEXT,
	FOREIGN KEY("id") REFERENCES "pages"("id") ON DELETE CASCADE,
	PRIMARY KEY("id")
);
COMMIT;
