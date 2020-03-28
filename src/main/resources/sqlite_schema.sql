create TABLE IF NOT EXISTS documents(
	url STRING PRIMARY KEY NOT NULL,
	content STRING NOT NULL,
	timeMillis INT NOT NULL
);

