CREATE TABLE IF NOT EXISTS `im_channel` (
  `id` char(32) NOT NULL,
  `name` varchar(64) NOT NULL,
  `type` char(1) NOT NULL,
  `purpose` varchar(256) DEFAULT NULL,
  `create_at` bigint(20) unsigned NOT NULL,
  `delete_at` bigint(20) unsigned NOT NULL DEFAULT '0',
  `last_post_at` bigint(20) unsigned NOT NULL DEFAULT '0',
  `member_count` mediumint(8) unsigned NOT NULL DEFAULT '0',
  `creator_id` char(32) NOT NULL DEFAULT '0',
  `from_user_id` char(32) DEFAULT '0',
  `to_user_id` char(32) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `im_channel_member` (
  `channel_id` char(32) NOT NULL,
  `user_id` char(32) NOT NULL,
  `is_admin` tinyint(3) unsigned NOT NULL,
  PRIMARY KEY (`channel_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `im_file` (
  `id` char(32) NOT NULL,
  `name` varchar(128) NOT NULL,
  `extension` varchar(32) NOT NULL,
  `size` int(11) NOT NULL,
  `mime_typ` varchar(256) NOT NULL,
  `width` smallint(6) NOT NULL,
  `height` smallint(6) NOT NULL,
  `path` varchar(128) NOT NULL,
  `thumb_width` smallint(6) DEFAULT NULL,
  `thumb_height` smallint(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `im_hide_channel` (
  `user_id` char(32) NOT NULL,
  `channel_id` char(32) NOT NULL,
  PRIMARY KEY (`user_id`,`channel_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `im_message` (
  `id` bigint(20) unsigned NOT NULL,
  `channel_id` char(32) COLLATE utf8mb4_bin NOT NULL,
  `sender_id` char(32) COLLATE utf8mb4_bin NOT NULL,
  `create_at` bigint(20) NOT NULL,
  `type` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL,
  `content` varchar(3000) COLLATE utf8mb4_bin DEFAULT NULL,
  `delete_at` bigint(20) NOT NULL DEFAULT '0',
  `file_id` char(32) COLLATE utf8mb4_bin DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `im_unread_message_count` (
  `user_id` char(32) NOT NULL,
  `channel_id` char(32) NOT NULL,
  `total` smallint(5) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`user_id`,`channel_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `im_user` (
  `id` char(32) NOT NULL,
  `name` varchar(32) NOT NULL,
  `name_first_letter` char(1) NOT NULL,
  `nickname` varchar(32) NOT NULL,
  `salt` varchar(64) NOT NULL,
  `password` varchar(64) NOT NULL,
  `locked` tinyint(4) NOT NULL DEFAULT '0',
  `avatar_url` varchar(512) DEFAULT NULL,
  `created_at` datetime NOT NULL,
  `last_post_at` bigint(20) DEFAULT NULL,
  `online_status` varchar(7) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `im_user_channel` (
  `user_id` char(32) NOT NULL,
  `channel_id` char(32) NOT NULL,
  `display_name` varchar(64) NOT NULL,
  `to_user_id` char(32) DEFAULT NULL,
  PRIMARY KEY (`user_id`,`channel_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `im_user` (`id`, `name`, `name_first_letter`, `nickname`, `salt`, `password`, `locked`, `avatar_url`, `created_at`, `last_post_at`, `online_status`) VALUES
    ('00000000000000000000000000000000', 'leo', 'l', '系统用户', 'MOlssyhqweLKffidserewr==', 'FDFGHTY33456FDHG000FDEKKKLLLPP', 0, NULL, '2018-06-16 21:00:00', NULL, NULL);
