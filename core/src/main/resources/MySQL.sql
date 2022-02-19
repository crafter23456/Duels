CREATE TABLE IF NOT EXISTS `players` (
	`player_id`   INT(6)       NOT NULL AUTO_INCREMENT,
	`uuid`        VARCHAR(255) NOT NULL,
	`player_name` VARCHAR(255) NOT NULL,
	`kills`       INT(6)       NOT NULL DEFAULT 0,
	`deaths`      INT(6)       NOT NULL DEFAULT 0,
	`wins`        INT(6)       NOT NULL DEFAULT 0,
	`lose`        INT(6)       NOT NULL DEFAULT 0,
	`winStreak`   INT(6)       NOT NULL DEFAULT 0,
	`bestStreak`  INT(6)       NOT NULL DEFAULT 0,
	`score`       INT(6)       NOT NULL DEFAULT 0,
	`coin`        INT(6)       NOT NULL DEFAULT 0,
	`xp`          INT(6)       NOT NULL DEFAULT 0,
	`kitSelected` VARCHAR(255) NOT NULL DEFAULT '',
	PRIMARY KEY (`player_id`)
)
ENGINE = InnoDB
DEFAULT CHARSET = utf8;
