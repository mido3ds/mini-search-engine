CREATE TABLE IF NOT EXISTS documents(
	url STRING PRIMARY KEY NOT NULL,
	content STRING NOT NULL,
	timeMillis INT NOT NULL,
	indexTimeMillis INT NOT NULL DEFAULT 0,
	counter INT NOT NULL,
	wordCount INT NOT NULL DEFAULT 1,
	rank FLOAT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS keywords(
	word STRING PRIMARY KEY NOT NULL
);

CREATE TABLE IF NOT EXISTS keywords_documents(
	docID INT NOT NULL,
	wordID INT NOT NULL,
	count INT NOT NULL DEFAULT 1,
	PRIMARY KEY (docID, wordID),
	FOREIGN KEY (docID) REFERENCES documents(ROWID)
		ON UPDATE CASCADE
		ON DELETE CASCADE,
	FOREIGN KEY (wordID) REFERENCES keywords(ROWID)
		ON UPDATE CASCADE
		ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS urlstore_queue(
	url STRING PRIMARY KEY NOT NULL
);

CREATE TABLE IF NOT EXISTS outgoing_urls(
	srcURL STRING NOT NULL,
	outURL STRING NOT NULL,
	PRIMARY KEY (srcURL, outURL),
	FOREIGN KEY (srcURL) REFERENCES documents(url)
		ON UPDATE CASCADE
		ON DELETE CASCADE,
	FOREIGN KEY (outURL) REFERENCES documents(url)
		ON UPDATE CASCADE
		ON DELETE CASCADE
);
